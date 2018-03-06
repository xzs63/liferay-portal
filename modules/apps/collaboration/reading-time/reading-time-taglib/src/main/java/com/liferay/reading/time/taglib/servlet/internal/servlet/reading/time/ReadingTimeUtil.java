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

package com.liferay.reading.time.taglib.servlet.internal.servlet.reading.time;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.reading.time.message.ReadingTimeMessageProvider;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Sergio González
 */
@Component(immediate = true)
public class ReadingTimeUtil {

	public static ReadingTimeMessageProvider getReadingTimeMessageProvider(
		String displayStyle) {

		ReadingTimeMessageProvider readingTimeMessageProvider =
			_instance._serviceTrackerMap.getService(displayStyle);

		if (readingTimeMessageProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format(
						"Reading time provider \"%s\" is not available",
						displayStyle));
			}
		}

		return readingTimeMessageProvider;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_instance = this;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ReadingTimeMessageProvider.class, "display.style");
	}

	@Deactivate
	protected void deactivate() {
		_instance = null;

		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReadingTimeUtil.class);

	private static ReadingTimeUtil _instance;

	private ServiceTrackerMap<String, ReadingTimeMessageProvider>
		_serviceTrackerMap;

}