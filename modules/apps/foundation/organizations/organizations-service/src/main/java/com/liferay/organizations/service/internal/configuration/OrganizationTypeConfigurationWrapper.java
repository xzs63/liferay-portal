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

package com.liferay.organizations.service.internal.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Marco Leo
 */
@Component(
	configurationPid = "com.liferay.organizations.service.internal.configuration.OrganizationTypeConfiguration",
	immediate = true, service = OrganizationTypeConfigurationWrapper.class
)
public class OrganizationTypeConfigurationWrapper {

	public String[] getChildrenTypes() {
		return _organizationTypeConfiguration.childrenTypes();
	}

	public String getName() {
		return _organizationTypeConfiguration.name();
	}

	public boolean isCountryEnabled() {
		return _organizationTypeConfiguration.countryEnabled();
	}

	public boolean isCountryRequired() {
		return _organizationTypeConfiguration.countryRequired();
	}

	public boolean isRootable() {
		return _organizationTypeConfiguration.rootable();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_organizationTypeConfiguration = ConfigurableUtil.createConfigurable(
			OrganizationTypeConfiguration.class, properties);
	}

	private volatile OrganizationTypeConfiguration
		_organizationTypeConfiguration;

}