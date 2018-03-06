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

package com.liferay.apio.architect.application.internal.endpoint;

import static com.liferay.apio.architect.endpoint.ExceptionSupplierUtil.notAllowed;
import static com.liferay.apio.architect.endpoint.ExceptionSupplierUtil.notFound;
import static com.liferay.apio.architect.operation.Method.DELETE;
import static com.liferay.apio.architect.operation.Method.POST;
import static com.liferay.apio.architect.operation.Method.PUT;

import static javax.ws.rs.core.Response.noContent;

import com.liferay.apio.architect.endpoint.PageEndpoint;
import com.liferay.apio.architect.function.ThrowableFunction;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;
import com.liferay.apio.architect.uri.Path;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;

/**
 * @author Alejandro Hernández
 */
public class PageEndpointImpl<T, S> implements PageEndpoint<T> {

	public PageEndpointImpl(
		String name, HttpServletRequest httpServletRequest,
		Function<String, Optional<Class<Identifier>>> identifierClassFunction,
		Function<String, Try<SingleModel<T>>> singleModelFunction,
		Supplier<Optional<CollectionRoutes<T>>> collectionRoutesSupplier,
		Supplier<Optional<Representor<T, Object>>> representorSupplier,
		Supplier<Optional<ItemRoutes<T, S>>> itemRoutesSupplier,
		Function<String, Optional<NestedCollectionRoutes<T, Object>>>
			nestedCollectionRoutesFunction,
		Function<Path, S> identifierFunction) {

		_name = name;
		_httpServletRequest = httpServletRequest;
		_identifierClassFunction = identifierClassFunction;
		_singleModelFunction = singleModelFunction;
		_collectionRoutesSupplier = collectionRoutesSupplier;
		_representorSupplier = representorSupplier;
		_itemRoutesSupplier = itemRoutesSupplier;
		_nestedCollectionRoutesFunction = nestedCollectionRoutesFunction;
		_identifierFunction = identifierFunction;
	}

	@Override
	public Try<SingleModel<T>> addCollectionItem(Map<String, Object> body) {
		Try<CollectionRoutes<T>> collectionRoutesTry = Try.fromOptional(
			_collectionRoutesSupplier::get, notFound(_name));

		return collectionRoutesTry.mapOptional(
			CollectionRoutes::getCreateItemFunctionOptional,
			notAllowed(POST, _name)
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(body)
		);
	}

	@Override
	public Try<SingleModel<T>> addNestedCollectionItem(
		String id, String nestedName, Map<String, Object> body) {

		Try<NestedCollectionRoutes<T, Object>> nestedCollectionRoutesTry =
			Try.fromOptional(
				() -> _nestedCollectionRoutesFunction.apply(nestedName),
				notFound(_name, nestedName));

		return nestedCollectionRoutesTry.mapOptional(
			NestedCollectionRoutes::getNestedCreateItemFunctionOptional
		).flatMap(
			function -> {
				Try<SingleModel<T>> singleModelTry =
					getCollectionItemSingleModelTry(id);

				return singleModelTry.mapOptional(
					_getIdentifierFunction(nestedName)
				).map(
					identifier -> function.apply(
						_httpServletRequest
					).apply(
						identifier
					).apply(
						body
					)
				);
			}
		).mapFailMatching(
			NoSuchElementException.class,
			notAllowed(POST, _name, id, nestedName)
		);
	}

	@Override
	public Response deleteCollectionItem(String id) {
		Try<ItemRoutes<T, S>> itemRoutesTry = Try.fromOptional(
			_itemRoutesSupplier::get, notFound(_name));

		itemRoutesTry.mapOptional(
			ItemRoutes::getDeleteConsumerOptional, notAllowed(DELETE, _name, id)
		).ifSuccess(
			function -> function.apply(
				_httpServletRequest
			).accept(
				_identifierFunction.apply(new Path(_name, id))
			)
		);

		return noContent().build();
	}

