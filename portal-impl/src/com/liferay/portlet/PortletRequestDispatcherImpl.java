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

package com.liferay.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequestDispatcher;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.DynamicServletRequestUtil;
import com.liferay.portal.struts.StrutsURLEncoder;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Raymond Augé
 */
public class PortletRequestDispatcherImpl
	implements LiferayPortletRequestDispatcher, RequestDispatcher {

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, boolean named,
		PortletContext portletContext) {

		this(requestDispatcher, named, portletContext, null);
	}

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, boolean named,
		PortletContext portletContext, String path) {

		_requestDispatcher = requestDispatcher;
		_named = named;
		_path = path;

		_liferayPortletContext = (LiferayPortletContext)portletContext;

		_portlet = _liferayPortletContext.getPortlet();
	}

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, String path) {

		_requestDispatcher = requestDispatcher;
		_path = path;

		_named = false;
		_liferayPortletContext = null;
		_portlet = null;
	}

	@Override
	public void forward(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IllegalStateException, IOException, PortletException {

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(portletResponse);

		if (httpServletResponse.isCommitted()) {
			throw new IllegalStateException("Response is already committed");
		}

		dispatch(portletRequest, portletResponse, false, false);
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		dispatch(servletRequest, servletResponse, false);
	}

	@Override
	public void include(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortletException {

		dispatch(portletRequest, portletResponse, false, true);
	}

	@Override
	public void include(
			PortletRequest portletRequest, PortletResponse portletResponse,
			boolean strutsURLEncoder)
		throws IOException, PortletException {

		dispatch(portletRequest, portletResponse, strutsURLEncoder, true);
	}

	@Override
	public void include(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		dispatch(renderRequest, renderResponse, false, true);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		dispatch(servletRequest, servletResponse, true);
	}

	protected void checkCalledFlushBuffer(
		boolean include, PortletResponse portletResponse) {

		if (!include && (portletResponse instanceof MimeResponseImpl)) {
			MimeResponseImpl mimeResponseImpl =
				(MimeResponseImpl)portletResponse;

			if (mimeResponseImpl.isCalledFlushBuffer()) {
				throw new IllegalStateException();
			}
		}
	}

	protected HttpServletRequest createDynamicServletRequest(
		HttpServletRequest httpServletRequest,
		PortletRequestImpl portletRequestImpl,
		Map<String, String[]> parameterMap) {

		return DynamicServletRequestUtil.createDynamicServletRequest(
			httpServletRequest, portletRequestImpl.getPortlet(), parameterMap,
			true);
	}

	protected void dispatch(
			PortletRequest portletRequest, PortletResponse portletResponse,
			boolean strutsURLEncoder, boolean include)
		throws IOException, PortletException {

		checkCalledFlushBuffer(include, portletResponse);

		PortletRequestImpl portletRequestImpl =
			PortletRequestImpl.getPortletRequestImpl(portletRequest);
		PortletResponseImpl portletResponseImpl =
			PortletResponseImpl.getPortletResponseImpl(portletResponse);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);
		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE, portletResponse);

		String pathInfo = null;
		String queryString = null;
		String requestURI = null;
		String servletPath = null;

		if (_path != null) {
			String pathNoQueryString = _path;

			int pos = _path.indexOf(CharPool.QUESTION);

			if (pos != -1) {
				pathNoQueryString = _path.substring(0, pos);
				queryString = _path.substring(pos + 1);

				httpServletRequest = createDynamicServletRequest(
					httpServletRequest, portletRequestImpl,
					toParameterMap(queryString));
			}

			Portlet portlet = portletRequestImpl.getPortlet();

			PortletApp portletApp = portlet.getPortletApp();

			Set<String> servletURLPatterns = portletApp.getServletURLPatterns();

			for (String urlPattern : servletURLPatterns) {
				if (urlPattern.endsWith("/*")) {
					int length = urlPattern.length() - 2;

					if ((pathNoQueryString.length() > length) &&
						pathNoQueryString.regionMatches(
							0, urlPattern, 0, length) &&
						(pathNoQueryString.charAt(length) == CharPool.SLASH)) {

						pathInfo = pathNoQueryString.substring(length);
						servletPath = urlPattern.substring(0, length);

						break;
					}
				}
			}

			if (servletPath == null) {
				servletPath = pathNoQueryString;
			}

			String contextPath = portletRequest.getContextPath();

			if (contextPath.equals(StringPool.SLASH)) {
				requestURI = pathNoQueryString;
			}
			else {
				requestURI = contextPath + pathNoQueryString;
			}
		}

		PortletServletRequest portletServletRequest = new PortletServletRequest(
			httpServletRequest, portletRequest, pathInfo, queryString,
			requestURI, servletPath, _named, include);

		PortletServletResponse portletServletResponse =
			new PortletServletResponse(
				PortalUtil.getHttpServletResponse(portletResponse),
				portletResponse, include);

		URLEncoder urlEncoder = _portlet.getURLEncoderInstance();

		if (urlEncoder != null) {
			portletResponseImpl.setURLEncoder(urlEncoder);
		}
		else if (strutsURLEncoder) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			URLEncoder strutsURLEncoderObj = new StrutsURLEncoder(
				portletServletRequest.getContextPath(),
				themeDisplay.getPathMain(),
				(String)_liferayPortletContext.getAttribute(
					Globals.SERVLET_KEY),
				(LiferayPortletURL)portletResponseImpl.createRenderURL());

			portletResponseImpl.setURLEncoder(strutsURLEncoderObj);
		}

		try {
			if (include) {
				_requestDispatcher.include(
					portletServletRequest, portletServletResponse);
			}
			else {
				_requestDispatcher.forward(
					portletServletRequest, portletServletResponse);
			}
		}
		catch (ServletException se) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to dispatch request", se);
			}

			_log.error("Unable to dispatch request: " + se.getMessage());

			throw new PortletException(se);
		}
		finally {
			portletRequestImpl.setPortletRequestDispatcherRequest(null);
		}
	}

	protected void dispatch(
			ServletRequest servletRequest, ServletResponse servletResponse,
			boolean include)
		throws IOException, ServletException {

		HttpServletRequest oldPortletRequestDispatcherRequest = null;

		PortletRequestImpl portletRequestImpl = null;

		if (servletRequest instanceof PortletServletRequest) {
			PortletRequest portletRequest =
				(PortletRequest)servletRequest.getAttribute(
					"javax.portlet.request");

			portletRequestImpl = PortletRequestImpl.getPortletRequestImpl(
				portletRequest);

			oldPortletRequestDispatcherRequest =
				portletRequestImpl.getPortletRequestDispatcherRequest();

			PortletServletRequest portletServletRequest =
				(PortletServletRequest)servletRequest;

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)portletServletRequest.getRequest();

			if (_path != null) {
				int pos = _path.indexOf(CharPool.QUESTION);

				if (pos != -1) {
					String queryString = _path.substring(pos + 1);

					httpServletRequest = createDynamicServletRequest(
						httpServletRequest, portletRequestImpl,
						toParameterMap(queryString));
				}
			}

			servletRequest = new PortletServletRequest(
				httpServletRequest, portletRequest,
				portletServletRequest.getPathInfo(),
				portletServletRequest.getQueryString(),
				portletServletRequest.getRequestURI(),
				portletServletRequest.getServletPath(), _named, include);
		}

		try {
			if (include) {
				_requestDispatcher.include(servletRequest, servletResponse);
			}
			else {
				_requestDispatcher.forward(servletRequest, servletResponse);
			}
		}
		catch (ServletException se) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to dispatch request", se);
			}

			_log.error("Unable to dispatch request: " + se.getMessage());

			throw new ServletException(se);
		}
		finally {
			if (portletRequestImpl != null) {
				portletRequestImpl.setPortletRequestDispatcherRequest(
					oldPortletRequestDispatcherRequest);
			}
		}
	}

	protected Map<String, String[]> toParameterMap(String queryString) {
		Map<String, String[]> parameterMap = new HashMap<>();

		for (String parameter :
				StringUtil.split(queryString, CharPool.AMPERSAND)) {

			String[] parameterArray = StringUtil.split(
				parameter, CharPool.EQUAL);

			String name = parameterArray[0];

			String value = StringPool.BLANK;

			if (parameterArray.length == 2) {
				value = parameterArray[1];
			}

			String[] values = parameterMap.get(name);

			if (values == null) {
				parameterMap.put(name, new String[] {value});
			}
			else {
				String[] newValues = new String[values.length + 1];

				System.arraycopy(values, 0, newValues, 0, values.length);

				newValues[newValues.length - 1] = value;

				parameterMap.put(name, newValues);
			}
		}

		return parameterMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRequestDispatcherImpl.class);

	private final LiferayPortletContext _liferayPortletContext;
	private final boolean _named;
	private final String _path;
	private final Portlet _portlet;
	private final RequestDispatcher _requestDispatcher;

}