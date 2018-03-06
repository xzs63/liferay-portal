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

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.workflow.web.internal.constants.WorkflowPortletKeys;

import java.util.ResourceBundle;

import javax.portlet.ActionRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"mvc.command.name=deactivateWorkflowDefinition"
	},
	service = MVCActionCommand.class
)
public class DeactivateWorkflowDefinitionMVCActionCommand
	extends RestoreWorkflowDefinitionMVCActionCommand {

	@Override
	protected String getSuccessMessage(ActionRequest actionRequest) {
		ResourceBundle resourceBundle = getResourceBundle(actionRequest);

		return LanguageUtil.get(
			resourceBundle, "workflow-unpublished-successfully");
	}

	@Override
	protected boolean isActive() {
		return false;
	}

}