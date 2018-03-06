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

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQLUtil;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

/**
 * @author Leonardo Barros
 */
public class DDMFormInstanceFinderImpl
	extends DDMFormInstanceFinderBaseImpl implements DDMFormInstanceFinder {

	public static final String COUNT_BY_C_G_N_D =
		DDMFormInstanceFinder.class.getName() + ".countByC_G_N_D";

	public static final String FIND_BY_C_G_N_D =
		DDMFormInstanceFinder.class.getName() + ".findByC_G_N_D";

	@Override
	public int countByKeywords(long companyId, long groupId, String keywords) {
		return doCountByKeywords(companyId, groupId, keywords, false);
	}

	@Override
	public int countByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return doCountByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, false);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long groupId, String keywords) {

		return doCountByKeywords(companyId, groupId, keywords, true);
	}

	@Override
	public int filterCountByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator) {

		return doCountByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, true);
	}

	@Override
	public List<DDMFormInstance> filterFindByKeywords(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<DDMFormInstance> filterFindByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return doFindByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator, true);
	}

	@Override
	public List<DDMFormInstance> findByKeywords(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return findByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<DDMFormInstance> findByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator) {

		return doFindByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator, start, end,
			orderByComparator, false);
	}

	protected int doCountByKeywords(
		long companyId, long groupId, String keywords,
		boolean inlineSQLHelper) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doCountByC_G_N_D(
			companyId, groupId, names, descriptions, andOperator,
			inlineSQLHelper);
	}

	protected int doCountByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(getClass(), COUNT_BY_C_G_N_D);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DDMFormInstance.class.getName(),
					"DDMFormInstance.formInstanceId", groupId);
			}

			if (groupId <= 0) {
				sql = StringUtil.replace(
					sql, "(DDMFormInstance.groupId = ?) AND", StringPool.BLANK);
			}

			sql = CustomSQLUtil.replaceKeywords(
				sql, "lower(DDMFormInstance.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DDMFormInstance.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			SQLQuery q = session.createSynchronizedSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(companyId);

			if (groupId > 0) {
				qPos.add(groupId);
			}

			qPos.add(names, 2);
			qPos.add(descriptions, 2);

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<DDMFormInstance> doFindByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator, int start, int end,
		OrderByComparator<DDMFormInstance> orderByComparator,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(getClass(), FIND_BY_C_G_N_D);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DDMFormInstance.class.getName(),
					"DDMFormInstance.formInstanceId", groupId);
			}

			if (groupId <= 0) {
				sql = StringUtil.replace(
					sql, "(DDMFormInstance.groupId = ?) AND", StringPool.BLANK);
			}

			sql = CustomSQLUtil.replaceKeywords(
				sql, "lower(DDMFormInstance.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DDMFormInstance.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);
			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			SQLQuery q = session.createSynchronizedSQLQuery(sql);

			q.addEntity("DDMFormInstance", DDMFormInstanceImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(companyId);

			if (groupId > 0) {
				qPos.add(groupId);
			}

			qPos.add(names, 2);
			qPos.add(descriptions, 2);

			return (List<DDMFormInstance>)QueryUtil.list(
				q, getDialect(), start, end);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

}