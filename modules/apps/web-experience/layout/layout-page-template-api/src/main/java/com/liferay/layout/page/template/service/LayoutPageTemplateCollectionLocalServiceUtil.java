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

package com.liferay.layout.page.template.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.osgi.util.ServiceTrackerFactory;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for LayoutPageTemplateCollection. This utility wraps
 * {@link com.liferay.layout.page.template.service.impl.LayoutPageTemplateCollectionLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionLocalService
 * @see com.liferay.layout.page.template.service.base.LayoutPageTemplateCollectionLocalServiceBaseImpl
 * @see com.liferay.layout.page.template.service.impl.LayoutPageTemplateCollectionLocalServiceImpl
 * @generated
 */
@ProviderType
public class LayoutPageTemplateCollectionLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.layout.page.template.service.impl.LayoutPageTemplateCollectionLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the layout page template collection to the database. Also notifies the appropriate model listeners.
	*
	* @param layoutPageTemplateCollection the layout page template collection
	* @return the layout page template collection that was added
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection addLayoutPageTemplateCollection(
		com.liferay.layout.page.template.model.LayoutPageTemplateCollection layoutPageTemplateCollection) {
		return getService()
				   .addLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection addLayoutPageTemplateCollection(
		long userId, long groupId, java.lang.String name,
		java.lang.String description, int type,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .addLayoutPageTemplateCollection(userId, groupId, name,
			description, type, serviceContext);
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection addLayoutPageTemplateCollection(
		long userId, long groupId, java.lang.String name,
		java.lang.String description,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .addLayoutPageTemplateCollection(userId, groupId, name,
			description, serviceContext);
	}

	/**
	* Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	*
	* @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	* @return the new layout page template collection
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection createLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {
		return getService()
				   .createLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	/**
	* Deletes the layout page template collection from the database. Also notifies the appropriate model listeners.
	*
	* @param layoutPageTemplateCollection the layout page template collection
	* @return the layout page template collection that was removed
	* @throws PortalException
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
		com.liferay.layout.page.template.model.LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .deleteLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	/**
	* Deletes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	* @return the layout page template collection that was removed
	* @throws PortalException if a layout page template collection with the primary key could not be found
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .deleteLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	/**
	* @throws PortalException
	*/
	public static com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {
		return getService()
				   .fetchLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the layout page template collection with the primary key.
	*
	* @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	* @return the layout page template collection
	* @throws PortalException if a layout page template collection with the primary key could not be found
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection getLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	/**
	* Returns a range of all the layout page template collections.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of layout page template collections
	*/
	public static java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		int start, int end) {
		return getService().getLayoutPageTemplateCollections(start, end);
	}

	public static java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getLayoutPageTemplateCollections(groupId, start, end);
	}

	public static java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getLayoutPageTemplateCollections(groupId, start, end,
			orderByComparator);
	}

	public static java.util.List<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, java.lang.String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.layout.page.template.model.LayoutPageTemplateCollection> orderByComparator) {
		return getService()
				   .getLayoutPageTemplateCollections(groupId, name, start, end,
			orderByComparator);
	}

	/**
	* Returns the number of layout page template collections.
	*
	* @return the number of layout page template collections
	*/
	public static int getLayoutPageTemplateCollectionsCount() {
		return getService().getLayoutPageTemplateCollectionsCount();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static java.lang.String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the layout page template collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param layoutPageTemplateCollection the layout page template collection
	* @return the layout page template collection that was updated
	*/
	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
		com.liferay.layout.page.template.model.LayoutPageTemplateCollection layoutPageTemplateCollection) {
		return getService()
				   .updateLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	public static com.liferay.layout.page.template.model.LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId, java.lang.String name,
		java.lang.String description)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .updateLayoutPageTemplateCollection(layoutPageTemplateCollectionId,
			name, description);
	}

	public static LayoutPageTemplateCollectionLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<LayoutPageTemplateCollectionLocalService, LayoutPageTemplateCollectionLocalService> _serviceTracker =
		ServiceTrackerFactory.open(LayoutPageTemplateCollectionLocalService.class);
}