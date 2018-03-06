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

package com.liferay.asset.categories.admin.web.internal.servlet.taglib.ui;

import com.liferay.asset.categories.admin.web.internal.constants.AssetCategoriesConstants;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"screen.navigation.category.order:Integer=20",
		"screen.navigation.entry.order:Integer=20"
	},
	service = {ScreenNavigationCategory.class, ScreenNavigationEntry.class}
)
public class CategoryPropertiesScreenNavigationEntry
	implements ScreenNavigationCategory, ScreenNavigationEntry<AssetCategory> {

	@Override
	public String getCategoryKey() {
		return "properties";
	}

	@Override
	public String getEntryKey() {
		return "properties";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "properties");
	}

	@Override
	public String getScreenNavigationKey() {
		return AssetCategoriesConstants.CATEGORY_KEY_GENERAL;
	}

	@Override
	public boolean isVisible(User user, AssetCategory category) {
		if (category == null) {
			return false;
		}

		return true;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		_jspRenderer.renderJSP(request, response, "/category/properties.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

}