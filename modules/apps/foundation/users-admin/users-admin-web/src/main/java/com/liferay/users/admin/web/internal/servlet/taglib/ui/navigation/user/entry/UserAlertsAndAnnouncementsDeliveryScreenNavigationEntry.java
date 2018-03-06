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

package com.liferay.users.admin.web.internal.servlet.taglib.ui.navigation.user.entry;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.portal.kernel.model.User;
import com.liferay.users.admin.constants.UserFormConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {"screen.navigation.entry.order:Integer=20"},
	service = ScreenNavigationEntry.class
)
public class UserAlertsAndAnnouncementsDeliveryScreenNavigationEntry
	extends BaseUserScreenNavigationEntry {

	@Override
	public String getActionCommandName() {
		return "/users_admin/update_announcements_deliveries";
	}

	@Override
	public String getCategoryKey() {
		return UserFormConstants.CATEGORY_KEY_PREFERENCES;
	}

	@Override
	public String getEntryKey() {
		return UserFormConstants.ENTRY_KEY_ALERTS_AND_ANNOUNCEMENTS_DELIVERY;
	}

	@Override
	public String getJspPath() {
		return "/user/announcements.jsp";
	}

	@Override
	public boolean isVisible(User user, User selUser) {
		if (selUser == null) {
			return false;
		}

		return true;
	}

}