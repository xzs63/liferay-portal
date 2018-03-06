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

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.kernel.workflow.WorkflowException;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Jeyvison Nascimento
 */
public abstract class BaseWorkflowDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			doProcessAction(actionRequest, actionResponse);

			addSuccessMessage(actionRequest, actionResponse);

			return SessionErrors.isEmpty(actionRequest);
		}
		catch (WorkflowException we) {
			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(actionRequest, we.getClass(), we);

			return false;
		}
		catch (PortletException pe) {
			throw pe;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String successMessage = getSuccessMessage(actionRequest);

		SessionMessages.add(actionRequest, "requestProcessed", successMessage);
	}

	@Override
	protected abstract void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	protected ResourceBundle getResourceBundle(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		return resourceBundleLoader.loadResourceBundle(locale);
	}

	protected String getSuccessMessage(ActionRequest actionRequest) {
		ResourceBundle resourceBundle = getResourceBundle(actionRequest);

		return LanguageUtil.get(
			resourceBundle, "workflow-updated-successfully");
	}

	protected String getTitle(Map<Locale, String> titleMap) {
		if (titleMap == null) {
			return null;
		}

		String value = StringPool.BLANK;

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);
			String title = titleMap.get(locale);

			if (Validator.isNotNull(title)) {
				value = LocalizationUtil.updateLocalization(
					value, "Title", title, languageId);
			}
			else {
				value = LocalizationUtil.removeLocalization(
					value, "Title", languageId);
			}
		}

		return value;
	}

	@Reference(
		target = "(bundle.symbolic.name=com.liferay.portal.workflow.web)",
		unbind = "-"
	)
	protected void setResourceBundleLoader(
		ResourceBundleLoader resourceBundleLoader) {

		this.resourceBundleLoader = resourceBundleLoader;
	}

	@Reference
	protected Portal portal;

	protected ResourceBundleLoader resourceBundleLoader;

	@Reference
	protected WorkflowDefinitionManager workflowDefinitionManager;

}