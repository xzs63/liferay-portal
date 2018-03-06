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

<%@ include file="/com.liferay.blogs.web/init.jsp" %>

<%
BlogsGroupServiceOverriddenConfiguration blogsGroupServiceOverriddenConfiguration = ConfigurationProviderUtil.getConfiguration(BlogsGroupServiceOverriddenConfiguration.class, new GroupServiceSettingsLocator(themeDisplay.getSiteGroupId(), BlogsConstants.SERVICE_NAME));
%>

<c:if test="<%= blogsGroupServiceOverriddenConfiguration.enableRss() %>">

	<%
	String rssURL = RSSUtil.getURL(StringBundler.concat(themeDisplay.getPathMain(), "/blogs/rss?p_l_id=", String.valueOf(themeDisplay.getPlid()), "&groupId=", String.valueOf(themeDisplay.getScopeGroupId())), GetterUtil.getInteger(blogsGroupServiceOverriddenConfiguration.rssDelta()), blogsGroupServiceOverriddenConfiguration.rssDisplayStyle(), blogsGroupServiceOverriddenConfiguration.rssFeedType(), null);
	%>

	<clay:link buttonStyle="borderless" elementClasses="btn-sm" href="<%= rssURL %>" icon="rss" label='<%= LanguageUtil.get(request, "rss") %>' />

	<liferay-util:html-top>
		<link href="<%= HtmlUtil.escapeAttribute(rssURL) %>" rel="alternate" title="RSS" type="application/rss+xml" />
	</liferay-util:html-top>
</c:if>

<%
BlogsGroupServiceSettings blogsGroupServiceSettings = BlogsGroupServiceSettings.getInstance(scopeGroupId);
%>

<c:if test="<%= BlogsPermission.contains(permissionChecker, scopeGroupId, ActionKeys.SUBSCRIBE) && (blogsGroupServiceSettings.isEmailEntryAddedEnabled() || blogsGroupServiceSettings.isEmailEntryUpdatedEnabled()) %>">
	<c:choose>
		<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(company.getCompanyId(), user.getUserId(), BlogsEntry.class.getName(), scopeGroupId) %>">
			<portlet:actionURL name="/blogs/edit_entry" var="unsubscribeURL">
				<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:actionURL>

			<clay:link buttonStyle="secondary" elementClasses="btn-sm" href="<%= unsubscribeURL %>" label='<%= LanguageUtil.get(request, "unsubscribe") %>' />
		</c:when>
		<c:otherwise>
			<portlet:actionURL name="/blogs/edit_entry" var="subscribeURL">
				<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:actionURL>

			<clay:link buttonStyle="secondary" elementClasses="btn-sm" href="<%= subscribeURL %>" label='<%= LanguageUtil.get(request, "subscribe") %>' />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="<%= BlogsPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_ENTRY) %>">
	<portlet:renderURL var="editEntryURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
		<portlet:param name="mvcRenderCommandName" value="/blogs/edit_entry" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<clay:link buttonStyle="primary" elementClasses="btn-sm" href="<%= editEntryURL %>" label='<%= LanguageUtil.get(request, "new-blog-entry") %>' />
</c:if>