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

package com.liferay.portal.search.elasticsearch6.internal.filter;

import com.liferay.portal.kernel.search.filter.MissingFilter;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = MissingFilterTranslator.class)
public class MissingFilterTranslatorImpl implements MissingFilterTranslator {

	@Override
	public QueryBuilder translate(MissingFilter missingFilter) {
		BoolQueryBuilder missingQueryBuilder = new BoolQueryBuilder().mustNot(
			new ExistsQueryBuilder(missingFilter.getField()));

		if (missingFilter.isExists() != null) {
			missingFilter.setExists(missingFilter.isExists());
		}

		if (missingFilter.isNullValue() != null) {
			missingFilter.setNullValue(missingFilter.isNullValue());
		}

		return missingQueryBuilder;
	}

}