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

package com.liferay.layout.page.template.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;

import com.liferay.osgi.util.ServiceTrackerFactory;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the layout page template collection service. This utility wraps {@link com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateCollectionPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionPersistence
 * @see com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateCollectionPersistenceImpl
 * @generated
 */
@ProviderType
public class LayoutPageTemplateCollectionUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {
		getPersistence().clearCache(layoutPageTemplateCollection);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutPageTemplateCollection update(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {
		return getPersistence().update(layoutPageTemplateCollection);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutPageTemplateCollection update(
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		ServiceContext serviceContext) {
		return getPersistence()
				   .update(layoutPageTemplateCollection, serviceContext);
	}

	/**
	* Returns all the layout page template collections where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	* Returns a range of all the layout page template collections where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end) {
		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .findByGroupId(groupId, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByGroupId(groupId, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence().fetchByGroupId_First(groupId, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByGroupId_Last(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByGroupId_Last(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] findByGroupId_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .findByGroupId_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, orderByComparator);
	}

	/**
	* Returns all the layout page template collections that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	* Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end) {
		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .filterFindByGroupId(groupId, start, end, orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] filterFindByGroupId_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .filterFindByGroupId_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, orderByComparator);
	}

	/**
	* Removes all the layout page template collections where groupId = &#63; from the database.
	*
	* @param groupId the group ID
	*/
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	* Returns the number of layout page template collections where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching layout page template collections
	*/
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	* Returns the number of layout page template collections that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching layout page template collections that the user has permission to view
	*/
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	* Returns the layout page template collection where groupId = &#63; and name = &#63; or throws a {@link NoSuchPageTemplateCollectionException} if it could not be found.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByG_N(long groupId,
		java.lang.String name)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByG_N(groupId, name);
	}

	/**
	* Returns the layout page template collection where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_N(long groupId,
		java.lang.String name) {
		return getPersistence().fetchByG_N(groupId, name);
	}

	/**
	* Returns the layout page template collection where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param groupId the group ID
	* @param name the name
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_N(long groupId,
		java.lang.String name, boolean retrieveFromCache) {
		return getPersistence().fetchByG_N(groupId, name, retrieveFromCache);
	}

	/**
	* Removes the layout page template collection where groupId = &#63; and name = &#63; from the database.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the layout page template collection that was removed
	*/
	public static LayoutPageTemplateCollection removeByG_N(long groupId,
		java.lang.String name)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().removeByG_N(groupId, name);
	}

	/**
	* Returns the number of layout page template collections where groupId = &#63; and name = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the number of matching layout page template collections
	*/
	public static int countByG_N(long groupId, java.lang.String name) {
		return getPersistence().countByG_N(groupId, name);
	}

	/**
	* Returns all the layout page template collections where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_LikeN(
		long groupId, java.lang.String name) {
		return getPersistence().findByG_LikeN(groupId, name);
	}

	/**
	* Returns a range of all the layout page template collections where groupId = &#63; and name LIKE &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_LikeN(
		long groupId, java.lang.String name, int start, int end) {
		return getPersistence().findByG_LikeN(groupId, name, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_LikeN(
		long groupId, java.lang.String name, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .findByG_LikeN(groupId, name, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_LikeN(
		long groupId, java.lang.String name, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByG_LikeN(groupId, name, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByG_LikeN_First(
		long groupId, java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .findByG_LikeN_First(groupId, name, orderByComparator);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_LikeN_First(
		long groupId, java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .fetchByG_LikeN_First(groupId, name, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByG_LikeN_Last(
		long groupId, java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .findByG_LikeN_Last(groupId, name, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_LikeN_Last(
		long groupId, java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .fetchByG_LikeN_Last(groupId, name, orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] findByG_LikeN_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId,
		java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .findByG_LikeN_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, name, orderByComparator);
	}

	/**
	* Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN(
		long groupId, java.lang.String name) {
		return getPersistence().filterFindByG_LikeN(groupId, name);
	}

	/**
	* Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN(
		long groupId, java.lang.String name, int start, int end) {
		return getPersistence().filterFindByG_LikeN(groupId, name, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name LIKE &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN(
		long groupId, java.lang.String name, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .filterFindByG_LikeN(groupId, name, start, end,
			orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param name the name
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] filterFindByG_LikeN_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId,
		java.lang.String name,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .filterFindByG_LikeN_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, name, orderByComparator);
	}

	/**
	* Removes all the layout page template collections where groupId = &#63; and name LIKE &#63; from the database.
	*
	* @param groupId the group ID
	* @param name the name
	*/
	public static void removeByG_LikeN(long groupId, java.lang.String name) {
		getPersistence().removeByG_LikeN(groupId, name);
	}

	/**
	* Returns the number of layout page template collections where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the number of matching layout page template collections
	*/
	public static int countByG_LikeN(long groupId, java.lang.String name) {
		return getPersistence().countByG_LikeN(groupId, name);
	}

	/**
	* Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @return the number of matching layout page template collections that the user has permission to view
	*/
	public static int filterCountByG_LikeN(long groupId, java.lang.String name) {
		return getPersistence().filterCountByG_LikeN(groupId, name);
	}

	/**
	* Returns all the layout page template collections where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @return the matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_T(long groupId,
		int type) {
		return getPersistence().findByG_T(groupId, type);
	}

	/**
	* Returns a range of all the layout page template collections where groupId = &#63; and type = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param type the type
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_T(long groupId,
		int type, int start, int end) {
		return getPersistence().findByG_T(groupId, type, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param type the type
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_T(long groupId,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .findByG_T(groupId, type, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param type the type
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findByG_T(long groupId,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByG_T(groupId, type, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByG_T_First(long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByG_T_First(groupId, type, orderByComparator);
	}

	/**
	* Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_T_First(long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .fetchByG_T_First(groupId, type, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection findByG_T_Last(long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByG_T_Last(groupId, type, orderByComparator);
	}

	/**
	* Returns the last layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	*/
	public static LayoutPageTemplateCollection fetchByG_T_Last(long groupId,
		int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence().fetchByG_T_Last(groupId, type, orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] findByG_T_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .findByG_T_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, type, orderByComparator);
	}

	/**
	* Returns all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @return the matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type) {
		return getPersistence().filterFindByG_T(groupId, type);
	}

	/**
	* Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param type the type
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end) {
		return getPersistence().filterFindByG_T(groupId, type, start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and type = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param type the type
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching layout page template collections that the user has permission to view
	*/
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence()
				   .filterFindByG_T(groupId, type, start, end, orderByComparator);
	}

	/**
	* Returns the layout page template collections before and after the current layout page template collection in the ordered set of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	*
	* @param layoutPageTemplateCollectionId the primary key of the current layout page template collection
	* @param groupId the group ID
	* @param type the type
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection[] filterFindByG_T_PrevAndNext(
		long layoutPageTemplateCollectionId, long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence()
				   .filterFindByG_T_PrevAndNext(layoutPageTemplateCollectionId,
			groupId, type, orderByComparator);
	}

	/**
	* Removes all the layout page template collections where groupId = &#63; and type = &#63; from the database.
	*
	* @param groupId the group ID
	* @param type the type
	*/
	public static void removeByG_T(long groupId, int type) {
		getPersistence().removeByG_T(groupId, type);
	}

	/**
	* Returns the number of layout page template collections where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @return the number of matching layout page template collections
	*/
	public static int countByG_T(long groupId, int type) {
		return getPersistence().countByG_T(groupId, type);
	}

	/**
	* Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	*
	* @param groupId the group ID
	* @param type the type
	* @return the number of matching layout page template collections that the user has permission to view
	*/
	public static int filterCountByG_T(long groupId, int type) {
		return getPersistence().filterCountByG_T(groupId, type);
	}

	/**
	* Caches the layout page template collection in the entity cache if it is enabled.
	*
	* @param layoutPageTemplateCollection the layout page template collection
	*/
	public static void cacheResult(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {
		getPersistence().cacheResult(layoutPageTemplateCollection);
	}

	/**
	* Caches the layout page template collections in the entity cache if it is enabled.
	*
	* @param layoutPageTemplateCollections the layout page template collections
	*/
	public static void cacheResult(
		List<LayoutPageTemplateCollection> layoutPageTemplateCollections) {
		getPersistence().cacheResult(layoutPageTemplateCollections);
	}

	/**
	* Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	*
	* @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	* @return the new layout page template collection
	*/
	public static LayoutPageTemplateCollection create(
		long layoutPageTemplateCollectionId) {
		return getPersistence().create(layoutPageTemplateCollectionId);
	}

	/**
	* Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	* @return the layout page template collection that was removed
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection remove(
		long layoutPageTemplateCollectionId)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().remove(layoutPageTemplateCollectionId);
	}

	public static LayoutPageTemplateCollection updateImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {
		return getPersistence().updateImpl(layoutPageTemplateCollection);
	}

	/**
	* Returns the layout page template collection with the primary key or throws a {@link NoSuchPageTemplateCollectionException} if it could not be found.
	*
	* @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	* @return the layout page template collection
	* @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection findByPrimaryKey(
		long layoutPageTemplateCollectionId)
		throws com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException {
		return getPersistence().findByPrimaryKey(layoutPageTemplateCollectionId);
	}

	/**
	* Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	* @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	*/
	public static LayoutPageTemplateCollection fetchByPrimaryKey(
		long layoutPageTemplateCollectionId) {
		return getPersistence().fetchByPrimaryKey(layoutPageTemplateCollectionId);
	}

	public static java.util.Map<java.io.Serializable, LayoutPageTemplateCollection> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the layout page template collections.
	*
	* @return the layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the layout page template collections.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @return the range of layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the layout page template collections.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findAll(int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the layout page template collections.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link LayoutPageTemplateCollectionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of layout page template collections
	* @param end the upper bound of the range of layout page template collections (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of layout page template collections
	*/
	public static List<LayoutPageTemplateCollection> findAll(int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the layout page template collections from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of layout page template collections.
	*
	* @return the number of layout page template collections
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static java.util.Set<java.lang.String> getBadColumnNames() {
		return getPersistence().getBadColumnNames();
	}

	public static LayoutPageTemplateCollectionPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<LayoutPageTemplateCollectionPersistence, LayoutPageTemplateCollectionPersistence> _serviceTracker =
		ServiceTrackerFactory.open(LayoutPageTemplateCollectionPersistence.class);
}