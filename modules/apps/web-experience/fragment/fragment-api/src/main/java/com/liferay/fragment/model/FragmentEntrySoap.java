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

package com.liferay.fragment.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.fragment.service.http.FragmentEntryServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.fragment.service.http.FragmentEntryServiceSoap
 * @generated
 */
@ProviderType
public class FragmentEntrySoap implements Serializable {
	public static FragmentEntrySoap toSoapModel(FragmentEntry model) {
		FragmentEntrySoap soapModel = new FragmentEntrySoap();

		soapModel.setFragmentEntryId(model.getFragmentEntryId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setFragmentCollectionId(model.getFragmentCollectionId());
		soapModel.setFragmentEntryKey(model.getFragmentEntryKey());
		soapModel.setName(model.getName());
		soapModel.setCss(model.getCss());
		soapModel.setHtml(model.getHtml());
		soapModel.setJs(model.getJs());
		soapModel.setHtmlPreviewEntryId(model.getHtmlPreviewEntryId());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());

		return soapModel;
	}

	public static FragmentEntrySoap[] toSoapModels(FragmentEntry[] models) {
		FragmentEntrySoap[] soapModels = new FragmentEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static FragmentEntrySoap[][] toSoapModels(FragmentEntry[][] models) {
		FragmentEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new FragmentEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new FragmentEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static FragmentEntrySoap[] toSoapModels(List<FragmentEntry> models) {
		List<FragmentEntrySoap> soapModels = new ArrayList<FragmentEntrySoap>(models.size());

		for (FragmentEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new FragmentEntrySoap[soapModels.size()]);
	}

	public FragmentEntrySoap() {
	}

	public long getPrimaryKey() {
		return _fragmentEntryId;
	}

	public void setPrimaryKey(long pk) {
		setFragmentEntryId(pk);
	}

	public long getFragmentEntryId() {
		return _fragmentEntryId;
	}

	public void setFragmentEntryId(long fragmentEntryId) {
		_fragmentEntryId = fragmentEntryId;
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

	public long getFragmentCollectionId() {
		return _fragmentCollectionId;
	}

	public void setFragmentCollectionId(long fragmentCollectionId) {
		_fragmentCollectionId = fragmentCollectionId;
	}

	public String getFragmentEntryKey() {
		return _fragmentEntryKey;
	}

	public void setFragmentEntryKey(String fragmentEntryKey) {
		_fragmentEntryKey = fragmentEntryKey;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getCss() {
		return _css;
	}

	public void setCss(String css) {
		_css = css;
	}

	public String getHtml() {
		return _html;
	}

	public void setHtml(String html) {
		_html = html;
	}

	public String getJs() {
		return _js;
	}

	public void setJs(String js) {
		_js = js;
	}

	public long getHtmlPreviewEntryId() {
		return _htmlPreviewEntryId;
	}

	public void setHtmlPreviewEntryId(long htmlPreviewEntryId) {
		_htmlPreviewEntryId = htmlPreviewEntryId;
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public long getStatusByUserId() {
		return _statusByUserId;
	}

	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	public String getStatusByUserName() {
		return _statusByUserName;
	}

	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	public Date getStatusDate() {
		return _statusDate;
	}

	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;
	}

	private long _fragmentEntryId;
	private long _groupId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _fragmentCollectionId;
	private String _fragmentEntryKey;
	private String _name;
	private String _css;
	private String _html;
	private String _js;
	private long _htmlPreviewEntryId;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
}