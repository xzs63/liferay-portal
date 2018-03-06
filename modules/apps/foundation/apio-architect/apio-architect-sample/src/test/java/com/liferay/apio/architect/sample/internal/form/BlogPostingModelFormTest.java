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

package com.liferay.apio.architect.sample.internal.form;

import static com.liferay.apio.architect.form.Form.Builder.empty;
import static com.liferay.apio.architect.sample.internal.form.BlogPostingForm.buildForm;
import static com.liferay.apio.architect.test.util.form.FormMatchers.isAFormWithConditions;
import static com.liferay.apio.architect.test.util.form.FormMatchers.isReturnedIn;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class BlogPostingModelFormTest {

	@Test
	public void test() {
		assertThat(
			buildForm(empty()),
			isAFormWithConditions(
				builder -> builder.whereLong(
					"creator", isReturnedIn(BlogPostingForm::getCreator)
				).whereString(
					"alternativeHeadline",
					isReturnedIn(BlogPostingForm::getAlternativeHeadline)
				).whereString(
					"articleBody", isReturnedIn(BlogPostingForm::getArticleBody)
				).whereString(
					"headline", isReturnedIn(BlogPostingForm::getHeadline)
				).build()));
	}

}