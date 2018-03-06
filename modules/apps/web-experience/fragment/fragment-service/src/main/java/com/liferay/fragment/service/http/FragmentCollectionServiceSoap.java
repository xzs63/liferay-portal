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

package com.liferay.fragment.service.http;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.service.FragmentCollectionServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.rmi.RemoteException;

/**
 * Provides the SOAP utility for the
 * {@link FragmentCollectionServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link com.liferay.fragment.model.FragmentCollectionSoap}.
 * If the method in the service utility returns a
 * {@link com.liferay.fragment.model.FragmentCollection}, that is translated to a
 * {@link com.liferay.fragment.model.FragmentCollectionSoap}. Methods that SOAP cannot
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
 * @see FragmentCollectionServiceHttp
 * @see com.liferay.fragment.model.FragmentCollectionSoap
 * @see FragmentCollectionServiceUtil
 * @generated
 */
@ProviderType
public class FragmentCollectionServiceSoap {
	public static com.liferay.fragment.model.FragmentCollectionSoap addFragmentCollection(
		long groupId, java.lang.String name, java.lang.String description,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.fragment.model.FragmentCollection returnValue = FragmentCollectionServiceUtil.addFragmentCollection(groupId,
					name, description, serviceContext);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap addFragmentCollection(
		long groupId, java.lang.String fragmentCollectionKey,
		java.lang.String name, java.lang.String description,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.fragment.model.FragmentCollection returnValue = FragmentCollectionServiceUtil.addFragmentCollection(groupId,
					fragmentCollectionKey, name, description, serviceContext);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap deleteFragmentCollection(
		long fragmentCollectionId) throws RemoteException {
		try {
			com.liferay.fragment.model.FragmentCollection returnValue = FragmentCollectionServiceUtil.deleteFragmentCollection(fragmentCollectionId);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void deleteFragmentCollections(long[] fragmentCollectionIds)
		throws RemoteException {
		try {
			FragmentCollectionServiceUtil.deleteFragmentCollections(fragmentCollectionIds);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap fetchFragmentCollection(
		long fragmentCollectionId) throws RemoteException {
		try {
			com.liferay.fragment.model.FragmentCollection returnValue = FragmentCollectionServiceUtil.fetchFragmentCollection(fragmentCollectionId);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap[] getFragmentCollections(
		long groupId) throws RemoteException {
		try {
			java.util.List<com.liferay.fragment.model.FragmentCollection> returnValue =
				FragmentCollectionServiceUtil.getFragmentCollections(groupId);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap[] getFragmentCollections(
		long groupId, int start, int end) throws RemoteException {
		try {
			java.util.List<com.liferay.fragment.model.FragmentCollection> returnValue =
				FragmentCollectionServiceUtil.getFragmentCollections(groupId,
					start, end);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap[] getFragmentCollections(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.fragment.model.FragmentCollection> orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.fragment.model.FragmentCollection> returnValue =
				FragmentCollectionServiceUtil.getFragmentCollections(groupId,
					start, end, orderByComparator);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap[] getFragmentCollections(
		long groupId, java.lang.String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.fragment.model.FragmentCollection> orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.fragment.model.FragmentCollection> returnValue =
				FragmentCollectionServiceUtil.getFragmentCollections(groupId,
					name, start, end, orderByComparator);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getFragmentCollectionsCount(long groupId)
		throws RemoteException {
		try {
			int returnValue = FragmentCollectionServiceUtil.getFragmentCollectionsCount(groupId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getFragmentCollectionsCount(long groupId,
		java.lang.String name) throws RemoteException {
		try {
			int returnValue = FragmentCollectionServiceUtil.getFragmentCollectionsCount(groupId,
					name);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static java.lang.String[] getTempFileNames(long groupId,
		java.lang.String folderName) throws RemoteException {
		try {
			java.lang.String[] returnValue = FragmentCollectionServiceUtil.getTempFileNames(groupId,
					folderName);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.fragment.model.FragmentCollectionSoap updateFragmentCollection(
		long fragmentCollectionId, java.lang.String name,
		java.lang.String description) throws RemoteException {
		try {
			com.liferay.fragment.model.FragmentCollection returnValue = FragmentCollectionServiceUtil.updateFragmentCollection(fragmentCollectionId,
					name, description);

			return com.liferay.fragment.model.FragmentCollectionSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(FragmentCollectionServiceSoap.class);
}