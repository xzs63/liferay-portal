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

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL");

if (Validator.isNull(backURL)) {
	backURL = PortalUtil.getLayoutFullURL(layoutsAdminDisplayContext.getSelLayout(), themeDisplay);
}

String portletResource = ParamUtil.getString(request, "portletResource");

Group selGroup = (Group)request.getAttribute(WebKeys.GROUP);

Group group = layoutsAdminDisplayContext.getGroup();

Layout selLayout = layoutsAdminDisplayContext.getSelLayout();

PortletURL redirectURL = layoutsAdminDisplayContext.getRedirectURL();

long refererPlid = ParamUtil.getLong(request, "refererPlid", LayoutConstants.DEFAULT_PLID);

Set<Long> parentPlids = new HashSet<Long>();

long parentPlid = refererPlid;

while (parentPlid > 0) {
	try {
		Layout parentLayout = LayoutLocalServiceUtil.getLayout(parentPlid);

		if (parentLayout.isRootLayout()) {
			break;
		}

		parentPlid = parentLayout.getParentPlid();

		parentPlids.add(parentPlid);
	}
	catch (Exception e) {
		break;
	}
}

LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(selLayout);

String layoutSetBranchName = StringPool.BLANK;

boolean incomplete = false;

if (layoutRevision != null) {
	long layoutSetBranchId = layoutRevision.getLayoutSetBranchId();

	incomplete = StagingUtil.isIncomplete(selLayout, layoutSetBranchId);

	if (incomplete) {
		LayoutSetBranch layoutSetBranch = LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(layoutSetBranchId);

		layoutSetBranchName = layoutSetBranch.getName();

		if (LayoutSetBranchConstants.MASTER_BRANCH_NAME.equals(layoutSetBranchName)) {
			layoutSetBranchName = LanguageUtil.get(request, layoutSetBranchName);
		}

		portletDisplay.setShowStagingIcon(false);
	}
}

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}

renderResponse.setTitle(selLayout.getName(locale));
%>

