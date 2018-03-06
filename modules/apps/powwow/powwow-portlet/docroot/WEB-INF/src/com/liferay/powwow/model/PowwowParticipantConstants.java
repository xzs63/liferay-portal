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

package com.liferay.powwow.model;

import com.liferay.petra.string.StringPool;

/**
 * @author Shinn Lok
 */
public class PowwowParticipantConstants {

	public static final String LABEL_ATTENDEE = "attendee";

	public static final String LABEL_HOST = "host";

	public static final int STATUS_DEFAULT = 0;

	public static final int STATUS_INVITED = 1;

	public static final int TYPE_ATTENDEE = 0;

	public static final int TYPE_HOST = 1;

	public static String getTypeLabel(int type) {
		if (type == TYPE_ATTENDEE) {
			return LABEL_ATTENDEE;
		}
		else if (type == TYPE_HOST) {
			return LABEL_HOST;
		}
		else {
			return StringPool.BLANK;
		}
	}

}