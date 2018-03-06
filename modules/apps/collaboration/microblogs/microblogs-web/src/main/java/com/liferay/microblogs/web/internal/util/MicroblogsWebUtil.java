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

package com.liferay.microblogs.web.internal.util;

import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.social.kernel.model.SocialRelationConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

/**
 * @author Jonathan Lee
 */
public class MicroblogsWebUtil {

	public static List<String> getHashtags(String content) {
		List<String> hashtags = new ArrayList<>();

		Matcher matcher = _hashtagPattern.matcher(content);

		while (matcher.find()) {
			String hashtag = matcher.group();

			hashtag = hashtag.substring(1);

			hashtags.add(hashtag);
		}

		return hashtags;
	}

	public static JSONArray getJSONRecipients(
			long userId, ThemeDisplay themeDisplay)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<User> users = UserLocalServiceUtil.getSocialUsers(
			userId, SocialRelationConstants.TYPE_BI_CONNECTION,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new UserFirstNameComparator(true));

		for (User user : users) {
			if (user.isDefaultUser() || (userId == user.getUserId())) {
				continue;
			}

			JSONObject userJSONObject = JSONFactoryUtil.createJSONObject();

			userJSONObject.put("emailAddress", user.getEmailAddress());
			userJSONObject.put("fullName", user.getFullName());
			userJSONObject.put("jobTitle", user.getJobTitle());
			userJSONObject.put(
				"portraitURL", user.getPortraitURL(themeDisplay));
			userJSONObject.put("screenName", user.getScreenName());
			userJSONObject.put("userId", user.getUserId());

			jsonArray.put(userJSONObject);
		}

		return jsonArray;
	}

	public static String getProcessedContent(
			MicroblogsEntry microblogsEntry, ServiceContext serviceContext)
		throws PortalException {

		return getProcessedContent(
			microblogsEntry.getContent(), serviceContext);
	}

	public static String getProcessedContent(
			String content, ServiceContext serviceContext)
		throws PortalException {

		content = replaceHashtags(content, serviceContext);

		content = replaceUserTags(content, serviceContext);

		return content;
	}

	public static List<String> getScreenNames(String content) {
		List<String> screenNames = new ArrayList<>();

		Matcher matcher = _userTagPattern.matcher(content);

		while (matcher.find()) {
			String screenName = matcher.group();

			screenName = screenName.replace("[@", StringPool.BLANK);
			screenName = screenName.replace("]", StringPool.BLANK);

			screenNames.add(screenName);
		}

		return screenNames;
	}

	protected static String replaceHashtags(
			String content, ServiceContext serviceContext)
		throws PortalException {

		String escapedContent = HtmlUtil.escape(content);

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Matcher matcher = _hashtagPattern.matcher(content);

		while (matcher.find()) {
			String result = matcher.group();

			StringBuilder sb = new StringBuilder(6);

			sb.append("<span class=\"hashtag\">#</span>");
			sb.append("<a class=\"hashtag-link\" href=\"");

			PortletURL portletURL = null;

			Group group = GroupLocalServiceUtil.getUserGroup(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

			long portletPlid = PortalUtil.getPlidFromPortletId(
				group.getGroupId(), true, MicroblogsPortletKeys.MICROBLOGS);

			if (portletPlid != 0) {
				portletURL = PortletURLFactoryUtil.create(
					serviceContext.getLiferayPortletRequest(),
					MicroblogsPortletKeys.MICROBLOGS, portletPlid,
					PortletRequest.RENDER_PHASE);

				try {
					portletURL.setWindowState(LiferayWindowState.NORMAL);
				}
				catch (WindowStateException wse) {
				}
			}
			else {
				LiferayPortletResponse liferayPortletResponse =
					serviceContext.getLiferayPortletResponse();

				portletURL = liferayPortletResponse.createRenderURL(
					MicroblogsPortletKeys.MICROBLOGS);

				try {
					portletURL.setWindowState(WindowState.MAXIMIZED);
				}
				catch (WindowStateException wse) {
				}
			}

			portletURL.setParameter("mvcPath", "/microblogs/view.jsp");

			String assetTagName = result.substring(1);

			portletURL.setParameter("tabs1", assetTagName);
			portletURL.setParameter("assetTagName", assetTagName);

			sb.append(portletURL);

			sb.append("\">");
			sb.append(assetTagName);
			sb.append("</a>");

			String tagLink = sb.toString();

			escapedContent = StringUtil.replace(
				escapedContent, result, tagLink);
		}

		return escapedContent;
	}

	protected static String replaceUserTags(
			String content, ServiceContext serviceContext)
		throws PortalException {

		Matcher matcher = _userTagPattern.matcher(content);

		while (matcher.find()) {
			String result = matcher.group();

			try {
				StringBuilder sb = new StringBuilder(5);

				sb.append("<a href=\"");

				String assetTagScreenName = result.replace(
					"[@", StringPool.BLANK);

				assetTagScreenName = assetTagScreenName.replace(
					"]", StringPool.BLANK);

				ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

				User assetTagUser = UserLocalServiceUtil.getUserByScreenName(
					themeDisplay.getCompanyId(), assetTagScreenName);

				sb.append(assetTagUser.getDisplayURL(themeDisplay));

				sb.append("\">");

				String assetTagUserName = PortalUtil.getUserName(
					assetTagUser.getUserId(), assetTagScreenName);

				sb.append(assetTagUserName);

				sb.append("</a>");

				String userLink = sb.toString();

				content = StringUtil.replace(content, result, userLink);
			}
			catch (NoSuchUserException nsue) {
				if (_log.isDebugEnabled()) {
					_log.debug(nsue, nsue);
				}
			}
		}

		return content;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MicroblogsWebUtil.class);

	private static final Pattern _hashtagPattern = Pattern.compile(
		"\\#[a-zA-Z]\\w*");
	private static final Pattern _userTagPattern = Pattern.compile(
		"\\[\\@\\S*\\]");

}