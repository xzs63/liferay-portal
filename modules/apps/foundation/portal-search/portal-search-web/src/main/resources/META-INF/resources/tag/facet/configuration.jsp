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

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
TagFacetPortletPreferences tagFacetPortletPreferences = new TagFacetPortletPreferencesImpl(java.util.Optional.ofNullable(portletPreferences));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<div class="portlet-configuration-body-content">
		<div class="container-fluid-1280">
			<aui:input label="tag-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(TagFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME) %>" value="<%= tagFacetPortletPreferences.getParameterName() %>" />

			<aui:select label="display-style" name="<%= PortletPreferencesJspUtil.getInputName(TagFacetPortletPreferences.PREFERENCE_KEY_DISPLAY_STYLE) %>">
				<aui:option label="cloud" selected="<%= tagFacetPortletPreferences.isDisplayStyleCloud() %>" />
				<aui:option label="list" selected="<%= tagFacetPortletPreferences.isDisplayStyleList() %>" />
			</aui:select>

			<aui:input label="max-terms" name="<%= PortletPreferencesJspUtil.getInputName(TagFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS) %>" value="<%= tagFacetPortletPreferences.getMaxTerms() %>" />

			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(TagFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD) %>" value="<%= tagFacetPortletPreferences.getFrequencyThreshold() %>" />

			<aui:input label="display-frequencies" name="<%= PortletPreferencesJspUtil.getInputName(TagFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= tagFacetPortletPreferences.isFrequenciesVisible() %>" />
		</div>
	</div>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>