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

package com.liferay.portal.search.elasticsearch6.internal.connection;

import java.util.function.Supplier;

/**
 * @author André de Oliveira
 */
public class PluginJarConflictCheckSuppression {

	public static void execute(Runnable runnable) {
		execute(
			() -> {
				runnable.run();

				return null;
			});
	}

	public static <T> T execute(Supplier<T> supplier) {
		String old = System.getProperty("java.class.path");

		System.setProperty("java.class.path", ".");

		String replaced = System.getProperty("java.class.path");

		try {
			return supplier.get();
		}
		finally {
			System.setProperty("java.class.path", replaced);

			System.setProperty("java.class.path", old);
		}
	}

}