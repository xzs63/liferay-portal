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

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Ferrer
 */
public class ConfigurationScopeDisplay {

	public ConfigurationScopeDisplay(String scope) {
		_scope = scope;
	}

	public void add(ConfigurationModel configurationModel) {
		_configurationModels.add(configurationModel);
	}

	public List<ConfigurationModel> getConfigurationModels() {
		return _configurationModels;
	}

	public String getScope() {
		return _scope;
	}

	private List<ConfigurationModel> _configurationModels = new ArrayList<>();
	private final String _scope;

}