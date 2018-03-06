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

package com.liferay.adaptive.media.image.internal.exportimport.content.processor;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Alejandro Tardín
 */
@Component(property = {"adaptive.media.format=html"})
public class AMImageHTMLExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		String content, boolean exportReferencedContent,
		boolean escapeContent) {

		AMReferenceExporter amReferenceExporter = new AMReferenceExporter(
			portletDataContext, stagedModel, exportReferencedContent);

		return _replace(content, amReferenceExporter);
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		AMEmbeddedReferenceSet amEmbeddedReferenceSet =
			_amEmbeddedReferenceSetFactory.create(
				portletDataContext, stagedModel);

		return _replace(content, amEmbeddedReferenceSet);
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		Document document = _parseDocument(content);

		for (Element element : document.select("[data-fileEntryId]")) {
			long fileEntryId = Long.valueOf(element.attr("data-fileEntryId"));

			_dlAppLocalService.getFileEntry(fileEntryId);
		}
	}

	@Reference(unbind = "-")
	protected void setAMEmbeddedReferenceSetFactory(
		AMEmbeddedReferenceSetFactory amEmbeddedReferenceSetFactory) {

		_amEmbeddedReferenceSetFactory = amEmbeddedReferenceSetFactory;
	}

	@Reference(unbind = "-")
	protected void setAMImageHTMLTagFactory(
		AMImageHTMLTagFactory amImageHTMLTagFactory) {

		_amImageHTMLTagFactory = amImageHTMLTagFactory;
	}

	@Reference(unbind = "-")
	protected void setDLAppLocalService(DLAppLocalService dlAppLocalService) {
		_dlAppLocalService = dlAppLocalService;
	}

	private FileEntry _getFileEntry(long fileEntryId) {
		try {
			return _dlAppLocalService.getFileEntry(fileEntryId);
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn(pe, pe);
			}

			return null;
		}
	}

	private Document _parseDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);
		outputSettings.syntax(Document.OutputSettings.Syntax.xml);

		document.outputSettings(outputSettings);

		return document;
	}

	private Element _parseNode(String tag) {
		Document document = _parseDocument(tag);

		Element bodyElement = document.body();

		return bodyElement.child(0);
	}

	private String _replace(
			String content, AMEmbeddedReferenceSet amEmbeddedReferenceSet)
		throws PortalException {

		Document document = _parseDocument(content);

		Elements elements = document.getElementsByAttribute(
			_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

		for (Element element : elements) {
			String path = element.attr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

			if (!amEmbeddedReferenceSet.containsReference(path)) {
				continue;
			}

			long fileEntryId = amEmbeddedReferenceSet.importReference(path);

			FileEntry fileEntry = _getFileEntry(fileEntryId);

			if (fileEntry == null) {
				continue;
			}

			element.attr("data-fileEntryId", String.valueOf(fileEntryId));
			element.removeAttr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

			if ("picture".equals(element.tagName())) {
				Elements imgElements = element.getElementsByTag("img");

				Element imgElement = imgElements.first();

				imgElement.removeAttr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

				Element picture = _parseNode(
					_amImageHTMLTagFactory.create(
						imgElement.toString(), fileEntry));

				element.html(picture.html());
			}
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private String _replace(
		String content, AMReferenceExporter amReferenceExporter) {

		Document document = _parseDocument(content);

		for (Element element : document.select("[data-fileEntryId]")) {
			long fileEntryId = Long.valueOf(element.attr("data-fileEntryId"));

			try {
				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					fileEntryId);

				amReferenceExporter.exportReference(fileEntry);

				element.removeAttr("data-fileEntryId");
				element.attr(
					_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH,
					ExportImportPathUtil.getModelPath(fileEntry));
			}
			catch (PortalException pe) {
				if (_log.isDebugEnabled()) {
					_log.debug(pe, pe);
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(pe.getMessage());
				}
			}
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private static final String _ATTRIBUTE_NAME_EXPORT_IMPORT_PATH =
		"export-import-path";

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageHTMLExportImportContentProcessor.class);

	private AMEmbeddedReferenceSetFactory _amEmbeddedReferenceSetFactory;
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;
	private DLAppLocalService _dlAppLocalService;

}