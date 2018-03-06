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

package com.liferay.apio.architect.error;

import com.liferay.apio.architect.uri.Path;

/**
 * Represents the errors that can occur while using Apio. Each error is a nested
 * error subclass.
 *
 * @author Alejandro Hernández
 * @author Jorge Ferrer
 */
public class ApioDeveloperError extends Error {

	/**
	 * Represents the error the developer should throw when a documentation
	 * message mapper is missing.
	 */
	public static class MustHaveDocumentationMessageMapper
		extends ApioDeveloperError {

		public MustHaveDocumentationMessageMapper(String mediaType) {
			super(
				"Documentation media type " + mediaType +
					" does not have a message mapper");
		}

	}

	/**
	 * Represents the error the developer should throw when an exception
	 * converter is missing.
	 */
	public static class MustHaveExceptionConverter extends ApioDeveloperError {

		public MustHaveExceptionConverter(Class<?> exceptionClass) {
			super(
				"Exception class " + exceptionClass.getName() +
					" does not have a converter");
		}

	}

	/**
	 * Represents the error the developer should throw when a form message
	 * mapper is missing.
	 */
	public static class MustHaveFormMessageMapper extends ApioDeveloperError {

		public MustHaveFormMessageMapper(String mediaType) {
			super(
				"Form media type " + mediaType +
					" does not have a message mapper");
		}

	}

	/**
	 * Represents the error the developer should throw when a message mapper is
	 * missing.
	 */
	public static class MustHaveMessageMapper extends ApioDeveloperError {

		public MustHaveMessageMapper(String mediaType, String name) {
			super(
				"Media type " + mediaType + " and name " + name + " does not " +
					"have a message mapper");
		}

	}

	/**
	 * Represents the error the developer should throw when an identifier's path
	 * mapper is missing.
	 */
	public static class MustHavePathIdentifierMapper
		extends ApioDeveloperError {

		public MustHavePathIdentifierMapper(Path path) {
			super(path.asURI() + " path does not have a valid path mapper");
		}

	}

	/**
	 * Represents the error the developer should throw when a problem JSON error
	 * message mapper is missing.
	 */
	public static class MustHaveProblemJSONErrorMessageMapper
		extends ApioDeveloperError {

		public MustHaveProblemJSONErrorMessageMapper() {
			super(
				"Media type application/problem+json does not have a message " +
					"mapper");
		}

	}

	/**
	 * Represents the error the developer should throw when a generic container
	 * has an invalid generic type.
	 */
	public static class MustHaveValidGenericType extends ApioDeveloperError {

		public MustHaveValidGenericType(Class clazz) {
			super(
				"Class " + clazz.getName() + " must have a valid generic type");
		}

	}

	private ApioDeveloperError(String message) {
		super(message);
	}

}