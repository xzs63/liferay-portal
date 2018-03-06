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
String redirect = ParamUtil.getString(request, "redirect", assetCategoriesDisplayContext.getEditCategoryRedirect());

long categoryId = ParamUtil.getLong(request, "categoryId");

AssetCategory category = AssetCategoryLocalServiceUtil.fetchCategory(categoryId);

long parentCategoryId = BeanParamUtil.getLong(category, request, "parentCategoryId");

long vocabularyId = ParamUtil.getLong(request, "vocabularyId");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(((category == null) ? LanguageUtil.get(request, "add-new-category") : category.getTitle(locale)));
%>

<portlet:actionURL name="editCategory" var="editCategoryURL">
	<portlet:param name="mvcPath" value="/edit_category.jsp" />
	<portlet:param name="vocabularyId" value="<%= String.valueOf(vocabularyId) %>" />
</portlet:actionURL>

<aui:form action="<%= editCategoryURL %>" cssClass="container-fluid-1280" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="categoryId" type="hidden" value="<%= categoryId %>" />
	<aui:input name="parentCategoryId" type="hidden" value="<%= parentCategoryId %>" />

	<liferay-ui:error exception="<%= AssetCategoryNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= DuplicateCategoryException.class %>" message="please-enter-a-unique-name" />

	<aui:model-context bean="<%= category %>" model="<%= AssetCategory.class %>" />

	<aui:fieldset-group markupView="lexicon">
		<aui:fieldset>
			<aui:input autoFocus="<%= true %>" label="name" name="title" placeholder="name" />

			<aui:input name="description" placeholder="description" />

			<c:if test="<%= assetCategoriesDisplayContext.isFlattenedNavigationAllowed() %>">
				<aui:field-wrapper label="parent-category">
					<liferay-asset:asset-categories-selector categoryIds="<%= String.valueOf(parentCategoryId) %>" hiddenInput="parentCategoryId" singleSelect="<%= true %>" />
				</aui:field-wrapper>
			</c:if>

			<c:if test="<%= category == null %>">
				<liferay-ui:panel collapsible="<%= true %>" extended="<%= false %>" markupView="lexicon" persistState="<%= true %>" title="permissions">
					<liferay-ui:input-permissions
						modelName="<%= AssetCategory.class.getName() %>"
					/>
				</liferay-ui:panel>
			</c:if>
		</aui:fieldset>
	</aui:fieldset-group>

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button href="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>