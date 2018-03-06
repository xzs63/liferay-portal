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

package com.liferay.layout.page.template.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.layout.page.template.service.http.LayoutPageTemplateCollectionServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.layout.page.template.service.http.LayoutPageTemplateCollectionServiceSoap
 * @generated
 */
@ProviderType
public class LayoutPageTemplateCollectionSoap implements Serializable {
	public static LayoutPageTemplateCollectionSoap toSoapModel(
		LayoutPageTemplateCollection model) {
		LayoutPageTemplateCollectionSoap soapModel = new LayoutPageTemplateCollectionSoap();

		soapModel.setLayoutPageTemplateCollectionId(model.getLayoutPageTemplateCollectionId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setName(model.getName());
		soapModel.setDescription(model.getDescription());
		soapModel.setType(model.getType());

		return soapModel;
	}

	public static LayoutPageTemplateCollectionSoap[] toSoapModels(
		LayoutPageTemplateCollection[] models) {
		LayoutPageTemplateCollectionSoap[] soapModels = new LayoutPageTemplateCollectionSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static LayoutPageTemplateCollectionSoap[][] toSoapModels(
		LayoutPageTemplateCollection[][] models) {
		LayoutPageTemplateCollectionSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new LayoutPageTemplateCollectionSoap[models.length][models[0].length];
		}
		else {
			soapModels = new LayoutPageTemplateCollectionSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static LayoutPageTemplateCollectionSoap[] toSoapModels(
		List<LayoutPageTemplateCollection> models) {
		List<LayoutPageTemplateCollectionSoap> soapModels = new ArrayList<LayoutPageTemplateCollectionSoap>(models.size());

		for (LayoutPageTemplateCollection model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new LayoutPageTemplateCollectionSoap[soapModels.size()]);
	}

	public LayoutPageTemplateCollectionSoap() {
	}

	public long getPrimaryKey() {
		return _layoutPageTemplateCollectionId;
	}

	public void setPrimaryKey(long pk) {
		setLayoutPageTemplateCollectionId(pk);
	}

	public long getLayoutPageTemplateCollectionId() {
		return _layoutPageTemplateCollectionId;
	}

	public void setLayoutPageTemplateCollectionId(
		long layoutPageTemplateCollectionId) {
		_layoutPageTemplateCollectionId = layoutPageTemplateCollectionId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public int getType() {
		return _type;
	}

	public void setType(int type) {
		_type = type;
	}

	private long _layoutPageTemplateCollectionId;
	private long _groupId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _name;
	private String _description;
	private int _type;
}