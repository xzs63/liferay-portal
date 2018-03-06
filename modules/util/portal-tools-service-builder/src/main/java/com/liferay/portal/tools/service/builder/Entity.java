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

package com.liferay.portal.tools.service.builder;

import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class Entity implements Comparable<Entity> {

	public static final Accessor<Entity, String> NAME_ACCESSOR =
		new Accessor<Entity, String>() {

			@Override
			public String get(Entity entity) {
				return entity.getName();
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Entity> getTypeClass() {
				return Entity.class;
			}

		};

	public static EntityColumn getEntityColumn(
		String name, List<EntityColumn> entityColumns) {

		for (EntityColumn entityColumn : entityColumns) {
			if (name.equals(entityColumn.getName())) {
				return entityColumn;
			}
		}

		throw new RuntimeException("Entity column " + name + " not found");
	}

	public static boolean hasEntityColumn(
		String name, List<EntityColumn> entityColumns) {

		return hasEntityColumn(name, null, entityColumns);
	}

	public static boolean hasEntityColumn(
		String name, String type, List<EntityColumn> entityColumns) {

		int index = entityColumns.indexOf(new EntityColumn(name));

		if (index != -1) {
			EntityColumn entityColumn = entityColumns.get(index);

			if ((type == null) || type.equals(entityColumn.getType())) {
				return true;
			}
		}

		return false;
	}

	public Entity(String name) {
		this(
			null, null, null, null, name, null, null, null, false, false, false,
			true, null, null, null, null, null, true, false, false, false,
			false, false, null, null, null, null, null, null, null, null, null,
			null, false, null);
	}

	public Entity(
		String packagePath, String apiPackagePath, String portletName,
		String portletShortName, String name, String humanName, String table,
		String alias, boolean uuid, boolean uuidAccessor, boolean localService,
		boolean remoteService, String persistenceClass, String finderClassName,
		String dataSource, String sessionFactory, String txManager,
		boolean cacheEnabled, boolean dynamicUpdateEnabled, boolean jsonEnabled,
		boolean mvccEnabled, boolean trashEnabled, boolean deprecated,
		List<EntityColumn> pkEntityColumns,
		List<EntityColumn> regularEntityColumns,
		List<EntityColumn> blobEntityColumns,
		List<EntityColumn> collectionEntityColumns,
		List<EntityColumn> entityColumns, EntityOrder entityOrder,
		List<EntityFinder> entityFinders, List<Entity> referenceEntities,
		List<String> unresolvedReferenceEntityNames,
		List<String> txRequiredMethodNames, boolean resourceActionModel,
		String uadEntityTypeDescription) {

		_packagePath = packagePath;
		_apiPackagePath = apiPackagePath;
		_portletName = portletName;
		_portletShortName = portletShortName;
		_name = name;
		_table = table;
		_alias = alias;
		_uuid = uuid;
		_uuidAccessor = uuidAccessor;
		_localService = localService;
		_remoteService = remoteService;
		_persistenceClassName = persistenceClass;
		_finderClassName = finderClassName;
		_dynamicUpdateEnabled = dynamicUpdateEnabled;
		_jsonEnabled = jsonEnabled;
		_mvccEnabled = mvccEnabled;
		_trashEnabled = trashEnabled;
		_deprecated = deprecated;
		_pkEntityColumns = pkEntityColumns;
		_regularEntityColumns = regularEntityColumns;
		_blobEntityColumns = blobEntityColumns;
		_collectionEntityColumns = collectionEntityColumns;
		_entityColumns = entityColumns;
		_entityOrder = entityOrder;
		_entityFinders = entityFinders;
		_referenceEntities = referenceEntities;
		_unresolvedReferenceEntityNames = unresolvedReferenceEntityNames;
		_txRequiredMethodNames = txRequiredMethodNames;
		_resourceActionModel = resourceActionModel;
		_uadEntityTypeDescription = uadEntityTypeDescription;

		_humanName = GetterUtil.getString(
			humanName, ServiceBuilder.toHumanName(name));
		_dataSource = GetterUtil.getString(dataSource, _DATA_SOURCE_DEFAULT);
		_sessionFactory = GetterUtil.getString(
			sessionFactory, _SESSION_FACTORY_DEFAULT);
		_txManager = GetterUtil.getString(txManager, _TX_MANAGER_DEFAULT);

		if (_entityFinders != null) {
			Set<EntityColumn> finderEntityColumns = new HashSet<>();

			for (EntityFinder entityFinder : _entityFinders) {
				finderEntityColumns.addAll(entityFinder.getEntityColumns());
			}

			_finderEntityColumns = new ArrayList<>(finderEntityColumns);

			Collections.sort(_finderEntityColumns);
		}
		else {
			_finderEntityColumns = Collections.emptyList();
		}

		if ((_blobEntityColumns != null) && !_blobEntityColumns.isEmpty()) {
			for (EntityColumn entityColumn : _blobEntityColumns) {
				if (!entityColumn.isLazy()) {
					cacheEnabled = false;

					break;
				}
			}
		}

		_cacheEnabled = cacheEnabled;

		boolean containerModel = false;

		if ((_entityColumns != null) && !_entityColumns.isEmpty()) {
			for (EntityColumn entityColumn : _entityColumns) {
				if (entityColumn.isContainerModel() ||
					entityColumn.isParentContainerModel()) {

					containerModel = true;

					break;
				}
			}
		}

		_containerModel = containerModel;
	}

	public void addReferenceEntity(Entity referenceEntity) {
		_referenceEntities.add(referenceEntity);
	}

	@Override
	public int compareTo(Entity entity) {
		return _name.compareToIgnoreCase(entity._name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Entity)) {
			return false;
		}

		Entity entity = (Entity)obj;

		String name = entity.getName();

		if (_name.equals(name)) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getAlias() {
		return _alias;
	}

	public String getApiPackagePath() {
		return _apiPackagePath;
	}

	public List<EntityColumn> getBadEntityColumns() {
		List<EntityColumn> entityColumns = ListUtil.copy(_entityColumns);

		Iterator<EntityColumn> iterator = entityColumns.iterator();

		while (iterator.hasNext()) {
			EntityColumn entityColumn = iterator.next();

			String name = entityColumn.getName();

			if (name.equals(entityColumn.getDBName())) {
				iterator.remove();
			}
		}

		return entityColumns;
	}

	public List<EntityColumn> getBlobEntityColumns() {
		return _blobEntityColumns;
	}

	public List<EntityColumn> getCollectionEntityColumns() {
		return _collectionEntityColumns;
	}

	public List<EntityFinder> getCollectionEntityFinders() {
		List<EntityFinder> entityFinders = new ArrayList<>(
			_entityFinders.size());

		for (EntityFinder entityFinder : _entityFinders) {
			if (entityFinder.isCollection() &&
				!entityFinder.hasCustomComparator()) {

				entityFinders.add(entityFinder);
			}
		}

		return entityFinders;
	}

	public String getConstantName() {
		return TextFormatter.format(
			TextFormatter.format(_name, TextFormatter.H), TextFormatter.A);
	}

	public String getDataSource() {
		return _dataSource;
	}

	public EntityColumn getEntityColumn(String name) {
		return getEntityColumn(name, _entityColumns);
	}

	public List<EntityColumn> getEntityColumns() {
		return _entityColumns;
	}

	public List<EntityFinder> getEntityFinders() {
		return _entityFinders;
	}

	public EntityOrder getEntityOrder() {
		return _entityOrder;
	}

	public EntityColumn getFilterPKEntityColumn() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isFilterPrimary()) {
				return entityColumn;
			}
		}

		return _getPKEntityColumn();
	}

	public String getFinderClassName() {
		return _finderClassName;
	}

	public List<EntityColumn> getFinderEntityColumns() {
		return _finderEntityColumns;
	}

	public String getHumanName() {
		return _humanName;
	}

	public String getHumanNames() {
		return TextFormatter.formatPlural(_humanName);
	}

	public Entity getLocalizedEntity() {
		return _localizedEntity;
	}

	public List<EntityColumn> getLocalizedEntityColumns() {
		return _localizedEntityColumns;
	}

	public String getName() {
		return _name;
	}

	public String getNames() {
		return TextFormatter.formatPlural(_name);
	}

	public String getPackagePath() {
		return _packagePath;
	}

	public List<String> getParentTransients() {
		return _parentTransients;
	}

	public String getPersistenceClassName() {
		return _persistenceClassName;
	}

	public String getPKClassName() {
		if (hasCompoundPK()) {
			return _name + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getType();
	}

	public String getPKDBName() {
		if (hasCompoundPK()) {
			return getVarName() + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getDBName();
	}

	public List<EntityColumn> getPKEntityColumns() {
		return _pkEntityColumns;
	}

	public String getPKVarName() {
		if (hasCompoundPK()) {
			return getVarName() + "PK";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getName();
	}

	public String getPKVarNames() {
		if (hasCompoundPK()) {
			return getVarName() + "PKs";
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return entityColumn.getNames();
	}

	public String getPortletName() {
		return _portletName;
	}

	public String getPortletShortName() {
		return _portletShortName;
	}

	public List<Entity> getReferenceEntities() {
		return _referenceEntities;
	}

	public List<EntityColumn> getRegularEntityColumns() {
		return _regularEntityColumns;
	}

	public String getSessionFactory() {
		return _sessionFactory;
	}

	public String getShortName() {
		if (_name.startsWith(_portletShortName)) {
			return _name.substring(_portletShortName.length());
		}
		else {
			return _name;
		}
	}

	public String getSpringPropertyName() {
		return TextFormatter.format(_name, TextFormatter.L);
	}

	public String getTable() {
		return _table;
	}

	public List<String> getTransients() {
		return _transients;
	}

	public String getTXManager() {
		return _txManager;
	}

	public List<String> getTxRequiredMethodNames() {
		return _txRequiredMethodNames;
	}

	public Map<String, List<EntityColumn>>
		getUADAnonymizableEntityColumnsMap() {

		Map<String, List<EntityColumn>> uadAnonymizableEntityColumnsMap =
			new HashMap<>();

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADUserId()) {
				List<EntityColumn> uadAnonymizableEntityColumns =
					new ArrayList<>();

				uadAnonymizableEntityColumns.add(entityColumn);

				uadAnonymizableEntityColumnsMap.put(
					entityColumn.getName(), uadAnonymizableEntityColumns);
			}
		}

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADUserName()) {
				List<EntityColumn> uadAnonymizableEntityColumns =
					uadAnonymizableEntityColumnsMap.get(
						entityColumn.getUADUserIdColumnName());

				if (uadAnonymizableEntityColumns != null) {
					uadAnonymizableEntityColumns.add(entityColumn);
				}
			}
		}

		return uadAnonymizableEntityColumnsMap;
	}

	public String getUADEntityTypeDescription() {
		return _uadEntityTypeDescription;
	}

	public List<EntityColumn> getUADNonanonymizableEntityColumns() {
		List<EntityColumn> uadNonanonymizableEntityColumns = new ArrayList<>();

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADNonanonymizable()) {
				uadNonanonymizableEntityColumns.add(entityColumn);
			}
		}

		return uadNonanonymizableEntityColumns;
	}

	public List<String> getUADUserIdColumnNames() {
		List<String> uadUserIdColumnNames = new ArrayList<>();

		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADUserId()) {
				uadUserIdColumnNames.add(entityColumn.getName());
			}
		}

		return uadUserIdColumnNames;
	}

	public List<EntityFinder> getUniqueEntityFinders() {
		List<EntityFinder> entityFinders = ListUtil.copy(_entityFinders);

		Iterator<EntityFinder> iterator = entityFinders.iterator();

		while (iterator.hasNext()) {
			EntityFinder entityFinder = iterator.next();

			if (entityFinder.isCollection() && !entityFinder.isUnique()) {
				iterator.remove();
			}
		}

		return entityFinders;
	}

	public List<String> getUnresolvedResolvedReferenceEntityNames() {
		if (_unresolvedReferenceEntityNames == null) {
			return new ArrayList<>();
		}

		return _unresolvedReferenceEntityNames;
	}

	public String getVarName() {
		return TextFormatter.format(_name, TextFormatter.I);
	}

	public String getVarNames() {
		return TextFormatter.formatPlural(getVarName());
	}

	public boolean hasActionableDynamicQuery() {
		if (hasEntityColumns() && hasLocalService()) {
			if (hasCompoundPK()) {
				EntityColumn entityColumn = _pkEntityColumns.get(0);

				return entityColumn.isPrimitiveType();
			}
			else {
				return hasPrimitivePK();
			}
		}

		return false;
	}

	public boolean hasArrayableOperator() {
		for (EntityFinder finder : _entityFinders) {
			if (finder.hasArrayableOperator()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasCompoundPK() {
		if (_pkEntityColumns.size() > 1) {
			return true;
		}

		return false;
	}

	public boolean hasEagerBlobColumn() {
		if ((_blobEntityColumns == null) || _blobEntityColumns.isEmpty()) {
			return false;
		}

		for (EntityColumn blobEntityColumns : _blobEntityColumns) {
			if (!blobEntityColumns.isLazy()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasEntityColumn(String name) {
		return hasEntityColumn(name, _entityColumns);
	}

	public boolean hasEntityColumn(String name, String type) {
		return hasEntityColumn(name, type, _entityColumns);
	}

	public boolean hasEntityColumns() {
		if (ListUtil.isEmpty(_entityColumns)) {
			return false;
		}

		return true;
	}

	public boolean hasFinderClassName() {
		if (Validator.isNull(_finderClassName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	public boolean hasLazyBlobEntityColumn() {
		if ((_blobEntityColumns == null) || _blobEntityColumns.isEmpty()) {
			return false;
		}

		for (EntityColumn blobEntityColumns : _blobEntityColumns) {
			if (blobEntityColumns.isLazy()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasLocalService() {
		return _localService;
	}

	public boolean hasPrimitivePK() {
		return hasPrimitivePK(true);
	}

	public boolean hasPrimitivePK(boolean includeWrappers) {
		if (hasCompoundPK()) {
			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		if (entityColumn.isPrimitiveType(includeWrappers)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean hasRemoteService() {
		return _remoteService;
	}

	public boolean hasUuid() {
		return _uuid;
	}

	public boolean hasUuidAccessor() {
		return _uuidAccessor;
	}

	public boolean isAttachedModel() {
		if (!isTypedModel()) {
			return false;
		}

		if (hasEntityColumn("classPK")) {
			EntityColumn classPKEntityColumn = getEntityColumn("classPK");

			String classPKColType = classPKEntityColumn.getType();

			if (classPKColType.equals("long")) {
				return true;
			}
		}

		return false;
	}

	public boolean isAuditedModel() {
		if (hasEntityColumn("companyId") &&
			hasEntityColumn("createDate", "Date") &&
			hasEntityColumn("modifiedDate", "Date") &&
			hasEntityColumn("userId") && hasEntityColumn("userName")) {

			return true;
		}

		return false;
	}

	public boolean isCacheEnabled() {
		return _cacheEnabled;
	}

	public boolean isContainerModel() {
		return _containerModel;
	}

	public boolean isDefaultDataSource() {
		if (_dataSource.equals(_DATA_SOURCE_DEFAULT)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isDefaultSessionFactory() {
		if (_sessionFactory.equals(_SESSION_FACTORY_DEFAULT)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isDefaultTXManager() {
		if (_txManager.equals(_TX_MANAGER_DEFAULT)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	public boolean isDynamicUpdateEnabled() {
		return _dynamicUpdateEnabled;
	}

	public boolean isGroupedModel() {
		String pkVarName = getPKVarName();

		if (isAuditedModel() && hasEntityColumn("groupId") &&
			!pkVarName.equals("groupId")) {

			return true;
		}
		else {
			return false;
		}
	}

	public boolean isHierarchicalTree() {
		if (!hasPrimitivePK()) {
			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		String methodName = entityColumn.getMethodName();

		if ((_entityColumns.indexOf(new EntityColumn("parent" + methodName)) !=
				-1) &&
			(_entityColumns.indexOf(new EntityColumn("left" + methodName)) !=
				-1) &&
			(_entityColumns.indexOf(new EntityColumn("right" + methodName)) !=
				-1)) {

			return true;
		}
		else {
			return false;
		}
	}

	public boolean isJsonEnabled() {
		return _jsonEnabled;
	}

	public boolean isLocalizedModel() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isLocalized()) {
				return true;
			}
		}

		return false;
	}

	public boolean isMvccEnabled() {
		return _mvccEnabled;
	}

	public boolean isOrdered() {
		if (_entityOrder != null) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPermissionCheckEnabled() {
		for (EntityFinder entityFinder : _entityFinders) {
			if (isPermissionCheckEnabled(entityFinder)) {
				return true;
			}
		}

		return false;
	}

	public boolean isPermissionCheckEnabled(EntityFinder entityFinder) {
		String entityFinderName = entityFinder.getName();

		if (_name.equals("Group") || _name.equals("User") ||
			entityFinderName.equals("UUID_G") || !entityFinder.isCollection() ||
			!hasPrimitivePK() || !_resourceActionModel) {

			return false;
		}

		if (hasEntityColumn("groupId") &&
			!entityFinder.hasEntityColumn("groupId")) {

			return false;
		}

		EntityColumn entityColumn = _getPKEntityColumn();

		return StringUtil.equalsIgnoreCase("long", entityColumn.getType());
	}

	public boolean isPermissionedModel() {
		if (hasEntityColumn("resourceBlockId")) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPortalReference() {
		return _portalReference;
	}

	public boolean isResolved() {
		if ((_unresolvedReferenceEntityNames != null) &&
			_unresolvedReferenceEntityNames.isEmpty()) {

			return true;
		}

		return false;
	}

	public boolean isResourcedModel() {
		String pkVarName = getPKVarName();

		if (hasEntityColumn("resourcePrimKey") &&
			!pkVarName.equals("resourcePrimKey")) {

			return true;
		}
		else {
			return false;
		}
	}

	public boolean isShardedModel() {
		if (_packagePath.equals("com.liferay.portal") &&
			_name.equals("Company")) {

			return false;
		}

		return hasEntityColumn("companyId");
	}

	public boolean isStagedAuditedModel() {
		if (isAuditedModel() && isStagedModel()) {
			return true;
		}

		return false;
	}

	public boolean isStagedGroupedModel() {
		if (isGroupedModel() && isStagedModel() &&
			hasEntityColumn("lastPublishDate", "Date")) {

			return true;
		}

		return false;
	}

	public boolean isStagedModel() {
		if (hasUuid() && hasEntityColumn("companyId") &&
			hasEntityColumn("createDate", "Date") &&
			hasEntityColumn("modifiedDate", "Date")) {

			return true;
		}

		return false;
	}

	public boolean isTrashEnabled() {
		return _trashEnabled;
	}

	public boolean isTreeModel() {
		if (hasEntityColumn("treePath")) {
			return true;
		}

		return false;
	}

	public boolean isTypedModel() {
		if (hasEntityColumn("classNameId")) {
			EntityColumn classNameIdEntityColumn = getEntityColumn(
				"classNameId");

			String classNameIdColType = classNameIdEntityColumn.getType();

			if (classNameIdColType.equals("long")) {
				return true;
			}
		}

		return false;
	}

	public boolean isUADEnabled() {
		for (EntityColumn entityColumn : _entityColumns) {
			if (entityColumn.isUADEnabled()) {
				return true;
			}
		}

		return false;
	}

	public boolean isWorkflowEnabled() {
		if (hasEntityColumn("status") && hasEntityColumn("statusByUserId") &&
			hasEntityColumn("statusByUserName") &&
			hasEntityColumn("statusDate")) {

			return true;
		}
		else {
			return false;
		}
	}

	public void setLocalizedEntity(Entity localizedEntity) {
		_localizedEntity = localizedEntity;

		_referenceEntities.add(localizedEntity);
	}

	public void setLocalizedEntityColumns(
		List<EntityColumn> localizedEntityColumns) {

		_localizedEntityColumns = localizedEntityColumns;
	}

	public void setParentTransients(List<String> transients) {
		_parentTransients = transients;
	}

	public void setPortalReference(boolean portalReference) {
		_portalReference = portalReference;
	}

	public void setResolved() {
		_unresolvedReferenceEntityNames = null;
	}

	public void setTransients(List<String> transients) {
		_transients = transients;
	}

	private EntityColumn _getPKEntityColumn() {
		if (_pkEntityColumns.isEmpty()) {
			throw new RuntimeException(
				"There is no primary key for entity " + _name);
		}

		return _pkEntityColumns.get(0);
	}

	private static final String _DATA_SOURCE_DEFAULT = "liferayDataSource";

	private static final String _SESSION_FACTORY_DEFAULT =
		"liferaySessionFactory";

	private static final String _TX_MANAGER_DEFAULT =
		"liferayTransactionManager";

	private final String _alias;
	private final String _apiPackagePath;
	private List<EntityColumn> _blobEntityColumns;
	private final boolean _cacheEnabled;
	private final List<EntityColumn> _collectionEntityColumns;
	private final boolean _containerModel;
	private final String _dataSource;
	private final boolean _deprecated;
	private final boolean _dynamicUpdateEnabled;
	private final List<EntityColumn> _entityColumns;
	private final List<EntityFinder> _entityFinders;
	private final EntityOrder _entityOrder;
	private final String _finderClassName;
	private final List<EntityColumn> _finderEntityColumns;
	private final String _humanName;
	private final boolean _jsonEnabled;
	private Entity _localizedEntity;
	private List<EntityColumn> _localizedEntityColumns;
	private final boolean _localService;
	private final boolean _mvccEnabled;
	private final String _name;
	private final String _packagePath;
	private List<String> _parentTransients;
	private final String _persistenceClassName;
	private final List<EntityColumn> _pkEntityColumns;
	private boolean _portalReference;
	private final String _portletName;
	private final String _portletShortName;
	private final List<Entity> _referenceEntities;
	private final List<EntityColumn> _regularEntityColumns;
	private final boolean _remoteService;
	private final boolean _resourceActionModel;
	private final String _sessionFactory;
	private final String _table;
	private List<String> _transients;
	private final boolean _trashEnabled;
	private final String _txManager;
	private final List<String> _txRequiredMethodNames;
	private final String _uadEntityTypeDescription;
	private List<String> _unresolvedReferenceEntityNames;
	private final boolean _uuid;
	private final boolean _uuidAccessor;

}