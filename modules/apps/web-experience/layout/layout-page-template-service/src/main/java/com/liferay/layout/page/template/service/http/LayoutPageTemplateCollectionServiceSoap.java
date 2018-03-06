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

package com.liferay.layout.page.template.service.http;

import aQute.bnd.annotation.ProviderType;

import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.rmi.RemoteException;

/**
 * Provides the SOAP utility for the
 * {@link LayoutPageTemplateCollectionServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap}.
 * If the method in the service utility returns a
 * {@link com.liferay.layout.page.template.model.LayoutPageTemplateCollection}, that is translated to a
 * {@link com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap}. Methods that SOAP cannot
 * safely wire are skipped.
 * </p>
 *
 * <p>
 * The benefits of using the SOAP utility is that it is cross platform
 * compatible. SOAP allows different languages like Java, .NET, C++, PHP, and
 * even Perl, to call the generated services. One drawback of SOAP is that it is
 * slow because it needs to serialize all calls into a text format (XML).
 * </p>
 *
 * <p>
 * You can see a list of services at http://localhost:8080/api/axis. Set the
 * property <b>axis.servlet.hosts.allowed</b> in portal.properties to configure
 * security.
 * </p>
 *
 * <p>
 * The SOAP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionServiceHttp
 * @see com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap
 * @see LayoutPageTemplateCollectionServiceUtil
 * @generated
 */
@ProviderType
public class LayoutPageTemplateCollectionServiceSoap {
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap addLayoutPageTemplateCollection(
		long groupId, java.lang.String name, java.lang.String description,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.layout.page.template.model.LayoutPageTemplateCollection returnValue =
				LayoutPageTemplateCollectionServiceUtil.addLayoutPageTemplateCollection(groupId,
					name, description, serviceContext);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap deleteLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) throws RemoteException {
		try {
			com.liferay.layout.page.template.model.LayoutPageTemplateCollection returnValue =
				LayoutPageTemplateCollectionServiceUtil.deleteLayoutPageTemplateCollection(layoutPageTemplateCollectionId);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void deleteLayoutPageTemplateCollections(
		long[] layoutPageTemplateCollectionIds) throws RemoteException {
		try {
			LayoutPageTemplateCollectionServiceUtil.deleteLayoutPageTemplateCollections(layoutPageTemplateCollectionIds);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap fetchLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) throws RemoteException {
		try {
			com.liferay.layout.page.template.model.LayoutPageTemplateCollection returnValue =
				LayoutPageTemplateCollectionServiceUtil.fetchLayoutPageTemplateCollection(layoutPageTemplateCollectionId);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap[] getLayoutPageTemplateCollections(
		long groupId) throws RemoteException {
		try {
			java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> returnValue =
				LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollections(groupId);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap[] getLayoutPageTemplateCollections(
		long groupId, int type) throws RemoteException {
		try {
			java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> returnValue =
				LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollections(groupId,
					type);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap[] getLayoutPageTemplateCollections(
		long groupId, int start, int end) throws RemoteException {
		try {
			java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> returnValue =
				LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollections(groupId,
					start, end);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap[] getLayoutPageTemplateCollections(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> returnValue =
				LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollections(groupId,
					start, end, orderByComparator);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap[] getLayoutPageTemplateCollections(
		long groupId, java.lang.String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> returnValue =
				LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollections(groupId,
					name, start, end, orderByComparator);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getLayoutPageTemplateCollectionsCount(long groupId)
		throws RemoteException {
		try {
			int returnValue = LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollectionsCount(groupId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getLayoutPageTemplateCollectionsCount(long groupId,
		java.lang.String name) throws RemoteException {
		try {
			int returnValue = LayoutPageTemplateCollectionServiceUtil.getLayoutPageTemplateCollectionsCount(groupId,
					name);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap updateLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId, java.lang.String name,
		java.lang.String description) throws RemoteException {
		try {
			com.liferay.layout.page.template.model.LayoutPageTemplateCollection returnValue =
				LayoutPageTemplateCollectionServiceUtil.updateLayoutPageTemplateCollection(layoutPageTemplateCollectionId,
					name, description);

			return com.liferay.layout.page.template.model.LayoutPageTemplateCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(LayoutPageTemplateCollectionServiceSoap.class);
}