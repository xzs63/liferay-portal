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

package com.liferay.site.navigation.service.http;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.site.navigation.service.SiteNavigationMenuServiceUtil;

import java.rmi.RemoteException;

/**
 * Provides the SOAP utility for the
 * {@link SiteNavigationMenuServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link com.liferay.site.navigation.model.SiteNavigationMenuSoap}.
 * If the method in the service utility returns a
 * {@link com.liferay.site.navigation.model.SiteNavigationMenu}, that is translated to a
 * {@link com.liferay.site.navigation.model.SiteNavigationMenuSoap}. Methods that SOAP cannot
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
 * @see SiteNavigationMenuServiceHttp
 * @see com.liferay.site.navigation.model.SiteNavigationMenuSoap
 * @see SiteNavigationMenuServiceUtil
 * @generated
 */
@ProviderType
public class SiteNavigationMenuServiceSoap {
	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap addSiteNavigationMenu(
		long groupId, java.lang.String name, int type,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.addSiteNavigationMenu(groupId,
					name, type, serviceContext);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap addSiteNavigationMenu(
		long groupId, java.lang.String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.addSiteNavigationMenu(groupId,
					name, serviceContext);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap deleteSiteNavigationMenu(
		long siteNavigationMenuId) throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.deleteSiteNavigationMenu(siteNavigationMenuId);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap fetchSiteNavigationMenu(
		long siteNavigationMenuId) throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.fetchSiteNavigationMenu(siteNavigationMenuId);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap[] getSiteNavigationMenus(
		long groupId) throws RemoteException {
		try {
			java.util.List<com.liferay.site.navigation.model.SiteNavigationMenu> returnValue =
				SiteNavigationMenuServiceUtil.getSiteNavigationMenus(groupId);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap[] getSiteNavigationMenus(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.site.navigation.model.SiteNavigationMenu> returnValue =
				SiteNavigationMenuServiceUtil.getSiteNavigationMenus(groupId,
					start, end, orderByComparator);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap[] getSiteNavigationMenus(
		long groupId, java.lang.String keywords, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws RemoteException {
		try {
			java.util.List<com.liferay.site.navigation.model.SiteNavigationMenu> returnValue =
				SiteNavigationMenuServiceUtil.getSiteNavigationMenus(groupId,
					keywords, start, end, orderByComparator);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getSiteNavigationMenusCount(long groupId)
		throws RemoteException {
		try {
			int returnValue = SiteNavigationMenuServiceUtil.getSiteNavigationMenusCount(groupId);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getSiteNavigationMenusCount(long groupId,
		java.lang.String keywords) throws RemoteException {
		try {
			int returnValue = SiteNavigationMenuServiceUtil.getSiteNavigationMenusCount(groupId,
					keywords);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap updateSiteNavigationMenu(
		long siteNavigationMenuId, int type, boolean auto,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.updateSiteNavigationMenu(siteNavigationMenuId,
					type, auto, serviceContext);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.site.navigation.model.SiteNavigationMenuSoap updateSiteNavigationMenu(
		long siteNavigationMenuId, java.lang.String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.site.navigation.model.SiteNavigationMenu returnValue = SiteNavigationMenuServiceUtil.updateSiteNavigationMenu(siteNavigationMenuId,
					name, serviceContext);

			return com.liferay.site.navigation.model.SiteNavigationMenuSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(SiteNavigationMenuServiceSoap.class);
}