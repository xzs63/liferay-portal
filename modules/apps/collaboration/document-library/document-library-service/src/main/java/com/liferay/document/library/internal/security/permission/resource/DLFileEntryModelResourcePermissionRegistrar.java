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

package com.liferay.document.library.internal.security.permission.resource;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionCheckerUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourcePermissionCheckerUtil;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermission;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(immediate = true)
public class DLFileEntryModelResourcePermissionRegistrar {

	@Activate
	public void activate(BundleContext bundleContext) {
		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("model.class.name", DLFileEntry.class.getName());

		_serviceRegistration = bundleContext.registerService(
			ModelResourcePermission.class,
			ModelResourcePermissionFactory.create(
				DLFileEntry.class, DLFileEntry::getFileEntryId,
				_dlFileEntryLocalService::getDLFileEntry,
				_portletResourcePermission,
				(modelResourcePermission, consumer) -> {
					consumer.accept(
						new StagedModelPermissionLogic<>(
							_stagingPermission, DLPortletKeys.DOCUMENT_LIBRARY,
							DLFileEntry::getFileEntryId));
					consumer.accept(
						new DLFileEntryWorkflowedModelPermissionLogic(
							modelResourcePermission));
					consumer.accept(
						(permissionChecker, name, fileEntry, actionId) -> {
							String className = fileEntry.getClassName();
							long classPK = fileEntry.getClassPK();

							if (Validator.isNull(className) || (classPK <= 0)) {
								return null;
							}

							Boolean hasResourcePermission =
								ResourcePermissionCheckerUtil.
									containsResourcePermission(
										permissionChecker, className, classPK,
										actionId);

							if ((hasResourcePermission != null) &&
								!hasResourcePermission) {

								return false;
							}

							Boolean hasBaseModelPermission =
								BaseModelPermissionCheckerUtil.
									containsBaseModelPermission(
										permissionChecker,
										fileEntry.getGroupId(), className,
										classPK, actionId);

							if ((hasBaseModelPermission != null) &&
								!hasBaseModelPermission) {

								return false;
							}

							return null;
						});

					if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
						consumer.accept(
							new DynamicInheritancePermissionLogic<>(
								_dlFolderModelResourcePermission,
								_getFetchParentFunction(), true));
					}
				}),
			properties);
	}

	@Deactivate
	public void deactivate() {
		_serviceRegistration.unregister();
	}

	private UnsafeFunction<DLFileEntry, DLFolder, PortalException>
		_getFetchParentFunction() {

		return fileEntry -> {
			long folderId = fileEntry.getFolderId();

			if (DLFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (fileEntry.isInTrash()) {
				return _dlFolderLocalService.fetchDLFolder(folderId);
			}

			return _dlFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFolder)"
	)
	private ModelResourcePermission<DLFolder> _dlFolderModelResourcePermission;

	@Reference(target = "(resource.name=" + DLConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	private ServiceRegistration<ModelResourcePermission> _serviceRegistration;

	@Reference
	private StagingPermission _stagingPermission;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

	@Reference
	private WorkflowPermission _workflowPermission;

	private class DLFileEntryWorkflowedModelPermissionLogic
		implements ModelResourcePermissionLogic<DLFileEntry> {

		@Override
		public Boolean contains(
				PermissionChecker permissionChecker, String name,
				DLFileEntry fileEntry, String actionId)
			throws PortalException {

			DLFileVersion fileVersion = fileEntry.getFileVersion();

			if (fileVersion.isDraft() || fileVersion.isScheduled()) {
				if (actionId.equals(ActionKeys.VIEW) &&
					!_modelResourcePermission.contains(
						permissionChecker, fileEntry, ActionKeys.UPDATE)) {

					return false;
				}
			}
			else if (fileVersion.isPending()) {
				return _workflowPermission.hasPermission(
					permissionChecker, fileVersion.getGroupId(), name,
					fileVersion.getFileVersionId(), actionId);
			}

			return null;
		}

		private DLFileEntryWorkflowedModelPermissionLogic(
			ModelResourcePermission<DLFileEntry> modelResourcePermission) {

			_modelResourcePermission = modelResourcePermission;
		}

		private final ModelResourcePermission<DLFileEntry>
			_modelResourcePermission;

	}

}