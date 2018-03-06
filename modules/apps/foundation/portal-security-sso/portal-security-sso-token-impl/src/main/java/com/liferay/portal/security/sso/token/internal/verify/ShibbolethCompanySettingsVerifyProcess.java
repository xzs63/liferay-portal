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

package com.liferay.portal.security.sso.token.internal.verify;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.settings.SettingsFactory;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.sso.token.internal.constants.LegacyTokenPropsKeys;
import com.liferay.portal.security.sso.token.internal.constants.TokenConfigurationKeys;
import com.liferay.portal.security.sso.token.internal.constants.TokenConstants;
import com.liferay.portal.verify.BaseCompanySettingsVerifyProcess;
import com.liferay.portal.verify.VerifyProcess;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Greenwald
 */
@Component(
	immediate = true,
	property = {"verify.process.name=com.liferay.portal.security.sso.token.shibboleth"},
	service = VerifyProcess.class
)
public class ShibbolethCompanySettingsVerifyProcess
	extends BaseCompanySettingsVerifyProcess {

	@Override
	protected CompanyLocalService getCompanyLocalService() {
		return _companyLocalService;
	}

	@Override
	protected Set<String> getLegacyPropertyKeys() {
		return SetUtil.fromArray(LegacyTokenPropsKeys.SHIBBOLETH_KEYS);
	}

	@Override
	protected String[][] getRenamePropertyKeysArray() {
		return new String[][] {
			new String[] {
				LegacyTokenPropsKeys.SHIBBOLETH_AUTH_ENABLED,
				TokenConfigurationKeys.AUTH_ENABLED
			},
			new String[] {
				LegacyTokenPropsKeys.SHIBBOLETH_IMPORT_FROM_LDAP,
				TokenConfigurationKeys.IMPORT_FROM_LDAP
			},
			new String[] {
				LegacyTokenPropsKeys.SHIBBOLETH_LOGOUT_URL,
				TokenConfigurationKeys.LOGOUT_REDIRECT_URL
			},
			new String[] {
				LegacyTokenPropsKeys.SHIBBOLETH_USER_HEADER,
				TokenConfigurationKeys.USER_HEADER
			}
		};
	}

	@Override
	protected SettingsFactory getSettingsFactory() {
		return _settingsFactory;
	}

	@Override
	protected String getSettingsId() {
		return TokenConstants.SERVICE_NAME;
	}

	@Reference(unbind = "-")
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Reference(unbind = "-")
	protected void setSettingsFactory(SettingsFactory settingsFactory) {
		_settingsFactory = settingsFactory;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ShibbolethCompanySettingsVerifyProcess.class);

	private CompanyLocalService _companyLocalService;
	private PrefsProps _prefsProps;
	private SettingsFactory _settingsFactory;

}