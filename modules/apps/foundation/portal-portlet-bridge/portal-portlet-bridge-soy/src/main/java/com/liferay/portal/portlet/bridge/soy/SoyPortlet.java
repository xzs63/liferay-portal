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

package com.liferay.portal.portlet.bridge.soy;

import com.liferay.osgi.util.service.OSGiServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCCommandCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortletHelper;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortletRequestFactory;
import com.liferay.portal.template.soy.constants.SoyTemplateConstants;
import com.liferay.portal.template.soy.utils.SoyContext;
import com.liferay.portal.template.soy.utils.SoyTemplateResourcesProvider;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.portlet.ActionResponseImpl;
import com.liferay.portlet.PortletRequestImpl;
import com.liferay.portlet.RenderRequestImpl;

import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Miroslav Ligas
 * @author Bruno Basto
 */
public class SoyPortlet extends MVCPortlet {

	/**
	 * @deprecated As of 3.1.0, use {@link SoyPortlet#init(PortletConfig)}}
	 *             instead
	 */
	@Deprecated
	@Override
	public void init() throws PortletException {
		super.init();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		super.init(portletConfig);

		propagateRequestParameters = GetterUtil.getBoolean(
			getInitParameter("propagate-request-parameters"), true);

		_bundle = FrameworkUtil.getBundle(getClass());
		_portletConfig = portletConfig;

		try {
			MVCCommandCache mvcRenderCommandCache = getRenderMVCCommandCache();

			FriendlyURLMapper friendlyURLMapper = _getFriendlyURLMapper();

			_soyPortletHelper = new SoyPortletHelper(
				_bundle, mvcRenderCommandCache, friendlyURLMapper);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		Template template = (Template)renderRequest.getAttribute(
			WebKeys.TEMPLATE);

		if (template == null) {
			try {
				_createRequestTemplate(renderRequest);
			}
			catch (TemplateException te) {
				throw new PortletException(te);
			}
		}

		if (_isPjaxRequest(renderRequest)) {
			return;
		}

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(resourceResponse);

		try {
			if (_isProcessAction(resourceRequest)) {
				_callProcessAction(
					resourceRequest, resourceResponse, httpServletResponse,
					_getPortlet());

				return;
			}

			if (callResourceMethod(resourceRequest, resourceResponse)) {
				return;
			}

			_callRender(resourceRequest, resourceResponse, _getPortlet());

			_prepareTemplate(resourceRequest, resourceResponse);

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			Template template = getTemplate(resourceRequest);

			ServletResponseUtil.write(
				httpServletResponse,
				_soyPortletHelper.serializeTemplate(template));
		}
		catch (Exception e) {
			_log.error("Error on the Serve Resource Phase", e);
		}
	}

	protected Set<String> getJavaScriptRequiredModules(String path) {
		try {
			Set<String> javaScriptRequiredModules = new HashSet<>();

			String javaScriptRequiredModule =
				_soyPortletHelper.getJavaScriptLoaderModule(path);

			javaScriptRequiredModules.add(javaScriptRequiredModule);

			return javaScriptRequiredModules;
		}
		catch (Exception e) {
			return Collections.emptySet();
		}
	}

	@Override
	protected String getPath(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String path = super.getPath(portletRequest, portletResponse);

		if (Validator.isNull(path) || StringPool.SLASH.equals(path)) {
			return viewTemplate;
		}

		return path;
	}

	protected Writer getResponseWriter(PortletResponse portletResponse)
		throws IOException {

		Writer writer = null;

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse)portletResponse;

			writer = UnsyncPrintWriterPool.borrow(mimeResponse.getWriter());
		}
		else {
			writer = new UnsyncStringWriter();
		}