	@Override
	public Try<SingleModel<T>> getCollectionItemSingleModelTry(String id) {
		return _singleModelFunction.apply(id);
	}

	@Override
	public Try<Page<T>> getCollectionPageTry() {
		Try<CollectionRoutes<T>> collectionRoutesTry = Try.fromOptional(
			_collectionRoutesSupplier::get, notFound(_name));

		return collectionRoutesTry.mapOptional(
			CollectionRoutes::getGetPageFunctionOptional, notFound(_name)
		).map(
			function -> function.apply(_httpServletRequest)
		);
	}

	@Override
	public Try<Page<T>> getNestedCollectionPageTry(
		String id, String nestedName) {

		Try<NestedCollectionRoutes<T, Object>> nestedCollectionRoutesTry =
			Try.fromOptional(
				() -> _nestedCollectionRoutesFunction.apply(nestedName),
				notFound(_name, id, nestedName));

		return nestedCollectionRoutesTry.map(
			NestedCollectionRoutes::getNestedGetPageFunctionOptional
		).map(
			Optional::get
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(new Path(_name, id))
		).flatMap(
			pageFunction -> {
				Try<SingleModel<T>> parentSingleModelTry =
					getCollectionItemSingleModelTry(id);

				return parentSingleModelTry.map(
					_getIdentifierFunction(nestedName)
				).map(
					optional -> optional.map(pageFunction)
				);
			}
		).map(
			Optional::get
		).mapFailMatching(
			NoSuchElementException.class, notFound(id, nestedName)
		);
	}

	@Override
	public Try<SingleModel<T>> updateCollectionItem(
		String id, Map<String, Object> body) {

		Try<ItemRoutes<T, S>> itemRoutesTry = Try.fromOptional(
			_itemRoutesSupplier::get, notFound(_name, id));

		return itemRoutesTry.mapOptional(
			ItemRoutes::getUpdateItemFunctionOptional,
			notAllowed(PUT, _name, id)
		).map(
			function -> function.apply(
				_httpServletRequest
			).compose(
				_identifierFunction
			).apply(
				new Path(_name, id)
			).apply(
				body
			)
		);
	}

	private Predicate<RelatedCollection<?>>
		_getFilterRelatedCollectionPredicate(String nestedName) {

		return relatedCollection -> {
			Class<?> relatedIdentifierClass =
				relatedCollection.getIdentifierClass();

			String className = relatedIdentifierClass.getName();

			Optional<Class<Identifier>> optional =
				_identifierClassFunction.apply(nestedName);

			return optional.map(
				Class::getName
			).map(
				className::equals
			).orElse(
				false
			);
		};
	}

	private ThrowableFunction<SingleModel<T>, Optional<Object>>
		_getIdentifierFunction(String nestedName) {

		return parentSingleModel -> {
			Optional<Representor<T, Object>> optional =
				_representorSupplier.get();

			return optional.map(
				Representor::getRelatedCollections
			).filter(
				stream -> stream.anyMatch(
					_getFilterRelatedCollectionPredicate(nestedName))
			).flatMap(
				__ -> _representorSupplier.get()
			).map(
				representor -> representor.getIdentifier(
					parentSingleModel.getModel())
			);
		};
	}

	private final Supplier<Optional<CollectionRoutes<T>>>
		_collectionRoutesSupplier;
	private final HttpServletRequest _httpServletRequest;
	private final Function<String, Optional<Class<Identifier>>>
		_identifierClassFunction;
	private final Function<Path, S> _identifierFunction;
	private final Supplier<Optional<ItemRoutes<T, S>>> _itemRoutesSupplier;
	private final String _name;
	private final Function<String, Optional<NestedCollectionRoutes<T, Object>>>
		_nestedCollectionRoutesFunction;
	private final Supplier<Optional<Representor<T, Object>>>
		_representorSupplier;
	private final Function<String, Try<SingleModel<T>>> _singleModelFunction;

}