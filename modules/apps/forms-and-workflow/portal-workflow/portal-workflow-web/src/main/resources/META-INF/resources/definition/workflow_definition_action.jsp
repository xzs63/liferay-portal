<%--
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
--%>

<%@ include file="/definition/init.jsp" %>

<%
String randomNamespace = StringUtil.randomId() + StringPool.UNDERLINE;

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

WorkflowDefinition workflowDefinition = (WorkflowDefinition)row.getObject();

String duplicateTitle = workflowDefinitionDisplayContext.getDuplicateTitle(workflowDefinition);
%>

<liferay-portlet:actionURL name="duplicateWorkflowDefinition" var="duplicateWorkflowDefinition">
	<portlet:param name="mvcPath" value="/definition/edit_workflow_definition.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</liferay-portlet:actionURL>

<liferay-ui:icon-menu direction="left-side" icon="<%= StringPool.BLANK %>" markupView="lexicon" message="<%= StringPool.BLANK %>" showWhenSingleIcon="<%= true %>">
	<portlet:renderURL var="viewURL">
		<portlet:param name="mvcPath" value="/definition/view_workflow_definition.jsp" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="name" value="<%= workflowDefinition.getName() %>" />
		<portlet:param name="version" value="<%= String.valueOf(workflowDefinition.getVersion()) %>" />
	</portlet:renderURL>

	<liferay-ui:icon
		message="view"
		url="<%= viewURL %>"
	/>

	<portlet:renderURL var="editURL">
		<portlet:param name="mvcPath" value="/definition/edit_workflow_definition.jsp" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="name" value="<%= workflowDefinition.getName() %>" />
		<portlet:param name="version" value="<%= String.valueOf(workflowDefinition.getVersion()) %>" />
	</portlet:renderURL>

	<liferay-ui:icon
		message="edit"
		url="<%= editURL %>"
	/>

	<c:choose>
		<c:when test="<%= workflowDefinition.isActive() %>">
			<liferay-portlet:actionURL name="deactivateWorkflowDefinition" var="deactivateWorkflowDefinitionURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="name" value="<%= workflowDefinition.getName() %>" />
				<portlet:param name="version" value="<%= String.valueOf(workflowDefinition.getVersion()) %>" />
			</liferay-portlet:actionURL>

			<liferay-ui:icon
				message="unpublish"
				url="<%= deactivateWorkflowDefinitionURL %>"
			/>

			<liferay-ui:icon
				id='<%= "duplicate" + workflowDefinition.getName() %>'
				message="duplicate"
				url="javascript:;"
			/>
		</c:when>
		<c:otherwise>
			<liferay-portlet:actionURL name="deleteWorkflowDefinition" var="deleteWorkflowDefinitionURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="name" value="<%= workflowDefinition.getName() %>" />
				<portlet:param name="version" value="<%= String.valueOf(workflowDefinition.getVersion()) %>" />
			</liferay-portlet:actionURL>

			<liferay-ui:icon
				message="delete"
				onClick='<%= renderResponse.getNamespace() + "confirmDeleteDefinition('" + deleteWorkflowDefinitionURL + "'); return false;" %>'
				url="<%= deleteWorkflowDefinitionURL %>"
			/>
		</c:otherwise>
	</c:choose>
</liferay-ui:icon-menu>

<div class="hide" id="<%= randomNamespace %>titleInputLocalized">
	<aui:col>
		<aui:field-wrapper label="title">
			<liferay-ui:input-localized name="title" xml="<%= duplicateTitle %>" />
		</aui:field-wrapper>
	</aui:col>

	<aui:col>
		<liferay-ui:message key="copy-does-not-include-revisions" />
	</aui:col>
</div>

<div class="hide" id="<%= randomNamespace %>contentInput">
	<aui:input name="name" type="hidden" value="<%= PortalUUIDUtil.generate() %>" />
	<aui:input name="content" type="hidden" value="<%= workflowDefinition.getContent() %>" />
	<aui:input name="duplicatedDefinitionTitle" type="hidden" value="<%= workflowDefinition.getTitle(LanguageUtil.getLanguageId(request)) %>" />
</div>

<aui:script use="liferay-workflow-web">
	var title = '<liferay-ui:message key="duplicate-workflow" />';

	var confirmBeforeDuplicateDialog = A.rbind('confirmBeforeDuplicateDialog', Liferay.WorkflowWeb, '<%= duplicateWorkflowDefinition %>', title, '<%= randomNamespace %>');

	Liferay.delegateClick('<portlet:namespace />duplicate<%= workflowDefinition.getName() %>', confirmBeforeDuplicateDialog);
</aui:script>