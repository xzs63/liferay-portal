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

package com.liferay.fragment.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.model.FragmentEntryLink;

import com.liferay.osgi.util.ServiceTrackerFactory;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the fragment entry link service. This utility wraps {@link com.liferay.fragment.service.persistence.impl.FragmentEntryLinkPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkPersistence
 * @see com.liferay.fragment.service.persistence.impl.FragmentEntryLinkPersistenceImpl
 * @generated
 */
@ProviderType
public class FragmentEntryLinkUtil {
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
	public static void clearCache(FragmentEntryLink fragmentEntryLink) {
		getPersistence().clearCache(fragmentEntryLink);
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
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FragmentEntryLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FragmentEntryLink update(FragmentEntryLink fragmentEntryLink) {
		return getPersistence().update(fragmentEntryLink);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FragmentEntryLink update(
		FragmentEntryLink fragmentEntryLink, ServiceContext serviceContext) {
		return getPersistence().update(fragmentEntryLink, serviceContext);
	}

	/**
	* Returns all the fragment entry links where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	* Returns a range of all the fragment entry links where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @return the range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByGroupId(long groupId,
		int start, int end) {
		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByGroupId(long groupId,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .findByGroupId(groupId, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByGroupId(long groupId,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByGroupId(groupId, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByGroupId_First(long groupId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByGroupId_First(long groupId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence().fetchByGroupId_First(groupId, orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByGroupId_Last(long groupId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByGroupId_Last(long groupId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	* Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63;.
	*
	* @param fragmentEntryLinkId the primary key of the current fragment entry link
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next fragment entry link
	* @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink[] findByGroupId_PrevAndNext(
		long fragmentEntryLinkId, long groupId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByGroupId_PrevAndNext(fragmentEntryLinkId, groupId,
			orderByComparator);
	}

	/**
	* Removes all the fragment entry links where groupId = &#63; from the database.
	*
	* @param groupId the group ID
	*/
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	* Returns the number of fragment entry links where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching fragment entry links
	*/
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	* Returns all the fragment entry links where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @return the matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_F(long groupId,
		long fragmentEntryId) {
		return getPersistence().findByG_F(groupId, fragmentEntryId);
	}

	/**
	* Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @return the range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_F(long groupId,
		long fragmentEntryId, int start, int end) {
		return getPersistence().findByG_F(groupId, fragmentEntryId, start, end);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_F(long groupId,
		long fragmentEntryId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .findByG_F(groupId, fragmentEntryId, start, end,
			orderByComparator);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_F(long groupId,
		long fragmentEntryId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByG_F(groupId, fragmentEntryId, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByG_F_First(long groupId,
		long fragmentEntryId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_F_First(groupId, fragmentEntryId, orderByComparator);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByG_F_First(long groupId,
		long fragmentEntryId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .fetchByG_F_First(groupId, fragmentEntryId, orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByG_F_Last(long groupId,
		long fragmentEntryId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_F_Last(groupId, fragmentEntryId, orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByG_F_Last(long groupId,
		long fragmentEntryId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .fetchByG_F_Last(groupId, fragmentEntryId, orderByComparator);
	}

	/**
	* Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param fragmentEntryLinkId the primary key of the current fragment entry link
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next fragment entry link
	* @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink[] findByG_F_PrevAndNext(
		long fragmentEntryLinkId, long groupId, long fragmentEntryId,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_F_PrevAndNext(fragmentEntryLinkId, groupId,
			fragmentEntryId, orderByComparator);
	}

	/**
	* Removes all the fragment entry links where groupId = &#63; and fragmentEntryId = &#63; from the database.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	*/
	public static void removeByG_F(long groupId, long fragmentEntryId) {
		getPersistence().removeByG_F(groupId, fragmentEntryId);
	}

	/**
	* Returns the number of fragment entry links where groupId = &#63; and fragmentEntryId = &#63;.
	*
	* @param groupId the group ID
	* @param fragmentEntryId the fragment entry ID
	* @return the number of matching fragment entry links
	*/
	public static int countByG_F(long groupId, long fragmentEntryId) {
		return getPersistence().countByG_F(groupId, fragmentEntryId);
	}

	/**
	* Returns all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @return the matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_C_C(long groupId,
		long classNameId, long classPK) {
		return getPersistence().findByG_C_C(groupId, classNameId, classPK);
	}

	/**
	* Returns a range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @return the range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_C_C(long groupId,
		long classNameId, long classPK, int start, int end) {
		return getPersistence()
				   .findByG_C_C(groupId, classNameId, classPK, start, end);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_C_C(long groupId,
		long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .findByG_C_C(groupId, classNameId, classPK, start, end,
			orderByComparator);
	}

	/**
	* Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching fragment entry links
	*/
	public static List<FragmentEntryLink> findByG_C_C(long groupId,
		long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByG_C_C(groupId, classNameId, classPK, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByG_C_C_First(long groupId,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_C_C_First(groupId, classNameId, classPK,
			orderByComparator);
	}

	/**
	* Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByG_C_C_First(long groupId,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .fetchByG_C_C_First(groupId, classNameId, classPK,
			orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link
	* @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink findByG_C_C_Last(long groupId,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_C_C_Last(groupId, classNameId, classPK,
			orderByComparator);
	}

	/**
	* Returns the last fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	*/
	public static FragmentEntryLink fetchByG_C_C_Last(long groupId,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence()
				   .fetchByG_C_C_Last(groupId, classNameId, classPK,
			orderByComparator);
	}

	/**
	* Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param fragmentEntryLinkId the primary key of the current fragment entry link
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next fragment entry link
	* @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink[] findByG_C_C_PrevAndNext(
		long fragmentEntryLinkId, long groupId, long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence()
				   .findByG_C_C_PrevAndNext(fragmentEntryLinkId, groupId,
			classNameId, classPK, orderByComparator);
	}

	/**
	* Removes all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	*/
	public static void removeByG_C_C(long groupId, long classNameId,
		long classPK) {
		getPersistence().removeByG_C_C(groupId, classNameId, classPK);
	}

	/**
	* Returns the number of fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	*
	* @param groupId the group ID
	* @param classNameId the class name ID
	* @param classPK the class pk
	* @return the number of matching fragment entry links
	*/
	public static int countByG_C_C(long groupId, long classNameId, long classPK) {
		return getPersistence().countByG_C_C(groupId, classNameId, classPK);
	}

	/**
	* Caches the fragment entry link in the entity cache if it is enabled.
	*
	* @param fragmentEntryLink the fragment entry link
	*/
	public static void cacheResult(FragmentEntryLink fragmentEntryLink) {
		getPersistence().cacheResult(fragmentEntryLink);
	}

	/**
	* Caches the fragment entry links in the entity cache if it is enabled.
	*
	* @param fragmentEntryLinks the fragment entry links
	*/
	public static void cacheResult(List<FragmentEntryLink> fragmentEntryLinks) {
		getPersistence().cacheResult(fragmentEntryLinks);
	}

	/**
	* Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	*
	* @param fragmentEntryLinkId the primary key for the new fragment entry link
	* @return the new fragment entry link
	*/
	public static FragmentEntryLink create(long fragmentEntryLinkId) {
		return getPersistence().create(fragmentEntryLinkId);
	}

	/**
	* Removes the fragment entry link with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param fragmentEntryLinkId the primary key of the fragment entry link
	* @return the fragment entry link that was removed
	* @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink remove(long fragmentEntryLinkId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence().remove(fragmentEntryLinkId);
	}

	public static FragmentEntryLink updateImpl(
		FragmentEntryLink fragmentEntryLink) {
		return getPersistence().updateImpl(fragmentEntryLink);
	}

	/**
	* Returns the fragment entry link with the primary key or throws a {@link NoSuchEntryLinkException} if it could not be found.
	*
	* @param fragmentEntryLinkId the primary key of the fragment entry link
	* @return the fragment entry link
	* @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink findByPrimaryKey(long fragmentEntryLinkId)
		throws com.liferay.fragment.exception.NoSuchEntryLinkException {
		return getPersistence().findByPrimaryKey(fragmentEntryLinkId);
	}

	/**
	* Returns the fragment entry link with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param fragmentEntryLinkId the primary key of the fragment entry link
	* @return the fragment entry link, or <code>null</code> if a fragment entry link with the primary key could not be found
	*/
	public static FragmentEntryLink fetchByPrimaryKey(long fragmentEntryLinkId) {
		return getPersistence().fetchByPrimaryKey(fragmentEntryLinkId);
	}

	public static java.util.Map<java.io.Serializable, FragmentEntryLink> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the fragment entry links.
	*
	* @return the fragment entry links
	*/
	public static List<FragmentEntryLink> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the fragment entry links.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @return the range of fragment entry links
	*/
	public static List<FragmentEntryLink> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the fragment entry links.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of fragment entry links
	*/
	public static List<FragmentEntryLink> findAll(int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the fragment entry links.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link FragmentEntryLinkModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of fragment entry links
	* @param end the upper bound of the range of fragment entry links (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of fragment entry links
	*/
	public static List<FragmentEntryLink> findAll(int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the fragment entry links from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of fragment entry links.
	*
	* @return the number of fragment entry links
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static FragmentEntryLinkPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<FragmentEntryLinkPersistence, FragmentEntryLinkPersistence> _serviceTracker =
		ServiceTrackerFactory.open(FragmentEntryLinkPersistence.class);
}