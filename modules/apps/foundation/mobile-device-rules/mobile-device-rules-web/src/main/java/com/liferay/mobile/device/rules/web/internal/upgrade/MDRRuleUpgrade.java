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

package com.liferay.mobile.device.rules.web.internal.upgrade;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Tom Wang
 */
public class MDRRuleUpgrade extends UpgradeProcess {

	public MDRRuleUpgrade(String oldPackageName, String newPackageName) {
		_oldPackageName = oldPackageName;
		_newPackageName = newPackageName;
	}

	@Override
	public void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update MDRRule set type_ = '", _newPackageName,
				"' where type_ = '", _oldPackageName, "'"));
	}

	private final String _newPackageName;
	private final String _oldPackageName;

}