<c:choose>
	<c:when test="<%= incomplete %>">
		<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(selLayout.getName(locale)), HtmlUtil.escape(layoutSetBranchName)} %>" key="the-page-x-is-not-enabled-in-x,-but-is-available-in-other-pages-variations" translateArguments="<%= false %>" />

		<aui:button-row>
			<aui:button id="enableLayoutButton" name="enableLayout" value='<%= LanguageUtil.format(request, "enable-in-x", HtmlUtil.escape(layoutSetBranchName), false) %>' />

			<portlet:actionURL name="/layout/enable_layout" var="enableLayoutURL">
				<portlet:param name="mvcPath" value="/view.jsp" />
				<portlet:param name="redirect" value="<%= redirectURL.toString() %>" />
				<portlet:param name="incompleteLayoutRevisionId" value="<%= String.valueOf(layoutRevision.getLayoutRevisionId()) %>" />
			</portlet:actionURL>

			<aui:script use="aui-base">
				AUI.$('#<portlet:namespace />enableLayoutButton').on(
					'click',
					function(event) {
						submitForm(document.hrefFm, '<%= enableLayoutURL %>');
					}
				);
			</aui:script>

			<aui:button cssClass="remove-layout" id="deleteLayoutButton" name="deleteLayout" value="delete-in-all-pages-variations" />

			<portlet:actionURL name="/layout/delete_layout" var="deleteLayoutURL">
				<portlet:param name="mvcPath" value="/view.jsp" />
				<portlet:param name="redirect" value='<%= HttpUtil.addParameter(redirectURL.toString(), liferayPortletResponse.getNamespace() + "selPlid", selLayout.getParentPlid()) %>' />
				<portlet:param name="selPlid" value="<%= String.valueOf(layoutsAdminDisplayContext.getSelPlid()) %>" />
				<portlet:param name="layoutSetBranchId" value="0" />
				<portlet:param name="selPlid" value="<%= String.valueOf(selLayout.getParentPlid()) %>" />
			</portlet:actionURL>

			<aui:script use="aui-base">
				AUI.$('#<portlet:namespace />deleteLayoutButton').on(
					'click',
					function(event) {
						submitForm(document.hrefFm, '<%= deleteLayoutURL %>');
					}
				);
			</aui:script>
		</aui:button-row>
	</c:when>
	<c:otherwise>
		<portlet:actionURL name="/layout/edit_layout" var="editLayoutURL">
			<portlet:param name="mvcRenderCommandName" value="/layout/edit_layout" />
		</portlet:actionURL>

		<aui:form action='<%= HttpUtil.addParameter(editLayoutURL, "refererPlid", plid) %>' cssClass="container-fluid-1280" enctype="multipart/form-data" method="post" name="editLayoutFm">
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
			<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />
			<aui:input name="groupId" type="hidden" value="<%= layoutsAdminDisplayContext.getGroupId() %>" />
			<aui:input name="liveGroupId" type="hidden" value="<%= layoutsAdminDisplayContext.getLiveGroupId() %>" />
			<aui:input name="stagingGroupId" type="hidden" value="<%= layoutsAdminDisplayContext.getStagingGroupId() %>" />
			<aui:input name="selPlid" type="hidden" value="<%= layoutsAdminDisplayContext.getSelPlid() %>" />
			<aui:input name="privateLayout" type="hidden" value="<%= layoutsAdminDisplayContext.isPrivateLayout() %>" />
			<aui:input name="layoutId" type="hidden" value="<%= layoutsAdminDisplayContext.getLayoutId() %>" />
			<aui:input name="type" type="hidden" value="<%= selLayout.getType() %>" />
			<aui:input name="<%= PortletDataHandlerKeys.SELECTED_LAYOUTS %>" type="hidden" />

			<liferay-ui:error exception="<%= LayoutTypeException.class %>">

				<%
				LayoutTypeException lte = (LayoutTypeException)errorException;

				String type = BeanParamUtil.getString(selLayout, request, "type");
				%>

				<c:if test="<%= lte.getType() == LayoutTypeException.FIRST_LAYOUT %>">
					<liferay-ui:message arguments='<%= Validator.isNull(lte.getLayoutType()) ? type : "layout.types." + lte.getLayoutType() %>' key="the-first-page-cannot-be-of-type-x" />
				</c:if>

				<c:if test="<%= lte.getType() == LayoutTypeException.FIRST_LAYOUT_PERMISSION %>">
					<liferay-ui:message key="you-cannot-delete-this-page-because-the-next-page-is-not-vieweable-by-unathenticated-users-and-so-cannot-be-the-first-page" />
				</c:if>

				<c:if test="<%= lte.getType() == LayoutTypeException.NOT_INSTANCEABLE %>">
					<liferay-ui:message arguments="<%= type %>" key="pages-of-type-x-cannot-be-selected" />
				</c:if>

				<c:if test="<%= lte.getType() == LayoutTypeException.NOT_PARENTABLE %>">
					<liferay-ui:message arguments="<%= type %>" key="pages-of-type-x-cannot-have-child-pages" />
				</c:if>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= LayoutNameException.class %>" message="please-enter-a-valid-name" />

			<liferay-ui:error exception="<%= RequiredLayoutException.class %>">

				<%
				RequiredLayoutException rle = (RequiredLayoutException)errorException;
				%>

				<c:if test="<%= rle.getType() == RequiredLayoutException.AT_LEAST_ONE %>">
					<liferay-ui:message key="you-must-have-at-least-one-page" />
				</c:if>
			</liferay-ui:error>

			<c:if test="<%= layoutRevision != null %>">
				<aui:input name="layoutSetBranchId" type="hidden" value="<%= layoutRevision.getLayoutSetBranchId() %>" />
			</c:if>

			<c:if test="<%= !group.isLayoutPrototype() && (selLayout != null) %>">
				<c:if test="<%= selGroup.hasLocalOrRemoteStagingGroup() && !selGroup.isStagingGroup() %>">
					<div class="alert alert-warning">
						<liferay-ui:message key="changes-are-immediately-available-to-end-users" />
					</div>
				</c:if>

				<%
				Group selLayoutGroup = selLayout.getGroup();
				%>

				<c:choose>
					<c:when test="<%= !SitesUtil.isLayoutUpdateable(selLayout) %>">
						<div class="alert alert-warning">
							<liferay-ui:message key="this-page-cannot-be-modified-because-it-is-associated-to-a-site-template-does-not-allow-modifications-to-it" />
						</div>
					</c:when>
					<c:when test="<%= !SitesUtil.isLayoutDeleteable(selLayout) %>">
						<div class="alert alert-warning">
							<liferay-ui:message key="this-page-cannot-be-deleted-and-cannot-have-child-pages-because-it-is-associated-to-a-site-template" />
						</div>
					</c:when>
				</c:choose>

				<c:if test="<%= (selLayout.getGroupId() != layoutsAdminDisplayContext.getGroupId()) && selLayoutGroup.isUserGroup() %>">

					<%
					UserGroup userGroup = UserGroupLocalServiceUtil.getUserGroup(selLayoutGroup.getClassPK());
					%>

					<div class="alert alert-warning">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(userGroup.getName()) %>" key="this-page-cannot-be-modified-because-it-belongs-to-the-user-group-x" translateArguments="<%= false %>" />
					</div>
				</c:if>
			</c:if>

			<liferay-ui:form-navigator
				formModelBean="<%= selLayout %>"
				id="<%= FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT %>"
				markupView="lexicon"
				showButtons="<%= false %>"
			/>

			<c:if test="<%= (selLayout.getGroupId() == layoutsAdminDisplayContext.getGroupId()) && SitesUtil.isLayoutUpdateable(selLayout) && LayoutPermissionUtil.contains(permissionChecker, selLayout, ActionKeys.UPDATE) %>">
				<aui:button-row>
					<aui:button type="submit" />

					<c:if test="<%= Validator.isNotNull(backURL) %>">
						<aui:button href="<%= backURL %>" name="cancelButton" type="cancel" />
					</c:if>
				</aui:button-row>
			</c:if>
		</aui:form>
	</c:otherwise>
</c:choose>