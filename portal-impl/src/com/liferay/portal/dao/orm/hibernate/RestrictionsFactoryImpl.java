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

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactory;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;

import java.util.Collection;
import java.util.Map;

import org.hibernate.criterion.Restrictions;

/**
 * @author Raymond Augé
 */
@DoPrivileged
public class RestrictionsFactoryImpl implements RestrictionsFactory {

	@Override
	public Criterion allEq(Map<String, Criterion> propertyNameValues) {
		return new CriterionImpl(Restrictions.allEq(propertyNameValues));
	}

	@Override
	public Criterion and(Criterion lhs, Criterion rhs) {
		CriterionImpl lhsImpl = (CriterionImpl)lhs;
		CriterionImpl rhsImpl = (CriterionImpl)rhs;

		return new CriterionImpl(
			Restrictions.and(
				lhsImpl.getWrappedCriterion(), rhsImpl.getWrappedCriterion()));
	}

	@Override
	public Criterion between(String propertyName, Object lo, Object hi) {
		return new CriterionImpl(Restrictions.between(propertyName, lo, hi));
	}

	@Override
	public Conjunction conjunction() {
		return new ConjunctionImpl(Restrictions.conjunction());
	}

	@Override
	public Disjunction disjunction() {
		return new DisjunctionImpl(Restrictions.disjunction());
	}

	@Override
	public Criterion eq(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.eq(propertyName, value));
	}

	@Override
	public Criterion eqProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.eqProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion ge(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.ge(propertyName, value));
	}

	@Override
	public Criterion geProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.geProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion gt(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.gt(propertyName, value));
	}

	@Override
	public Criterion gtProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.gtProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion ilike(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.ilike(propertyName, value));
	}

	@Override
	public Criterion in(String propertyName, Collection<?> values) {
		return new CriterionImpl(Restrictions.in(propertyName, values));
	}

	@Override
	public Criterion in(String propertyName, Object[] values) {
		return new CriterionImpl(Restrictions.in(propertyName, values));
	}

	@Override
	public Criterion isEmpty(String propertyName) {
		return new CriterionImpl(Restrictions.isEmpty(propertyName));
	}

	@Override
	public Criterion isNotEmpty(String propertyName) {
		return new CriterionImpl(Restrictions.isNotEmpty(propertyName));
	}

	@Override
	public Criterion isNotNull(String propertyName) {
		return new CriterionImpl(Restrictions.isNotNull(propertyName));
	}

	@Override
	public Criterion isNull(String propertyName) {
		return new CriterionImpl(Restrictions.isNull(propertyName));
	}

	@Override
	public Criterion le(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.le(propertyName, value));
	}

	@Override
	public Criterion leProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.leProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion like(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.like(propertyName, value));
	}

	@Override
	public Criterion lt(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.lt(propertyName, value));
	}

	@Override
	public Criterion ltProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.ltProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion ne(String propertyName, Object value) {
		return new CriterionImpl(Restrictions.ne(propertyName, value));
	}

	@Override
	public Criterion neProperty(String propertyName, String otherPropertyName) {
		return new CriterionImpl(
			Restrictions.neProperty(propertyName, otherPropertyName));
	}

	@Override
	public Criterion not(Criterion expression) {
		CriterionImpl expressionImpl = (CriterionImpl)expression;

		return new CriterionImpl(
			Restrictions.not(expressionImpl.getWrappedCriterion()));
	}

	@Override
	public Criterion or(Criterion lhs, Criterion rhs) {
		CriterionImpl lhsImpl = (CriterionImpl)lhs;
		CriterionImpl rhsImpl = (CriterionImpl)rhs;

		return new CriterionImpl(
			Restrictions.or(
				lhsImpl.getWrappedCriterion(), rhsImpl.getWrappedCriterion()));
	}

	@Override
	public Criterion sizeEq(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeEq(propertyName, size));
	}

	@Override
	public Criterion sizeGe(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeGe(propertyName, size));
	}

	@Override
	public Criterion sizeGt(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeGe(propertyName, size));
	}

	@Override
	public Criterion sizeLe(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeLe(propertyName, size));
	}

	@Override
	public Criterion sizeLt(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeLt(propertyName, size));
	}

	@Override
	public Criterion sizeNe(String propertyName, int size) {
		return new CriterionImpl(Restrictions.sizeNe(propertyName, size));
	}

	@Override
	public Criterion sqlRestriction(String sql) {
		return new CriterionImpl(Restrictions.sqlRestriction(sql));
	}

	@Override
	public Criterion sqlRestriction(String sql, Object value, Type type) {
		return new CriterionImpl(
			Restrictions.sqlRestriction(
				sql, value, TypeTranslator.translate(type)));
	}

	@Override
	public Criterion sqlRestriction(String sql, Object[] values, Type[] types) {
		org.hibernate.type.Type[] hibernateTypes =
			new org.hibernate.type.Type[types.length];

		for (int i = 0; i < types.length; i++) {
			hibernateTypes[i] = TypeTranslator.translate(types[i]);
		}

		return new CriterionImpl(
			Restrictions.sqlRestriction(sql, values, hibernateTypes));
	}

}