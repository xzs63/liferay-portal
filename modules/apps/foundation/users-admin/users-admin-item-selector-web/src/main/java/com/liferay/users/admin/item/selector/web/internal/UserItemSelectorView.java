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

package com.liferay.users.admin.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.users.admin.item.selector.UserItemSelectorCriterion;
import com.liferay.users.admin.item.selector.web.internal.constants.UserItemSelectorViewConstants;
import com.liferay.users.admin.item.selector.web.internal.display.context.UserItemSelectorViewDisplayContext;
import com.liferay.users.admin.kernel.util.UsersAdmin;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletURL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(immediate = true, service = ItemSelectorView.class)
public class UserItemSelectorView
	implements ItemSelectorView<UserItemSelectorCriterion> {

	@Override
	public Class<UserItemSelectorCriterion> getItemSelectorCriterionClass() {
		return UserItemSelectorCriterion.class;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundle resourceBundle = _portal.getResourceBundle(locale);

		return LanguageUtil.get(resourceBundle, "users");
	}

	@Override
	public boolean isVisible(ThemeDisplay themeDisplay) {
		return true;
	}

	@Override
	public void renderHTML(
			ServletRequest request, ServletResponse response,
			UserItemSelectorCriterion userItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;

		UserItemSelectorViewDisplayContext userItemSelectorViewDisplayContext =
			new UserItemSelectorViewDisplayContext(
				_userLocalService, _usersAdmin, httpServletRequest, portletURL,
				itemSelectedEventName);

		request.setAttribute(
			UserItemSelectorViewConstants.
				USER_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			userItemSelectorViewDisplayContext);

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/user_item_selector.jsp");

		requestDispatcher.include(request, response);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new ItemSelectorReturnType[] {
					new UUIDItemSelectorReturnType()
				}));

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.users.admin.item.selector.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UsersAdmin _usersAdmin;

}