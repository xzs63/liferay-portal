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

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

/**
 * @author Carlos Lancha
 */
public class DropdownCheckboxItem extends DropdownItem {

	public DropdownCheckboxItem() {
		super("checkbox");
	}

	public String getInputName() {
		return _inputName;
	}

	public String getInputValue() {
		return _inputValue;
	}

	public boolean isChecked() {
		return _checked;
	}

	public void setChecked(boolean checked) {
		_checked = checked;
	}

	public void setInputName(String inputName) {
		_inputName = inputName;
	}

	public void setInputValue(String inputValue) {
		_inputValue = inputValue;
	}

	private boolean _checked;
	private String _inputName;
	private String _inputValue;

}