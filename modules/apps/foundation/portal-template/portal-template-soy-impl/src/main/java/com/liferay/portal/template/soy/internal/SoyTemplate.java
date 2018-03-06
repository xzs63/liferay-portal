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

package com.liferay.portal.template.soy.internal;

import com.google.common.io.CharStreams;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyFileSet.Builder;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofu.Renderer;
import com.google.template.soy.tofu.SoyTofuOptions;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.ClassResourceBundleLoader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.AbstractMultiResourceTemplate;
import com.liferay.portal.template.TemplateContextHelper;
import com.liferay.portal.template.soy.constants.SoyTemplateConstants;
import com.liferay.portal.template.soy.utils.SoyRawData;

import java.io.Reader;
import java.io.Writer;

import java.lang.reflect.Array;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ClassUtils;

import org.json.JSONArray;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Bruno Basto
 */
public class SoyTemplate extends AbstractMultiResourceTemplate {

	public SoyTemplate(
		List<TemplateResource> templateResources,
		TemplateResource errorTemplateResource, Map<String, Object> context,
		SoyTemplateContextHelper templateContextHelper, boolean privileged,
		SoyTofuCacheHandler soyTofuCacheHandler) {

		super(
			templateResources, errorTemplateResource, context,
			templateContextHelper, TemplateConstants.LANG_TYPE_SOY, 0);

		_templateContextHelper = templateContextHelper;
		_privileged = privileged;

		_soyMapData = new SoyMapData();
		_injectedSoyMapData = new SoyMapData();
		_soyTofuCacheHandler = soyTofuCacheHandler;
	}

	@Override
	public void clear() {
		_soyMapData = new SoyMapData();
		_injectedSoyMapData = new SoyMapData();

		super.clear();
	}

	@Override
	public void prepare(HttpServletRequest request) {
		Map<String, Object> injectedDataObjects = new HashMap<>();

		_templateContextHelper.prepare(injectedDataObjects, request);

		for (Map.Entry<String, Object> entry : injectedDataObjects.entrySet()) {
			putInjectedData(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object put(String key, Object value) {
		TemplateContextHelper templateContextHelper =
			getTemplateContextHelper();

		Set<String> restrictedVariables =
			templateContextHelper.getRestrictedVariables();

		Object currentValue = get(key);

		if (!restrictedVariables.contains(key) &&
			!Objects.equals(value, currentValue)) {

			Object soyMapValue = getSoyMapValue(value);

			_soyMapData.put(key, soyMapValue);
		}

		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		for (String key : map.keySet()) {
			put(key, map.get(key));
		}
	}

	public void putInjectedData(String key, Object value) {
		_injectedSoyMapData.put(key, getSoyMapValue(value));
	}

	@Override
	public Object remove(Object key) {
		if (SoyTemplateConstants.INJECTED_DATA.equals(key)) {
			_injectedSoyMapData = new SoyMapData();
		}

		_soyMapData.remove((String)key);

		return super.remove(key);
	}

	protected SoyMsgBundleBridge createSoyMsgBundleBridge(
		SoyFileSet soyFileSet, Locale locale) {

		SoyMsgBundle soyMsgBundle = soyFileSet.extractMsgs();

		ResourceBundle languageResourceBundle = _getLanguageResourceBundle(
			locale);

		SoyMsgBundleBridge soyMsgBundleBridge = new SoyMsgBundleBridge(
			soyMsgBundle, locale, languageResourceBundle);

		return soyMsgBundleBridge;
	}

	protected SoyFileSet getSoyFileSet(List<TemplateResource> templateResources)
		throws Exception {

		SoyFileSet soyFileSet = null;

		if (_privileged) {
			soyFileSet = AccessController.doPrivileged(
				new TemplatePrivilegedExceptionAction(templateResources));
		}
		else {
			Builder builder = SoyFileSet.builder();

			for (TemplateResource templateResource : templateResources) {
				String templateContent = getTemplateContent(templateResource);

				builder.add(templateContent, templateResource.getTemplateId());
			}

			soyFileSet = builder.build();
		}

		return soyFileSet;
	}

	protected SoyMapData getSoyMapData() {
		return _soyMapData;
	}

	protected SoyMapData getSoyMapInjectedData() {
		if (containsKey(SoyTemplateConstants.INJECTED_DATA)) {
			Map<String, Object> injectedData = (Map<String, Object>)get(
				SoyTemplateConstants.INJECTED_DATA);

			for (Map.Entry<String, Object> entry : injectedData.entrySet()) {
				putInjectedData(entry.getKey(), entry.getValue());
			}
		}

		return _injectedSoyMapData;
	}

	protected Object getSoyMapValue(IdentityHashMap cache, Object value) {
		if (cache.containsKey(value)) {
			return cache.get(value);
		}

		Class<?> clazz = value.getClass();

		if (ClassUtils.isPrimitiveOrWrapper(clazz) || value instanceof String) {
			return value;
		}

		if (value instanceof Class) {
			clazz = (Class)value;

			return clazz.getName();
		}

		if (clazz.isEnum()) {
			return String.valueOf(value);
		}

		if (clazz.isArray()) {
			List<Object> newList = new ArrayList<>();

			cache.put(value, newList);

			for (int i = 0; i < Array.getLength(value); i++) {
				Object object = Array.get(value, i);

				newList.add(getSoyMapValue(cache, object));
			}

			return newList;
		}

		if (value instanceof Iterable) {
			@SuppressWarnings("unchecked")
			Iterable<Object> iterable = (Iterable<Object>)value;

			List<Object> newList = new ArrayList<>();

			cache.put(value, newList);

			for (Object object : iterable) {
				newList.add(getSoyMapValue(cache, object));
			}

			return newList;
		}

		if (value instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)value;

			List<Object> newList = new ArrayList<>();

			cache.put(value, newList);

			for (int i = 0; i < jsonArray.length(); i++) {
				Object object = jsonArray.opt(i);

				newList.add(getSoyMapValue(cache, object));
			}

			return newList;
		}

		if (value instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>)value;

			Map<Object, Object> newMap = new TreeMap<>();

			cache.put(value, newMap);

			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object newKey = getSoyMapValue(cache, entry.getKey());

				if (newKey == null) {
					continue;
				}

				Object newValue = getSoyMapValue(cache, entry.getValue());

				newMap.put(newKey, newValue);
			}

			return newMap;
		}

		if (value instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)value;

			Map<String, Object> newMap = new TreeMap<>();

			cache.put(value, newMap);

			Iterator<String> iterator = jsonObject.keys();

			while (iterator.hasNext()) {
				String key = iterator.next();

				Object object = jsonObject.get(key);

				Object newValue = getSoyMapValue(cache, object);

				newMap.put(key, newValue);
			}

			return newMap;
		}

