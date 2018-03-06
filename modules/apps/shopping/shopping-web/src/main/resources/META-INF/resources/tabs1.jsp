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
String tabs1 = ParamUtil.getString(request, "tabs1", "categories");

PortletURL viewURL = renderResponse.createRenderURL();

viewURL.setParameter("mvcRenderCommandName", "/shopping/view");
%>

<clay:navigation-bar
	inverted="<%= true %>"
	items="<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("categories"));
						navigationItem.setHref(viewURL, "tabs1", "categories");
						navigationItem.setLabel(LanguageUtil.get(request, "categories"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("cart"));
						navigationItem.setHref(viewURL, "tabs1", "cart");
						navigationItem.setLabel(LanguageUtil.get(request, "cart"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("orders"));
						navigationItem.setHref(viewURL, "tabs1", "orders");
						navigationItem.setLabel(LanguageUtil.get(request, "orders"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("coupons"));
						navigationItem.setHref(viewURL, "tabs1", "coupons");
						navigationItem.setLabel(LanguageUtil.get(request, "coupons"));
					});
			}
		}
	%>"
/>