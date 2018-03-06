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

package com.liferay.dynamic.data.lists.internal.upgrade;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_0_0.UpgradeKernelPackage;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_0_0.UpgradeLastPublishDate;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_0_0.UpgradeSchema;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_0_1.UpgradeRecordGroup;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_0_2.UpgradeDDLRecordSetSettings;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_1_1.UpgradeDDLRecordSet;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_1_1.UpgradeDDLRecordSetVersion;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_1_2.UpgradeDDLRecord;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_1_2.UpgradeDDLRecordVersion;
import com.liferay.dynamic.data.lists.internal.upgrade.v1_1_3.UpgradeDDLRecordSetSettingsFieldValues;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true,
	service = {DDLServiceUpgrade.class, UpgradeStepRegistrator.class}
)
public class DDLServiceUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"com.liferay.dynamic.data.lists.service", "0.0.1", "0.0.2",
			new UpgradeSchema());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "0.0.2", "0.0.3",
			new UpgradeKernelPackage());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "0.0.3", "1.0.0",
			new UpgradeLastPublishDate());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.0.0", "1.0.1",
			new UpgradeRecordGroup());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.0.1", "1.0.2",
			new UpgradeDDLRecordSetSettings(_jsonFactory));

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.0.2", "1.1.0",
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_1_0.
				UpgradeSchema());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.1.0", "1.1.1",
			new UpgradeDDLRecordSet(),
			new UpgradeDDLRecordSetVersion(
				_counterLocalService, _ddmStructureVersionLocalService,
				_userLocalService));

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.1.1", "1.1.2",
			new UpgradeDDLRecord(), new UpgradeDDLRecordVersion());

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.1.2", "1.1.3",
			new UpgradeDDLRecordSetSettingsFieldValues(_jsonFactory));

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.1.3", "1.2.0",
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_2_0.
				UpgradeDDLRecordSet(
					_classNameLocalService, _ddmFormInstanceLocalService,
					_ddmStructureLinkLocalService,
					_portletPreferencesLocalService,
					_resourcePermissionLocalService),
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_2_0.
				UpgradeDDLRecordSetVersion(_ddmFormInstanceVersionLocalService),
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_2_0.
				UpgradeDDLRecordVersion(
					_ddmFormInstanceRecordVersionLocalService),
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_2_0.
				UpgradeResourceAction(_resourceActionLocalService));

		registry.register(
			"com.liferay.dynamic.data.lists.service", "1.2.0", "1.2.1",
			new com.liferay.dynamic.data.lists.internal.upgrade.v1_2_1.
				UpgradeDDLRecord(_ddmFormInstanceRecordLocalService));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Reference
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}