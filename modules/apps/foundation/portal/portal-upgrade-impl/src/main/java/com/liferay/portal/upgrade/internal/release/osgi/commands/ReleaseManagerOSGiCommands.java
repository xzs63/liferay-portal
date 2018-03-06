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

package com.liferay.portal.upgrade.internal.release.osgi.commands;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBContext;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBProcessContext;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.output.stream.container.OutputStreamContainer;
import com.liferay.portal.output.stream.container.OutputStreamContainerFactory;
import com.liferay.portal.output.stream.container.OutputStreamContainerFactoryTracker;
import com.liferay.portal.upgrade.internal.configuration.ReleaseManagerConfiguration;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;
import com.liferay.portal.upgrade.internal.release.ReleasePublisher;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.utils.log.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Miguel Pastor
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.portal.upgrade.internal.configuration.ReleaseManagerConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	property = {
		"osgi.command.function=check", "osgi.command.function=execute",
		"osgi.command.function=executeAll", "osgi.command.function=list",
		"osgi.command.scope=upgrade"
	},
	service = ReleaseManagerOSGiCommands.class
)
public class ReleaseManagerOSGiCommands {

	@Descriptor("List pending or running upgrades")
	public void check() {
		Set<String> bundleSymbolicNames = _serviceTrackerMap.keySet();

		for (String bundleSymbolicName : bundleSymbolicNames) {
			String schemaVersionString = getSchemaVersionString(
				bundleSymbolicName);

			ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
				_serviceTrackerMap.getService(bundleSymbolicName));

			List<List<UpgradeInfo>> upgradeInfosList =
				releaseGraphManager.getUpgradeInfosList(schemaVersionString);

			int size = upgradeInfosList.size();

			if (size > 1) {
				System.out.println(
					StringBundler.concat(
						"There are ", String.valueOf(size),
						" possible end nodes for ", schemaVersionString));
			}

			if (size == 0) {
				continue;
			}

			StringBundler sb = new StringBundler(6);

			sb.append("There is an upgrade process available for ");
			sb.append(bundleSymbolicName);
			sb.append(" from ");
			sb.append(schemaVersionString);
			sb.append(" to ");

			List<UpgradeInfo> upgradeInfos = upgradeInfosList.get(0);

			UpgradeInfo lastUpgradeInfo = upgradeInfos.get(
				upgradeInfos.size() - 1);

			sb.append(lastUpgradeInfo.getToSchemaVersionString());

			System.out.println(sb.toString());
		}
	}

	@Descriptor("Execute upgrade for a specific module")
	public void execute(String bundleSymbolicName) {
		if (_serviceTrackerMap.getService(bundleSymbolicName) == null) {
			System.out.println(
				"No upgrade processes registered for " + bundleSymbolicName);

			return;
		}

		try {
			doExecute(bundleSymbolicName, _serviceTrackerMap);
		}
		catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}

	@Descriptor("Execute upgrade for a specific module and final version")
	public void execute(String bundleSymbolicName, String toVersionString) {
		String schemaVersionString = getSchemaVersionString(bundleSymbolicName);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			_serviceTrackerMap.getService(bundleSymbolicName));

		executeUpgradeInfos(
			bundleSymbolicName,
			releaseGraphManager.getUpgradeInfos(
				schemaVersionString, toVersionString));
	}

	@Descriptor("Execute all pending upgrades")
	public void executeAll() {
		Set<String> upgradeThrewExceptionBundleSymbolicNames = new HashSet<>();

		executeAll(upgradeThrewExceptionBundleSymbolicNames);

		if (upgradeThrewExceptionBundleSymbolicNames.isEmpty()) {
			System.out.println("All modules were successfully upgraded");

			return;
		}

		StringBundler sb = new StringBundler(
			(upgradeThrewExceptionBundleSymbolicNames.size() * 3) + 3);

		sb.append("\nThe following modules had errors while upgrading:\n");

		for (String upgradeThrewExceptionBundleSymbolicName :
				upgradeThrewExceptionBundleSymbolicNames) {

			sb.append("\t");
			sb.append(upgradeThrewExceptionBundleSymbolicName);
			sb.append("\n");
		}

		sb.append("Use the command upgrade:list <module name> to get more ");
		sb.append("details about the status of a specific upgrade.");

		System.out.println(sb.toString());
	}

	@Descriptor("List registered upgrade processes for all modules")
	public void list() {
		for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
			list(bundleSymbolicName);
		}
	}

	@Descriptor("List registered upgrade processes for a specific module")
	public void list(String bundleSymbolicName) {
		List<UpgradeInfo> upgradeProcesses = _serviceTrackerMap.getService(
			bundleSymbolicName);

		System.out.println(
			StringBundler.concat(
				"Registered upgrade processes for ", bundleSymbolicName, " ",
				getSchemaVersionString(bundleSymbolicName)));

		for (UpgradeInfo upgradeProcess : upgradeProcesses) {
			System.out.println("\t" + upgradeProcess);
		}
	}

	@Reference(unbind = "-")
	public void setOutputStreamTracker(
		OutputStreamContainerFactoryTracker
			outputStreamContainerFactoryTracker) {

		_outputStreamContainerFactoryTracker =
			outputStreamContainerFactoryTracker;
	}

	@Activate
	protected void activate(
		final BundleContext bundleContext, Map<String, Object> properties) {

		_logger = new Logger(bundleContext);

		DB db = DBManagerUtil.getDB();

		ServiceTrackerMapListener<String, UpgradeInfo, List<UpgradeInfo>>
			serviceTrackerMapListener = null;

		_releaseManagerConfiguration = ConfigurableUtil.createConfigurable(
			ReleaseManagerConfiguration.class, properties);

		if (_releaseManagerConfiguration.autoUpgrade()) {
			serviceTrackerMapListener =
				new UpgradeInfoServiceTrackerMapListener();
		}

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, UpgradeStep.class,
			StringBundler.concat(
				"(&(upgrade.bundle.symbolic.name=*)(|(upgrade.db.type=any)",
				"(upgrade.db.type=", String.valueOf(db.getDBType()), ")))"),
			new PropertyServiceReferenceMapper<String, UpgradeStep>(
				"upgrade.bundle.symbolic.name"),
			new UpgradeServiceTrackerCustomizer(bundleContext),
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<UpgradeStep>(
					"upgrade.from.schema.version")),
			serviceTrackerMapListener);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected void doExecute(
		String bundleSymbolicName,
		ServiceTrackerMap<String, List<UpgradeInfo>> serviceTrackerMap) {

		List<List<UpgradeInfo>> upgradeInfosList = getUpgradeInfosList(
			bundleSymbolicName, serviceTrackerMap);

		int size = upgradeInfosList.size();

		if (size > 1) {
			throw new IllegalStateException(
				StringBundler.concat(
					"There are ", String.valueOf(size),
					" possible end nodes for ",
					getSchemaVersionString(bundleSymbolicName)));
		}

		if (size == 0) {
			return;
		}

		executeUpgradeInfos(bundleSymbolicName, upgradeInfosList.get(0));
	}

	protected void executeAll(
		Set<String> upgradeThrewExceptionBundleSymbolicNames) {

		Set<String> upgradableBundleSymbolicNames =
			getUpgradableBundleSymbolicNames();

		upgradableBundleSymbolicNames.removeAll(
			upgradeThrewExceptionBundleSymbolicNames);

		if (upgradableBundleSymbolicNames.isEmpty()) {
			return;
		}

		for (String upgradableBundleSymbolicName :
				upgradableBundleSymbolicNames) {

			try {
				doExecute(upgradableBundleSymbolicName, _serviceTrackerMap);
			}
			catch (Throwable t) {
				System.out.println(
					StringBundler.concat(
						"\nFailed upgrade process for module ",
						upgradableBundleSymbolicName, ":"));

				t.printStackTrace(System.out);

				upgradeThrewExceptionBundleSymbolicNames.add(
					upgradableBundleSymbolicName);
			}
		}

		executeAll(upgradeThrewExceptionBundleSymbolicNames);
	}

	protected void executeUpgradeInfos(
		final String bundleSymbolicName, final List<UpgradeInfo> upgradeInfos) {

		OutputStreamContainerFactory outputStreamContainerFactory =
			_outputStreamContainerFactoryTracker.
				getOutputStreamContainerFactory();

		OutputStreamContainer outputStreamContainer =
			outputStreamContainerFactory.create(
				"upgrade-" + bundleSymbolicName);

		OutputStream outputStream = outputStreamContainer.getOutputStream();

		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		if (release != null) {
			_releasePublisher.publishInProgress(release);
		}

		_outputStreamContainerFactoryTracker.runWithSwappedLog(
			new UpgradeInfosRunnable(
				bundleSymbolicName, upgradeInfos, outputStream),
			outputStreamContainer.getDescription(), outputStream);

		try {
			outputStream.close();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		if (release != null) {
			_releasePublisher.publish(release);
		}
	}

	protected String getSchemaVersionString(String bundleSymbolicName) {
		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		if ((release == null) || Validator.isNull(release.getSchemaVersion())) {
			return "0.0.0";
		}

		return release.getSchemaVersion();
	}

	protected Set<String> getUpgradableBundleSymbolicNames() {
		Set<String> upgradableBundleSymbolicNames = new HashSet<>();

		for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
			if (isUpgradable(bundleSymbolicName)) {
				upgradableBundleSymbolicNames.add(bundleSymbolicName);
			}
		}

		return upgradableBundleSymbolicNames;
	}

	protected List<List<UpgradeInfo>> getUpgradeInfosList(
		String bundleSymbolicName,
		ServiceTrackerMap<String, List<UpgradeInfo>> serviceTrackerMap) {

		String schemaVersionString = getSchemaVersionString(bundleSymbolicName);

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			serviceTrackerMap.getService(bundleSymbolicName));

		return releaseGraphManager.getUpgradeInfosList(schemaVersionString);
	}

	protected boolean isUpgradable(String bundleSymbolicName) {
		List<List<UpgradeInfo>> upgradeInfosList = getUpgradeInfosList(
			bundleSymbolicName, _serviceTrackerMap);

		if (upgradeInfosList.size() == 1) {
			return true;
		}

		return false;
	}

	@Reference(unbind = "-")
	protected void setReleaseLocalService(
		ReleaseLocalService releaseLocalService) {

		_releaseLocalService = releaseLocalService;
	}

	@Reference(unbind = "-")
	protected void setReleasePublisher(ReleasePublisher releasePublisher) {
		_releasePublisher = releasePublisher;
	}

	private static Logger _logger;

	private OutputStreamContainerFactoryTracker
		_outputStreamContainerFactoryTracker;
	private ReleaseLocalService _releaseLocalService;
	private ReleaseManagerConfiguration _releaseManagerConfiguration;
	private ReleasePublisher _releasePublisher;
	private ServiceTrackerMap<String, List<UpgradeInfo>> _serviceTrackerMap;

	private static class UpgradeServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<UpgradeStep, UpgradeInfo> {

		public UpgradeServiceTrackerCustomizer(BundleContext bundleContext) {
			_bundleContext = bundleContext;
		}

		@Override
		public UpgradeInfo addingService(
			ServiceReference<UpgradeStep> serviceReference) {

			String fromSchemaVersionString =
				(String)serviceReference.getProperty(
					"upgrade.from.schema.version");
			String toSchemaVersionString = (String)serviceReference.getProperty(
				"upgrade.to.schema.version");

			UpgradeStep upgradeStep = _bundleContext.getService(
				serviceReference);

			if (upgradeStep == null) {
				_logger.log(
					Logger.LOG_WARNING,
					"Skipping service " + serviceReference +
						" because it does not implement UpgradeStep");

				return null;
			}

			int buildNumber = 0;

			try {
				Class<? extends UpgradeStep> clazz = upgradeStep.getClass();

				Configuration configuration =
					ConfigurationFactoryUtil.getConfiguration(
						clazz.getClassLoader(), "service");

				Properties properties = configuration.getProperties();

				buildNumber = GetterUtil.getInteger(
					properties.getProperty("build.number"));
			}
			catch (Exception e) {
				_logger.log(
					Logger.LOG_DEBUG,
					"Unable to read service.properties for " +
						serviceReference);
			}

			return new UpgradeInfo(
				fromSchemaVersionString, toSchemaVersionString, buildNumber,
				upgradeStep);
		}

		@Override
		public void modifiedService(
			ServiceReference<UpgradeStep> serviceReference,
			UpgradeInfo upgradeInfo) {
		}

		@Override
		public void removedService(
			ServiceReference<UpgradeStep> serviceReference,
			UpgradeInfo upgradeInfo) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

	private class UpgradeInfoServiceTrackerMapListener
		implements ServiceTrackerMapListener
			<String, UpgradeInfo, List<UpgradeInfo>> {

		@Override
		public void keyEmitted(
			ServiceTrackerMap<String, List<UpgradeInfo>> serviceTrackerMap,
			final String key, UpgradeInfo upgradeInfo,
			List<UpgradeInfo> upgradeInfos) {

			doExecute(key, serviceTrackerMap);
		}

		@Override
		public void keyRemoved(
			ServiceTrackerMap<String, List<UpgradeInfo>> serviceTrackerMap,
			String key, UpgradeInfo upgradeInfo,
			List<UpgradeInfo> upgradeInfos) {
		}

	}

	private class UpgradeInfosRunnable implements Runnable {

		public UpgradeInfosRunnable(
			String bundleSymbolicName, List<UpgradeInfo> upgradeInfos,
			OutputStream outputStream) {

			_bundleSymbolicName = bundleSymbolicName;
			_upgradeInfos = upgradeInfos;
			_outputStream = outputStream;
		}

		@Override
		public void run() {
			int buildNumber = 0;
			int state = ReleaseConstants.STATE_GOOD;

			for (UpgradeInfo upgradeInfo : _upgradeInfos) {
				UpgradeStep upgradeStep = upgradeInfo.getUpgradeStep();

				try {
					_updateReleaseState(_STATE_IN_PROGRESS);

					upgradeStep.upgrade(
						new DBProcessContext() {

							@Override
							public DBContext getDBContext() {
								return new DBContext();
							}

							@Override
							public OutputStream getOutputStream() {
								return _outputStream;
							}

						});

					_releaseLocalService.updateRelease(
						_bundleSymbolicName,
						upgradeInfo.getToSchemaVersionString(),
						upgradeInfo.getFromSchemaVersionString());

					buildNumber = upgradeInfo.getBuildNumber();
				}
				catch (Exception e) {
					state = ReleaseConstants.STATE_UPGRADE_FAILURE;

					throw new RuntimeException(e);
				}
				finally {
					Release release = _releaseLocalService.fetchRelease(
						_bundleSymbolicName);

					if ((release != null) &&
						((buildNumber > 0) ||
						 (state == ReleaseConstants.STATE_UPGRADE_FAILURE))) {

						release.setBuildNumber(buildNumber);
						release.setState(state);

						_releaseLocalService.updateRelease(release);
					}
				}
			}

			_updateReleaseState(ReleaseConstants.STATE_GOOD);

			CacheRegistryUtil.clear();
		}

		private void _updateReleaseState(int state) {
			Release release = _releaseLocalService.fetchRelease(
				_bundleSymbolicName);

			if (release != null) {
				release.setState(state);

				_releaseLocalService.updateRelease(release);
			}
		}

		private static final int _STATE_IN_PROGRESS = -1;

		private final String _bundleSymbolicName;
		private final OutputStream _outputStream;
		private final List<UpgradeInfo> _upgradeInfos;

	}

}