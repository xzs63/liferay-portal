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

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.bundle.mvcactioncommand.TestMVCActionCommand1;
import com.liferay.portal.kernel.portlet.bridges.mvc.bundle.mvcactioncommand.TestMVCActionCommand2;
import com.liferay.portal.kernel.portlet.bridges.mvc.bundle.mvcactioncommand.TestPortlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SyntheticBundleRule;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletConfig;

/**
 * @author Manuel de la Peña
 */
public class MVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new SyntheticBundleRule("bundle.mvcactioncommand"));

	@Test
	public void testMultipleMVCActionCommandsWithMultipleParameters()
		throws Exception {

		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME);
		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	@Test
	public void testMultipleMVCActionCommandsWithSingleParameter()
		throws Exception {

		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME +
				StringPool.COMMA +
					TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand2.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	@Test
	public void testSingleMVCActionCommand() throws Exception {
		MockActionRequest mockActionRequest = new MockLiferayPortletRequest();

		mockActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_NAME);

		_portlet.processAction(mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
		Assert.assertEquals(
			TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE,
			mockActionRequest.getAttribute(
				TestMVCActionCommand1.TEST_MVC_ACTION_COMMAND_ATTRIBUTE));
	}

	@Inject(filter = "javax.portlet.name=" + TestPortlet.PORTLET_NAME)
	private final javax.portlet.Portlet _portlet = null;

	private static class MockLiferayPortletConfig
		extends MockPortletConfig implements LiferayPortletConfig {

		@Override
		public Portlet getPortlet() {
			return null;
		}

		@Override
		public String getPortletId() {
			return "testPortlet";
		}

		@Override
		public boolean isCopyRequestParameters() {
			return false;
		}

		@Override
		public boolean isWARFile() {
			return false;
		}

	}

	private static class MockLiferayPortletRequest
		extends MockActionRequest implements LiferayPortletRequest {

		@Override
		public void addParameter(String name, String value) {
			_mockHttpServletRequest.addParameter(name, value);

			super.addParameter(name, value);
		}

		@Override
		public Map<String, String[]> clearRenderParameters() {
			return null;
		}

		@Override
		public void defineObjects(
			PortletConfig portletConfig, PortletResponse portletResponse) {
		}

		@Override
		public Object getAttribute(String name) {
			if (name.equals(JavaConstants.JAVAX_PORTLET_CONFIG)) {
				return new MockLiferayPortletConfig();
			}

			return super.getAttribute(name);
		}

		@Override
		public HttpServletRequest getHttpServletRequest() {
			return _mockHttpServletRequest;
		}

		@Override
		public HttpServletRequest getOriginalHttpServletRequest() {
			return _mockHttpServletRequest;
		}

		@Override
		public long getPlid() {
			return 0;
		}

		@Override
		public Portlet getPortlet() {
			return null;
		}

		@Override
		public String getPortletName() {
			return null;
		}

		private final MockHttpServletRequest _mockHttpServletRequest =
			new MockHttpServletRequest();

	}

}