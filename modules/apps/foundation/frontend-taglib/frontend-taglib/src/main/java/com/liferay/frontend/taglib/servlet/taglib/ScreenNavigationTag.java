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

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import java.util.List;
import java.util.Objects;

import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class ScreenNavigationTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if (ListUtil.isEmpty(_screenNavigationCategories)) {
			return SKIP_PAGE;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		ScreenNavigationRegistry screenNavigationRegistry =
			ServletContextUtil.getScreenNavigationRegistry();

		_screenNavigationCategories =
			screenNavigationRegistry.getScreenNavigationCategories(
				_key, themeDisplay.getUser(), getModelContext());

		return super.doStartTag();
	}

	public Object getModelContext() {
		if (Validator.isNotNull(_modelBean)) {
			return _modelBean;
		}

		return _context;
	}

	public void setContext(Object context) {
		_context = context;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setModelBean(Object modelBean) {
		_modelBean = modelBean;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_containerCssClass = "col-md-9";
		_context = null;
		_fullContainerCssClass = "col-md-12";
		_id = null;
		_key = null;
		_modelBean = null;
		_navCssClass = "col-md-3";
		_portletURL = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute(
			"liferay-frontend:screen-navigation:containerCssClass",
			_containerCssClass);
		request.setAttribute(
			"liferay-frontend:screen-navigation:fullContainerCssClass",
			_fullContainerCssClass);

		String id = _id;

		if (Validator.isNotNull(id)) {
			PortletResponse portletResponse =
				(PortletResponse)request.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			String namespace = StringPool.BLANK;

			if (portletResponse != null) {
				namespace = portletResponse.getNamespace();
			}

			id = PortalUtil.getUniqueElementId(
				getOriginalServletRequest(), namespace, id);
		}
		else {
			id = PortalUtil.generateRandomKey(
				request, ScreenNavigationTag.class.getName());
		}

		request.setAttribute("liferay-frontend:screen-navigation:id", id);

		request.setAttribute(
			"liferay-frontend:screen-navigation:navCssClass", _navCssClass);
		request.setAttribute(
			"liferay-frontend:screen-navigation:portletURL", _portletURL);
		request.setAttribute(
			"liferay-frontend:screen-navigation:screenNavigationCategories",
			_screenNavigationCategories);
		request.setAttribute(
			"liferay-frontend:screen-navigation:screenNavigationEntries",
			_getScreenNavigationEntries());
		request.setAttribute(
			"liferay-frontend:screen-navigation:" +
				"selectedScreenNavigationCategory",
			_getSelectedScreenNavigationCategory());
		request.setAttribute(
			"liferay-frontend:screen-navigation:selectedScreenNavigationEntry",
			_getSelectedScreenNavigationEntry());
	}

	private String _getDefaultScreenNavigationCategoryKey() {
		ScreenNavigationCategory screenNavigationCategory =
			_screenNavigationCategories.get(0);

		return screenNavigationCategory.getCategoryKey();
	}

	private String _getDefaultScreenNavigationEntryKey() {
		List<ScreenNavigationEntry> screenNavigationEntries =
			_getScreenNavigationEntries();

		ScreenNavigationEntry screenNavigationEntry =
			screenNavigationEntries.get(0);

		return screenNavigationEntry.getEntryKey();
	}

	private List<ScreenNavigationEntry> _getScreenNavigationEntries() {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		ScreenNavigationRegistry screenNavigationRegistry =
			ServletContextUtil.getScreenNavigationRegistry();

		ScreenNavigationCategory selectedScreenNavigationCategory =
			_getSelectedScreenNavigationCategory();

		return screenNavigationRegistry.getScreenNavigationEntries(
			selectedScreenNavigationCategory, themeDisplay.getUser(),
			getModelContext());
	}

	private ScreenNavigationCategory _getSelectedScreenNavigationCategory() {
		String screenNavigationCategoryKey = ParamUtil.getString(
			request, "screenNavigationCategoryKey",
			_getDefaultScreenNavigationCategoryKey());

		for (ScreenNavigationCategory screenNavigationCategory :
				_screenNavigationCategories) {

			if (Objects.equals(
					screenNavigationCategory.getCategoryKey(),
					screenNavigationCategoryKey)) {

				return screenNavigationCategory;
			}
		}

		return null;
	}

	private ScreenNavigationEntry _getSelectedScreenNavigationEntry() {
		String screenNavigationEntryKey = ParamUtil.getString(
			request, "screenNavigationEntryKey");

		if (Validator.isNull(screenNavigationEntryKey)) {
			screenNavigationEntryKey = _getDefaultScreenNavigationEntryKey();
		}

		List<ScreenNavigationEntry> screenNavigationEntries =
			_getScreenNavigationEntries();

		for (ScreenNavigationEntry screenNavigationEntry :
				screenNavigationEntries) {

			if (Objects.equals(
					screenNavigationEntry.getEntryKey(),
					screenNavigationEntryKey)) {

				return screenNavigationEntry;
			}
		}

		return null;
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/screen_navigation/page.jsp";

	private String _containerCssClass = "col-md-9";
	private Object _context;
	private String _fullContainerCssClass = "col-md-12";
	private String _id;
	private String _key;
	private Object _modelBean;
	private String _navCssClass = "col-md-3";
	private PortletURL _portletURL;
	private List<ScreenNavigationCategory> _screenNavigationCategories;

}