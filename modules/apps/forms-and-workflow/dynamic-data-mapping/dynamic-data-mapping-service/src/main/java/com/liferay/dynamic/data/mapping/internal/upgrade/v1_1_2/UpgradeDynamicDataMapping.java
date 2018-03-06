/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_2;

import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueTransformer;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesTransformer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.AutoBatchPreparedStatementUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Rafael Praxedes
 */
public class UpgradeDynamicDataMapping extends UpgradeProcess {

	public UpgradeDynamicDataMapping(
		DDMFormJSONDeserializer ddmFormJSONDeserializer,
		DDMFormJSONSerializer ddmFormJSONSerializer,
		DDMFormValuesJSONDeserializer ddmFormValuesJSONDeserializer,
		DDMFormValuesJSONSerializer ddmFormValuesJSONSerializer,
		JSONFactory jsonFactory) {

		_ddmFormJSONDeserializer = ddmFormJSONDeserializer;
		_ddmFormJSONSerializer = ddmFormJSONSerializer;
		_ddmFormValuesJSONDeserializer = ddmFormValuesJSONDeserializer;
		_ddmFormValuesJSONSerializer = ddmFormValuesJSONSerializer;
		_jsonFactory = jsonFactory;
	}

	protected String convertJSONArrayToString(String value) {
		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray(value);

			if (jsonArray.length() == 0) {
				return StringPool.BLANK;
			}

			return jsonArray.getString(0);
		}
		catch (JSONException jsone) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsone, jsone);
			}

			return value;
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeDDMStructureReferences();

		upgradeDDLDDMContentReferences();
		upgradeDLDDMContentReferences();
	}

	protected DDMForm getDDMForm(long structureId) throws Exception {
		DDMForm ddmForm = _ddmForms.get(structureId);

		if (ddmForm != null) {
			return ddmForm;
		}

		try (PreparedStatement ps = connection.prepareStatement(
				"select definition from DDMStructure where structureId = ?")) {

			ps.setLong(1, structureId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					ddmForm = _ddmFormJSONDeserializer.deserialize(
						rs.getString("definition"));

					_ddmForms.put(structureId, ddmForm);

					return ddmForm;
				}
			}
		}

		throw new UpgradeException(
			"Unable to find dynamic data mapping structure with ID " +
				structureId);
	}

	protected DDMForm getFullHierarchyDDMForm(long structureId)
		throws Exception {

		DDMForm fullHierarchyDDMForm = _fullHierarchyDDMForms.get(structureId);

		if (fullHierarchyDDMForm != null) {
			return fullHierarchyDDMForm;
		}

		try (PreparedStatement ps = connection.prepareStatement(
				"select parentStructureId from DDMStructure where " +
					"structureId = ?")) {

			ps.setLong(1, structureId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					long parentStructureId = rs.getLong("parentStructureId");

					fullHierarchyDDMForm = getDDMForm(structureId);

					if (parentStructureId > 0) {
						DDMForm parentDDMForm = getFullHierarchyDDMForm(
							parentStructureId);

						List<DDMFormField> ddmFormFields =
							fullHierarchyDDMForm.getDDMFormFields();

						ddmFormFields.addAll(parentDDMForm.getDDMFormFields());
					}

					_fullHierarchyDDMForms.put(
						structureId, fullHierarchyDDMForm);

					return fullHierarchyDDMForm;
				}
			}
		}

		throw new UpgradeException(
			"Unable to find dynamic data mapping structure with ID " +
				structureId);
	}

	protected void transformDDMFormFieldValues(DDMFormValues ddmFormValues)
		throws Exception {

		DDMFormValuesTransformer ddmFormValuesTransformer =
			new DDMFormValuesTransformer(ddmFormValues);

		ddmFormValuesTransformer.addTransformer(
			new RadioDDMFormFieldValueTransformer());
		ddmFormValuesTransformer.addTransformer(
			new SelectDDMFormFieldValueTransformer());

		ddmFormValuesTransformer.transform();
	}

	protected void updateDDMFormField(DDMFormField ddmFormField)
		throws Exception {

		String type = ddmFormField.getType();

		if (type.equals("radio")) {
			LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

			for (Locale locale : predefinedValue.getAvailableLocales()) {
				String valueString = predefinedValue.getString(locale);

				if (Validator.isNull(valueString)) {
					continue;
				}

				predefinedValue.addString(
					locale, convertJSONArrayToString(valueString));
			}
		}
	}

	protected void updateDDMFormFields(DDMForm ddmForm) throws Exception {
		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (Entry<String, DDMFormField> entry : ddmFormFieldsMap.entrySet()) {
			updateDDMFormField(entry.getValue());
		}
	}

	protected void upgradeDDLDDMContentReferences() throws Exception {
		StringBundler sb = new StringBundler(7);

		sb.append("select DDMContent.contentId, DDMContent.data_, ");
		sb.append("DDMStructure.structureId from DDLRecordVersion inner join ");
		sb.append("DDLRecordSet on DDLRecordVersion.recordSetId = ");
		sb.append("DDLRecordSet.recordSetId inner join DDMContent on  ");
		sb.append("DDLRecordVersion.DDMStorageId = DDMContent.contentId ");
		sb.append("inner join DDMStructure on DDLRecordSet.DDMStructureId = ");
		sb.append("DDMStructure.structureId");

		upgradeDDMContentReferences(sb.toString());
	}

	protected void upgradeDDMContentReferences(String query) throws Exception {
		try (PreparedStatement ps1 = connection.prepareStatement(query);
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMContent set data_= ? where contentId = ?");
			ResultSet rs = ps1.executeQuery()) {

			while (rs.next()) {
				long contentId = rs.getLong("contentId");
				String data = rs.getString("data_");
				long ddmStructureId = rs.getLong("structureId");

				DDMForm ddmForm = getFullHierarchyDDMForm(ddmStructureId);

				DDMFormValues ddmFormValues =
					_ddmFormValuesJSONDeserializer.deserialize(ddmForm, data);

				transformDDMFormFieldValues(ddmFormValues);

				ps2.setString(
					1, _ddmFormValuesJSONSerializer.serialize(ddmFormValues));

				ps2.setLong(2, contentId);

				ps2.addBatch();
			}

			ps2.executeBatch();
		}
	}

	protected void upgradeDDMStructureReferences() throws Exception {
		try (PreparedStatement ps1 = connection.prepareStatement(
				"select DDMStructure.structureId from DDMStructure");
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			ResultSet rs = ps1.executeQuery()) {

			while (rs.next()) {
				long ddmStructureId = rs.getLong("structureId");

				DDMForm ddmForm = getDDMForm(ddmStructureId);

				updateDDMFormFields(ddmForm);

				ps2.setString(1, _ddmFormJSONSerializer.serialize(ddmForm));

				ps2.setLong(2, ddmStructureId);

				ps2.addBatch();
			}

			ps2.executeBatch();
		}
	}

	protected void upgradeDLDDMContentReferences() throws Exception {
		StringBundler sb = new StringBundler(9);

		sb.append("select DDMContent.contentId, DDMContent.data_,");
		sb.append("DDMStructure.structureId from DLFileEntryMetadata inner ");
		sb.append("join DDMContent on DLFileEntryMetadata.DDMStorageId = ");
		sb.append("DDMContent.contentId inner join DDMStructure on ");
		sb.append("DLFileEntryMetadata.DDMStructureId = DDMStructure.");
		sb.append("structureId inner join DLFileVersion on ");
		sb.append("DLFileEntryMetadata.fileVersionId = DLFileVersion.");
		sb.append("fileVersionId and DLFileEntryMetadata.fileEntryId = ");
		sb.append("DLFileVersion.fileEntryId");

		upgradeDDMContentReferences(sb.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeDynamicDataMapping.class);

	private final DDMFormJSONDeserializer _ddmFormJSONDeserializer;
	private final DDMFormJSONSerializer _ddmFormJSONSerializer;
	private final Map<Long, DDMForm> _ddmForms = new HashMap<>();
	private final DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer;
	private final DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer;
	private final Map<Long, DDMForm> _fullHierarchyDDMForms = new HashMap<>();
	private final JSONFactory _jsonFactory;

	private class RadioDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		@Override
		public String getFieldType() {
			return "radio";
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				if (Validator.isNull(valueString)) {
					continue;
				}

				value.addString(locale, convertJSONArrayToString(valueString));
			}
		}

	}

	private class SelectDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		@Override
		public String getFieldType() {
			return "select";
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			for (Locale locale : value.getAvailableLocales()) {
				String valueString = value.getString(locale);

				JSONArray jsonArray = convertToJSONArray(valueString);

				value.addString(locale, jsonArray.toString());
			}
		}

		protected JSONArray convertToJSONArray(String value) {
			if (Validator.isNull(value)) {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				return jsonArray;
			}

			try {
				JSONArray jsonArray = _jsonFactory.createJSONArray(value);

				return jsonArray;
			}
			catch (Exception e) {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				jsonArray.put(value);

				return jsonArray;
			}
		}

	}

}