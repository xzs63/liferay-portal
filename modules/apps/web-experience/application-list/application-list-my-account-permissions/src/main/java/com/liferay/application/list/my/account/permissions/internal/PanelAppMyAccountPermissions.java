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

package com.liferay.application.list.my.account.permissions.internal;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletPreferences;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Drew Brokke
 */
@Component(immediate = true, service = PanelAppMyAccountPermissions.class)
public class PanelAppMyAccountPermissions {

	public void initPermissions(List<Company> companies, Portlet portlet) {
		for (Company company : companies) {
			initPermissions(company.getCompanyId(), Arrays.asList(portlet));
		}
	}

	public void initPermissions(long companyId, List<Portlet> portlets) {
		Role userRole = _getUserRole(companyId);

		if (userRole == null) {
			return;
		}

		for (Portlet portlet : portlets) {
			try {
				List<String> actionIds =
					ResourceActionsUtil.getPortletResourceActions(
						portlet.getRootPortletId());

				_initPermissions(
					companyId, portlet.getPortletId(),
					portlet.getRootPortletId(), userRole, actionIds);
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to initialize My Account panel permissions ",
						"for portlet ", portlet.getPortletId(), " in company ",
						String.valueOf(companyId)),
					e);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		String filter = StringBundler.concat(
			"(&(objectClass=", PanelApp.class.getName(), ")",
			"(panel.category.key=", PanelCategoryKeys.USER_MY_ACCOUNT, "*))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filter, new PanelAppServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Role _getUserRole(long companyId) {
		try {
			return _roleLocalService.getRole(companyId, RoleConstants.USER);
		}
		catch (PortalException pe) {
			_log.error("Unable to get user role in company " + companyId, pe);
		}

		return null;
	}

	private void _initPermissions(
			long companyId, String portletId, String rootPortletId,
			Role userRole, List<String> actionIds)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getLayoutPortletSetup(
				companyId, companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				LayoutConstants.DEFAULT_PLID, portletId,
				PortletConstants.DEFAULT_PREFERENCES);

		if (_prefsProps.getBoolean(
				portletPreferences,
				"myAccountAccessInControlPanelPermissionsInitialized")) {

			return;
		}

		if (actionIds.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
			_resourcePermissionLocalService.addResourcePermission(
				companyId, rootPortletId, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), userRole.getRoleId(),
				ActionKeys.ACCESS_IN_CONTROL_PANEL);
		}

		portletPreferences.setValue(
			"myAccountAccessInControlPanelPermissionsInitialized",
			StringPool.TRUE);

		portletPreferences.store();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PanelAppMyAccountPermissions.class);

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	private ServiceTracker<PanelApp, PanelApp> _serviceTracker;

	private class PanelAppServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<PanelApp, PanelApp> {

		@Override
		public PanelApp addingService(ServiceReference<PanelApp> reference) {
			PanelApp panelApp = _bundleContext.getService(reference);

			try {
				Portlet portlet = panelApp.getPortlet();

				if (portlet == null) {
					portlet = _portletLocalService.getPortletById(
						panelApp.getPortletId());
				}

				if (portlet == null) {
					Class<?> panelAppClass = panelApp.getClass();

					_log.error(
						StringBundler.concat(
							"Unable to get portlet ", panelApp.getPortletId(),
							" for panel app ", panelAppClass.getName()));

					return panelApp;
				}

				initPermissions(_companyLocalService.getCompanies(), portlet);

				return panelApp;
			}
			catch (Throwable t) {
				_bundleContext.ungetService(reference);

				throw t;
			}
		}

		@Override
		public void modifiedService(
			ServiceReference<PanelApp> serviceReference, PanelApp panelApp) {

			removedService(serviceReference, panelApp);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<PanelApp> serviceReference, PanelApp panelApp) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}