		if (value instanceof org.json.JSONObject) {
			org.json.JSONObject jsonObject = (org.json.JSONObject)value;

			Map<Object, Object> newMap = new TreeMap<>();

			cache.put(value, newMap);

			Iterator<String> iterator = jsonObject.keys();

			while (iterator.hasNext()) {
				String key = iterator.next();

				Object object = jsonObject.opt(key);

				Object newValue = getSoyMapValue(cache, object);

				newMap.put(key, newValue);
			}

			return newMap;
		}

		if (value instanceof SoyRawData) {
			SoyRawData soyRawData = (SoyRawData)value;

			return soyRawData.getValue();
		}

		String json = JSONFactoryUtil.looseSerialize(value);

		Object deserialized = JSONFactoryUtil.looseDeserialize(json);

		if (deserialized instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)deserialized;

			Map<String, Object> newMap = new TreeMap<>();

			for (String key : map.keySet()) {
				Object newValue = BeanPropertiesUtil.getObjectSilent(
					value, key);

				newMap.put(key, newValue);
			}

			if (!newMap.isEmpty()) {
				return getSoyMapValue(cache, newMap);
			}
		}

		return _templateContextHelper.deserializeValue(value);
	}

	protected Object getSoyMapValue(Object value) {
		IdentityHashMap cache = new IdentityHashMap<>();

		cache.put(null, null);

		return getSoyMapValue(cache, value);
	}

	protected Optional<SoyMsgBundle> getSoyMsgBundle(
		SoyFileSet soyFileSet, SoyTofuCacheBag soyTofuCacheBag) {

		Locale locale = (Locale)get("locale");

		if (locale != null) {
			SoyMsgBundle soyMsgBundle = soyTofuCacheBag.getMessageBundle(
				locale);

			if (soyMsgBundle == null) {
				soyMsgBundle = createSoyMsgBundleBridge(soyFileSet, locale);

				soyTofuCacheBag.putMessageBundle(locale, soyMsgBundle);
			}

			return Optional.of(soyMsgBundle);
		}

		return Optional.empty();
	}

	protected SoyTofuCacheBag getSoyTofuCacheBag(
			List<TemplateResource> templateResources)
		throws Exception {

		SoyTofuCacheBag soyTofuCacheBag = _soyTofuCacheHandler.get(
			templateResources);

		if (soyTofuCacheBag == null) {
			SoyTofuOptions soyTofuOptions = new SoyTofuOptions();

			soyTofuOptions.setUseCaching(true);

			SoyFileSet soyFileSet = getSoyFileSet(templateResources);

			SoyTofu soyTofu = soyFileSet.compileToTofu(soyTofuOptions);

			soyTofuCacheBag = _soyTofuCacheHandler.add(
				templateResources, soyFileSet, soyTofu);
		}

		return soyTofuCacheBag;
	}

	protected String getTemplateContent(TemplateResource templateResource)
		throws Exception {

		Reader reader = templateResource.getReader();

		return CharStreams.toString(reader);
	}

	protected TemplateContextHelper getTemplateContextHelper() {
		return _templateContextHelper;
	}

	@Override
	protected void handleException(Exception exception, Writer writer)
		throws TemplateException {

		put("exception", exception.getMessage());

		StringBundler sb = new StringBundler();

		for (TemplateResource templateResource : templateResources) {
			if (templateResource instanceof StringTemplateResource) {
				StringTemplateResource stringTemplateResource =
					(StringTemplateResource)templateResource;

				sb.append(stringTemplateResource.getContent());
			}
		}

		put("script", sb.toString());

		try {
			processTemplates(Arrays.asList(errorTemplateResource), writer);
		}
		catch (Exception e) {
			throw new TemplateException(
				"Unable to process Soy template " +
					errorTemplateResource.getTemplateId(),
				e);
		}
	}

	@Override
	protected void processTemplates(
			List<TemplateResource> templateResources, Writer writer)
		throws Exception {

		try {
			String namespace = GetterUtil.getString(
				get(TemplateConstants.NAMESPACE));

			if (Validator.isNull(namespace)) {
				throw new TemplateException("Namespace is not specified");
			}

			SoyTofuCacheBag soyTofuCacheBag = getSoyTofuCacheBag(
				templateResources);

			SoyTofu soyTofu = soyTofuCacheBag.getSoyTofu();

			Renderer renderer = soyTofu.newRenderer(namespace);

			renderer.setData(getSoyMapData());
			renderer.setIjData(getSoyMapInjectedData());

			SoyFileSet soyFileSet = soyTofuCacheBag.getSoyFileSet();

			Optional<SoyMsgBundle> soyMsgBundle = getSoyMsgBundle(
				soyFileSet, soyTofuCacheBag);

			if (soyMsgBundle.isPresent()) {
				renderer.setMsgBundle(soyMsgBundle.get());
			}

			boolean renderStrict = GetterUtil.getBoolean(
				get(TemplateConstants.RENDER_STRICT), true);

			if (renderStrict) {
				SanitizedContent sanitizedContent = renderer.renderStrict();

				writer.write(sanitizedContent.stringValue());
			}
			else {
				writer.write(renderer.render());
			}
		}
		catch (PrivilegedActionException pae) {
			throw pae.getException();
		}
	}

	private ResourceBundle _getLanguageResourceBundle(Locale locale) {
		List<ResourceBundleLoader> resourceBundleLoaders = new ArrayList<>();

		for (TemplateResource templateResource : templateResources) {
			try {
				Bundle templateResourceBundle =
					SoyProviderCapabilityBundleRegister.getTemplateBundle(
						templateResource.getTemplateId());

				BundleWiring bundleWiring = templateResourceBundle.adapt(
					BundleWiring.class);

				resourceBundleLoaders.add(
					new ClassResourceBundleLoader(
						"content.Language", bundleWiring.getClassLoader()));
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					String templateId = templateResource.getTemplateId();

					_log.debug(
						"Unable to get language resource bundle for template " +
							StringUtil.quote(templateId),
						e);
				}
			}
		}

		resourceBundleLoaders.add(LanguageUtil.getPortalResourceBundleLoader());

		AggregateResourceBundleLoader aggregateResourceBundleLoader =
			new AggregateResourceBundleLoader(
				resourceBundleLoaders.toArray(
					new ResourceBundleLoader[resourceBundleLoaders.size()]));

		return aggregateResourceBundleLoader.loadResourceBundle(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(SoyTemplate.class);

	private SoyMapData _injectedSoyMapData;
	private final boolean _privileged;
	private SoyMapData _soyMapData;
	private final SoyTofuCacheHandler _soyTofuCacheHandler;
	private final SoyTemplateContextHelper _templateContextHelper;

	private class TemplatePrivilegedExceptionAction
		implements PrivilegedExceptionAction<SoyFileSet> {

		public TemplatePrivilegedExceptionAction(
			List<TemplateResource> templateResources) {

			_templateResources = templateResources;
		}

		@Override
		public SoyFileSet run() throws Exception {
			Builder builder = SoyFileSet.builder();

			for (TemplateResource templateResource : _templateResources) {
				String templateContent = getTemplateContent(templateResource);

				builder.add(templateContent, templateResource.getTemplateId());
			}

			return builder.build();
		}

		private final List<TemplateResource> _templateResources;

	}

}