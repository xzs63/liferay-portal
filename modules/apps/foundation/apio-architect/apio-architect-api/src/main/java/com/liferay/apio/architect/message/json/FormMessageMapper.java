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

package com.liferay.apio.architect.message.json;

import aQute.bnd.annotation.ConsumerType;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.form.FormField;

import javax.ws.rs.core.HttpHeaders;

/**
 * Maps {@link Form} data to its representation in a JSON object. Instances of
 * this interface work like events. The {@code
 * javax.ws.rs.ext.MessageBodyWriter} of the {@code Form} calls the {@code
 * FormMessageMapper} methods. In each method, developers should only map the
 * provided part of the resource to its representation in a JSON object. To
 * enable this, each method receives a {@link JSONObjectBuilder}.
 *
 * The methods {@link #onStart(JSONObjectBuilder, Form, HttpHeaders)}
 * and {@link #onFinish(JSONObjectBuilder, Form, HttpHeaders)} are
 * called when the writer starts and finishes the single model item,
 * respectively. Otherwise, the message mapper's methods aren't called in a
 * particular order.
 *
 * @author Alejandro Hernández
 */
@ConsumerType
@SuppressWarnings("unused")
public interface FormMessageMapper {

	/**
	 * Returns the media type the mapper represents.
	 *
	 * @return the media type the mapper represents
	 */
	public String getMediaType();

	/**
	 * Maps the {@code Form} description to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param description the {@code Form} description
	 */
	public default void mapFormDescription(
		JSONObjectBuilder jsonObjectBuilder, String description) {
	}

	/**
	 * Maps a form field to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param formField a form's field
	 */
	public default void mapFormField(
		JSONObjectBuilder jsonObjectBuilder, FormField formField) {
	}

	/**
	 * Maps the {@code Form} title to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param title the {@code Form} title
	 */
	public default void mapFormTitle(
		JSONObjectBuilder jsonObjectBuilder, String title) {
	}

	/**
	 * Maps a form URL to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param url the form's URL
	 */
	public default void mapFormURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {
	}

	/**
	 * Finishes the form. This is the final form message mapper method the
	 * writer calls.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param form the form
	 * @param httpHeaders the current request's HTTP headers
	 */
	public default void onFinish(
		JSONObjectBuilder jsonObjectBuilder, Form form,
		HttpHeaders httpHeaders) {
	}

	/**
	 * Starts the form. This is the first form message mapper method the writer
	 * calls for the form.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param form the form
	 * @param httpHeaders the current request's HTTP headers
	 */
	public default void onStart(
		JSONObjectBuilder jsonObjectBuilder, Form form,
		HttpHeaders httpHeaders) {
	}

	/**
	 * Returns {@code true} if the mapper can map all things related to the
	 * current request.
	 *
	 * @param  form the form
	 * @param  httpHeaders the current request's HTTP headers
	 * @return {@code true} if the mapper can map the request; {@code false}
	 *         otherwise
	 */
	public default boolean supports(Form form, HttpHeaders httpHeaders) {
		return true;
	}

}