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

package com.liferay.user.associated.data.web.internal.user.action.contributor;

import com.liferay.admin.kernel.util.Omniadmin;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.users.admin.user.action.contributor.BaseUserActionContributor;
import com.liferay.users.admin.user.action.contributor.UserActionContributor;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = UserActionContributor.class)
public class ErasePersonalDataUserActionContributor
	extends BaseUserActionContributor {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return LanguageUtil.get(
			getResourceBundle(getLocale(portletRequest)),
			"erase-personal-data");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse,
		User user, User selUser) {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			portletRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"p_u_i_d", String.valueOf(selUser.getUserId()));
		liferayPortletURL.setParameter(
			"mvcRenderCommandName",
			"/user_associated_data/manage_user_associated_data_summary");

		return liferayPortletURL.toString();
	}

	@Override
	public boolean isShow(
		PortletRequest portletRequest, User user, User selUser) {

		if (_omniadmin.isOmniadmin(selUser)) {
			return false;
		}

		return _omniadmin.isOmniadmin(user);
	}

	@Reference
	private Omniadmin _omniadmin;

	@Reference
	private Portal _portal;

}