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

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.portal.cache.key.HashCodeHexStringCacheKeyGenerator;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class FinderCacheImplTest {

	@BeforeClass
	public static void setUpClass() {
		_props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler());

		_serializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, true));
		_notSerializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, false));

		RegistryUtil.setRegistry(new BasicRegistryImpl());

		CacheKeyGeneratorUtil cacheKeyGeneratorUtil =
			new CacheKeyGeneratorUtil();

		cacheKeyGeneratorUtil.setDefaultCacheKeyGenerator(_cacheKeyGenerator);
	}

	@Before
	public void setUp() {
		_finderPath = new FinderPath(
			true, true, FinderCacheImplTest.class,
			FinderCacheImplTest.class.getName(), "test",
			new String[] {String.class.getName()});
	}

	@Test
	public void testPutEmptyListInvalid() {
		_assertPutEmptyListInvalid(_notSerializedMultiVMPool);
		_assertPutEmptyListInvalid(_serializedMultiVMPool);
	}

	@Test
	public void testPutEmptyListValid() {
		_assertPutEmptyListValid(_notSerializedMultiVMPool);
		_assertPutEmptyListValid(_serializedMultiVMPool);
	}

	@Test
	public void testTestKeysCollide() {
		Assert.assertEquals(
			_cacheKeyGenerator.getCacheKey(_KEY1),
			_cacheKeyGenerator.getCacheKey(_KEY2));
	}

	@Test
	public void testThreshold() {
		_props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler() {

				@Override
				public Object invoke(
					Object proxy, Method method, Object[] args) {

					String methodName = method.getName();

					if (methodName.equals("get")) {
						String key = (String)args[0];

						if (PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD.
								equals(key)) {

							return "2";
						}
					}

					return super.invoke(proxy, method, args);
				}

			});

		FinderCache finderCache = _activateFinderCache(
			_notSerializedMultiVMPool);

		List<Serializable> values = new ArrayList<>();

		values.add("a");
		values.add("b");

		finderCache.putResult(_finderPath, _KEY1, values, true);

		Object result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertEquals(values, result);

		values.add("c");

		finderCache.putResult(_finderPath, _KEY1, values, true);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertNull(result);
	}

	private FinderCache _activateFinderCache(MultiVMPool multiVMPool) {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		entityCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setEntityCache(entityCacheImpl);

		finderCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setProps(_props);

		finderCacheImpl.activate();

		return finderCacheImpl;
	}

	private void _assertPutEmptyListInvalid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		Object result = finderCache.getResult(_finderPath, _KEY2, null);

		Assert.assertNull(result);
	}

	private void _assertPutEmptyListValid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		Object result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertSame(Collections.emptyList(), result);
	}

	private static final String[] _KEY1 = {"home"};

	private static final String[] _KEY2 = {"j1me"};

	private static final CacheKeyGenerator _cacheKeyGenerator =
		new HashCodeHexStringCacheKeyGenerator();
	private static final ClassLoader _classLoader =
		FinderCacheImplTest.class.getClassLoader();
	private static MultiVMPool _notSerializedMultiVMPool;
	private static Props _props;
	private static MultiVMPool _serializedMultiVMPool;

	private FinderPath _finderPath;

	private static class TestBasePersistence<T extends BaseModel<T>>
		extends BasePersistenceImpl<T> {

		@Override
		public Map<Serializable, T> fetchByPrimaryKeys(
			Set<Serializable> primaryKeys) {

			Assert.assertNotNull(_keys);
			Assert.assertEquals(_keys, primaryKeys);

			Map map = new HashMap();

			for (Object key : _keys) {
				map.put(key, key);
			}

			return map;
		}

		private TestBasePersistence(Set<?> keys) {
			_keys = keys;
		}

		private final Set<?> _keys;

	}

}