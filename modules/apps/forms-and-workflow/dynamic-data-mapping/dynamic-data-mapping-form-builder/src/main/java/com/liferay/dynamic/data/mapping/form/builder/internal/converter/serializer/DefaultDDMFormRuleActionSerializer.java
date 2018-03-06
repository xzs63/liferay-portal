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

package com.liferay.dynamic.data.mapping.form.builder.internal.converter.serializer;

import com.liferay.dynamic.data.mapping.form.builder.internal.converter.model.action.DefaultDDMFormRuleAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class DefaultDDMFormRuleActionSerializer
	implements DDMFormRuleActionSerializer {

	public DefaultDDMFormRuleActionSerializer(
		DefaultDDMFormRuleAction defaultDDMFormRuleAction) {

		_defaultDefaultDDMFormRuleAction = defaultDDMFormRuleAction;
	}

	@Override
	public String serialize(
		DDMFormRuleSerializerContext ddmFormRuleSerializerContext) {

		String functionName = _actionBooleanFunctionNameMap.get(
			_defaultDefaultDDMFormRuleAction.getAction());

		return String.format(
			_SET_BOOLEAN_PROPERTY_FORMAT, functionName,
			_defaultDefaultDDMFormRuleAction.getTarget());
	}

	private static final String _SET_BOOLEAN_PROPERTY_FORMAT = "%s('%s', true)";

	private static final Map<String, String> _actionBooleanFunctionNameMap =
		new HashMap<>();

	static {
		_actionBooleanFunctionNameMap.put("enable", "setEnabled");
		_actionBooleanFunctionNameMap.put("invalidate", "setInvalid");
		_actionBooleanFunctionNameMap.put("require", "setRequired");
		_actionBooleanFunctionNameMap.put("show", "setVisible");
	}

	private final DefaultDDMFormRuleAction _defaultDefaultDDMFormRuleAction;

}