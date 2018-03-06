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

package com.liferay.user.associated.data.util;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Noah Sherrill
 */
@Component(immediate = true, service = UADDynamicQueryHelper.class)
public class UADDynamicQueryHelper {

	public ActionableDynamicQuery addActionableDynamicQueryCriteria(
		ActionableDynamicQuery actionableDynamicQuery,
		final String[] userIdFieldNames, final long userId) {

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				_getCriterion(userIdFieldNames, userId)));

		return actionableDynamicQuery;
	}

	public DynamicQuery addDynamicQueryCriteria(
		DynamicQuery dynamicQuery, String[] userIdFieldNames, long userId) {

		dynamicQuery.add(_getCriterion(userIdFieldNames, userId));

		return dynamicQuery;
	}

	private Criterion _getCriterion(String[] userIdFieldNames, long userId) {
		Criterion criterion = RestrictionsFactoryUtil.eq(
			userIdFieldNames[0], userId);

		for (int i = 1; i < userIdFieldNames.length; i++) {
			Criterion userIdCriterion = RestrictionsFactoryUtil.eq(
				userIdFieldNames[i], userId);

			criterion = RestrictionsFactoryUtil.or(criterion, userIdCriterion);
		}

		return criterion;
	}

}