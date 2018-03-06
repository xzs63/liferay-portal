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

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormAdminRequestHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Marcellus Tavares
 */
public class DDMFormViewFormInstanceRecordDisplayContext {

	public DDMFormViewFormInstanceRecordDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DDMFormInstanceRecordLocalService formInstanceRecordLocalService,
		DDMFormRenderer formRenderer, DDMFormValuesFactory formValuesFactory,
		DDMFormValuesMerger formValuesMerger) {

		_httpServletResponse = httpServletResponse;
		_ddmFormInstanceRecordLocalService = formInstanceRecordLocalService;
		_ddmFormRenderer = formRenderer;
		_ddmFormValuesFactory = formValuesFactory;
		_ddmFormValuesMerger = formValuesMerger;

		_ddmFormAdminRequestHelper = new DDMFormAdminRequestHelper(
			httpServletRequest);
	}

	public String getDDMFormHTML(RenderRequest renderRequest)
		throws PortalException {

		DDMFormInstanceRecord formInstanceRecord = getDDMFormInstanceRecord();

		DDMFormInstance formInstance = formInstanceRecord.getFormInstance();

		DDMStructure structure = formInstance.getStructure();

		DDMFormInstanceVersion formInstanceVersion =
			formInstance.getFormInstanceVersion(
				formInstanceRecord.getFormInstanceVersion());

		DDMStructureVersion structureVersion =
			formInstanceVersion.getStructureVersion();

		DDMForm currentForm = structureVersion.getDDMForm();

		DDMFormValues formValues = _ddmFormValuesFactory.create(
			renderRequest, currentForm);

		formValues = _ddmFormValuesMerger.merge(
			formInstanceRecord.getDDMFormValues(), formValues);

		DDMFormRenderingContext formRenderingContext =
			createDDMFormRenderingContext(structure.getDDMForm());

		formRenderingContext.setDDMFormValues(formValues);

		updateDDMFormFields(currentForm, structure.getDDMForm());

		DDMFormLayout formLayout = structureVersion.getDDMFormLayout();

		return _ddmFormRenderer.render(
			currentForm, formLayout, formRenderingContext);
	}

	protected DDMFormRenderingContext createDDMFormRenderingContext(
		DDMForm ddmForm) {

		DDMFormRenderingContext formRenderingContext =
			new DDMFormRenderingContext();

		formRenderingContext.setHttpServletRequest(
			_ddmFormAdminRequestHelper.getRequest());
		formRenderingContext.setHttpServletResponse(_httpServletResponse);

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Locale locale = ddmForm.getDefaultLocale();

		if (availableLocales.contains(_ddmFormAdminRequestHelper.getLocale())) {
			locale = _ddmFormAdminRequestHelper.getLocale();
		}

		formRenderingContext.setLocale(locale);

		formRenderingContext.setPortletNamespace(
			PortalUtil.getPortletNamespace(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN));
		formRenderingContext.setReadOnly(true);

		return formRenderingContext;
	}

	protected DDMFormInstanceRecord getDDMFormInstanceRecord()
		throws PortalException {

		HttpServletRequest httpServletRequest =
			_ddmFormAdminRequestHelper.getRequest();

		long formInstanceRecordId = ParamUtil.getLong(
			httpServletRequest, "formInstanceRecordId");

		if (formInstanceRecordId > 0) {
			return _ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				formInstanceRecordId);
		}

		DDMFormInstanceRecord formInstanceRecord =
			(DDMFormInstanceRecord)
				httpServletRequest.getAttribute(
					DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_RECORD);

		return formInstanceRecord;
	}

	protected boolean isDDMFormFieldRemoved(
		Map<String, DDMFormField> latestFormFieldMap, String fieldName) {

		if (latestFormFieldMap.containsKey(fieldName)) {
			return false;
		}

		return true;
	}

	protected void setDDMFormFieldRemovedLabel(DDMFormField formField) {
		Locale locale = _ddmFormAdminRequestHelper.getLocale();

		LocalizedValue label = formField.getLabel();

		String labelString = label.getString(locale);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		label.addString(
			locale,
			LanguageUtil.format(
				resourceBundle, "x-removed", labelString, false));
	}

	protected void updateDDMFormField(
		Map<String, DDMFormField> latestFormFieldMap, DDMFormField formField) {

		boolean removed = isDDMFormFieldRemoved(
			latestFormFieldMap, formField.getName());

		if (removed) {
			setDDMFormFieldRemovedLabel(formField);
		}

		formField.setReadOnly(true);

		// Nested fields

		for (DDMFormField nestedFormField :
				formField.getNestedDDMFormFields()) {

			updateDDMFormField(latestFormFieldMap, nestedFormField);
		}
	}

	protected void updateDDMFormFields(
		DDMForm currentForm, DDMForm latestForm) {

		Map<String, DDMFormField> latestDDMFormFieldMap =
			latestForm.getDDMFormFieldsMap(true);

		for (DDMFormField formField : currentForm.getDDMFormFields()) {
			updateDDMFormField(latestDDMFormFieldMap, formField);
		}
	}

	private final DDMFormAdminRequestHelper _ddmFormAdminRequestHelper;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private final HttpServletResponse _httpServletResponse;

}