		return writer;
	}

	protected Template getTemplate(PortletRequest portletRequest)
		throws PortletException {

		Template template = (Template)portletRequest.getAttribute(
			WebKeys.TEMPLATE);

		if (template != null) {
			return template;
		}

		try {
			return _createRequestTemplate(portletRequest);
		}
		catch (TemplateException te) {
			throw new PortletException("Unable to create template", te);
		}
	}

	@Override
	protected void include(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, String lifecycle)
		throws IOException, PortletException {

		try {
			Writer writer = getResponseWriter(portletResponse);

			_prepareTemplate(portletRequest, portletResponse);

			_writeTemplate(portletRequest, portletResponse, writer);

			_writeJavaScript(portletRequest, portletResponse, writer);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		if (clearRequestParameters) {
			if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
				portletResponse.setProperty("clear-request-parameters", "true");
			}
		}
	}

	protected void populateJavaScriptTemplateContext(
		Template template, String portletNamespace) {

		String portletComponentId = _getPortletComponentId(portletNamespace);

		template.put(
			"element", "#" + _getPortletWrapperId(portletNamespace) + " > div");

		template.put("id", portletComponentId);
	}

	protected void propagateRequestParameters(PortletRequest portletRequest)
		throws PortletException {

		Map<String, Object> soyContextParametersMap = new HashMap<>();

		Map<String, String[]> parametersMap = portletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
			String parameterName = entry.getKey();
			String[] parameterValues = entry.getValue();

			if (parameterValues.length == 1) {
				soyContextParametersMap.put(parameterName, parameterValues[0]);
			}
			else if (parameterValues.length > 1) {
				soyContextParametersMap.put(parameterName, parameterValues);
			}
		}

		Template template = getTemplate(portletRequest);

		Map<String, Object> injectedData = (Map<String, Object>)template.get(
			SoyTemplateConstants.INJECTED_DATA);

		if (injectedData == null) {
			injectedData = new HashMap<>();
		}

		injectedData.put("requestParams", soyContextParametersMap);

		template.put(SoyTemplateConstants.INJECTED_DATA, injectedData);
	}

	protected boolean propagateRequestParameters;

	/**
	 * @deprecated As of 3.1.0, use {@link
	 *             SoyPortlet#getTemplate(PortletRequest)}} instead
	 */
	@Deprecated
	protected Template template;

	private void _callProcessAction(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			HttpServletResponse response, Portlet portlet)
		throws Exception {

		SoyPortletRequestFactory soyPortletRequestFactory =
			new SoyPortletRequestFactory(portlet);

		ActionRequestImpl actionRequestImpl =
			soyPortletRequestFactory.createActionRequest(resourceRequest);

		ActionResponseImpl actionResponseImpl =
			soyPortletRequestFactory.createActionResponse(
				actionRequestImpl, resourceResponse);

		processAction(actionRequestImpl, actionResponseImpl);

		_copyRequestAttributes(actionRequestImpl, resourceRequest);

		String portletNamespace = resourceResponse.getNamespace();

		String redirect = HttpUtil.setParameter(
			actionResponseImpl.getRedirectLocation(), portletNamespace + "pjax",
			"true");

		redirect = HttpUtil.setParameter(redirect, "p_p_lifecycle", "2");

		response.sendRedirect(redirect);
	}

	private void _callRender(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			Portlet portlet)
		throws Exception {

		SoyPortletRequestFactory soyPortletRequestFactory =
			new SoyPortletRequestFactory(portlet);

		RenderRequestImpl renderRequestImpl =
			soyPortletRequestFactory.createRenderRequest(
				resourceRequest, resourceResponse);

		RenderResponse renderResponse =
			soyPortletRequestFactory.createRenderResponse(
				renderRequestImpl, resourceResponse);

		render(renderRequestImpl, renderResponse);

		_copyRequestAttributes(renderRequestImpl, resourceRequest);

		String mvcRenderCommandName = ParamUtil.getString(
			resourceRequest, "mvcRenderCommandName", "/");

		MVCRenderCommand mvcRenderCommand = _getMVCRenderCommand(
			mvcRenderCommandName);

		String path = getPath(resourceRequest, resourceResponse);

		if (mvcRenderCommand != MVCRenderCommand.EMPTY) {
			path = mvcRenderCommand.render(renderRequestImpl, renderResponse);

			_copyRequestAttributes(renderRequestImpl, resourceRequest);
		}

		resourceRequest.setAttribute(
			getMVCPathAttributeName(renderResponse.getNamespace()), path);
	}

	private void _clearSessionMessages(PortletRequest portletRequest) {
		SessionErrors.clear(portletRequest);

		SessionMessages.clear(portletRequest);
	}

	private void _copyRequestAttributes(
		PortletRequestImpl portletRequestImpl,
		ResourceRequest resourceRequest) {

		Enumeration<String> attributeNames =
			portletRequestImpl.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();

			resourceRequest.setAttribute(
				attributeName, portletRequestImpl.getAttribute(attributeName));
		}
	}

	private Template _createRequestTemplate(PortletRequest portletRequest)
		throws TemplateException {

		List<TemplateResource> templateResources = _getTemplateResources();

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_SOY, templateResources, false);

		portletRequest.setAttribute(WebKeys.TEMPLATE, template);

		return template;
	}

	private FriendlyURLMapper _getFriendlyURLMapper() {
		Portlet portlet = _getPortlet();

		return portlet.getFriendlyURLMapperInstance();
	}

	private MVCRenderCommand _getMVCRenderCommand(String mvcRenderCommandName) {
		MVCCommandCache mvcRenderCommandCache = getRenderMVCCommandCache();

		return (MVCRenderCommand)mvcRenderCommandCache.getMVCCommand(
			mvcRenderCommandName);
	}

	private Portlet _getPortlet() {
		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)_portletConfig;

		return liferayPortletConfig.getPortlet();
	}

	private String _getPortletComponentId(String portletNamespace) {
		return portletNamespace.concat("PortletComponent");
	}

	private String _getPortletWrapperId(String portletNamespace) {
		StringBundler sb = new StringBundler(3);

		sb.append(portletNamespace);
		sb.append(StringPool.UNDERLINE);
		sb.append("SoyWrapper");

		return sb.toString();
	}

	private List<TemplateResource> _getTemplateResources()
		throws TemplateException {

		if (_templateResources != null) {
			return _templateResources;
		}

		Bundle bundle = FrameworkUtil.getBundle(SoyPortlet.class);

		_templateResources = OSGiServiceUtil.callService(
			bundle.getBundleContext(), SoyTemplateResourcesProvider.class,
			soyTemplateResourcesProvider -> {
				List<TemplateResource> templateResources =
					soyTemplateResourcesProvider.getBundleTemplateResources(
						_bundle, templatePath);

				MVCCommandCache mvcCommandCache = getRenderMVCCommandCache();

				for (String mvcCommandName :
						mvcCommandCache.getMVCCommandNames()) {

					MVCCommand mvcCommand = _getMVCRenderCommand(
						mvcCommandName);

					Bundle curBundle = FrameworkUtil.getBundle(
						mvcCommand.getClass());

					List<TemplateResource> mvcCommandTemplateResources =
						soyTemplateResourcesProvider.getBundleTemplateResources(
							curBundle, templatePath);

					templateResources.addAll(mvcCommandTemplateResources);
				}

				return templateResources;
			});

		return _templateResources;
	}

	private boolean _isPjaxRequest(PortletRequest portletRequest) {
		return ParamUtil.getBoolean(portletRequest, "pjax");
	}

	private boolean _isProcessAction(PortletRequest portletRequest) {
		int original_p_p_lifecycle = ParamUtil.getInteger(
			portletRequest, "original_p_p_lifecycle");

		if (original_p_p_lifecycle == 1) {
			return true;
		}

		return false;
	}

	private void _prepareSessionMessages(
		PortletRequest portletRequest, SoyContext soyContext) {

		Map<String, Object> sessionErrors = new HashMap<>();

		for (String key : SessionErrors.keySet(portletRequest)) {
			sessionErrors.put(key, SessionErrors.get(portletRequest, key));
		}

		soyContext.putInjectedData("sessionErrors", sessionErrors);

		Map<String, Object> sessionMessages = new HashMap<>();

		for (String key : SessionMessages.keySet(portletRequest)) {
			if (key.endsWith(
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE)) {

				continue;
			}

			sessionMessages.put(key, SessionMessages.get(portletRequest, key));
		}

		soyContext.putInjectedData("sessionMessages", sessionMessages);
	}

	private void _prepareTemplate(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		hideDefaultErrorMessage(portletRequest);

		Template template = getTemplate(portletRequest);

		String path = getPath(portletRequest, portletResponse);

		template.put(
			"javaScriptLoaderModule",
			_soyPortletHelper.getJavaScriptLoaderModule(path));

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		template.put("locale", themeDisplay.getLocale());

		String templateNamespace = path.concat(".render");

		template.put(TemplateConstants.NAMESPACE, templateNamespace);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		template.prepare(httpServletRequest);

		SoyContext soyContext = new SoyContext();

		soyContext.putInjectedData(
			"portletNamespace", portletResponse.getNamespace());

		_prepareSessionMessages(portletRequest, soyContext);

		template.putAll(soyContext);

		if (propagateRequestParameters) {
			propagateRequestParameters(portletRequest);
		}

		populateJavaScriptTemplateContext(
			template, portletResponse.getNamespace());

		_clearSessionMessages(portletRequest);
	}

	private void _writeJavaScript(
			PortletRequest portletRequest, PortletResponse portletResponse,
			Writer writer)
		throws Exception {

		ScriptData scriptData = new ScriptData();

		String portletNamespace = portletResponse.getNamespace();

		String portletComponentId = _getPortletComponentId(portletNamespace);

		String portletId = PortalUtil.getPortletId(portletRequest);
		String portletWrapperId = _getPortletWrapperId(portletNamespace);
		Template template = getTemplate(portletRequest);

		String portletJavaScript = _soyPortletHelper.getRouterJavaScript(
			portletComponentId, portletId, portletNamespace, portletWrapperId,
			template);

		Set<String> requiredModules = new HashSet<>();

		requiredModules.add(
			"portal-portlet-bridge-soy/router/SoyPortletRouter");

		String path = getPath(portletRequest, portletResponse);

		requiredModules.addAll(getJavaScriptRequiredModules(path));

		String requiredModulesString = StringUtil.merge(requiredModules);

		scriptData.append(
			portletId, portletJavaScript, requiredModulesString,
			ScriptData.ModulesType.ES6);

		scriptData.writeTo(writer);
	}

	private void _writeTemplate(
			PortletRequest portletRequest, PortletResponse portletResponse,
			Writer writer)
		throws Exception {

		writer.write("<div id=\"");
		writer.write(_getPortletWrapperId(portletResponse.getNamespace()));
		writer.write("\">");

		Template template = getTemplate(portletRequest);

		template.processTemplate(writer);

		writer.write("</div>");
	}

	private static final Log _log = LogFactoryUtil.getLog(SoyPortlet.class);

	private Bundle _bundle;
	private PortletConfig _portletConfig;
	private SoyPortletHelper _soyPortletHelper;
	private List<TemplateResource> _templateResources;

}