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

package com.liferay.knowledge.base.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Roberto Díaz
 */
@ExtendedObjectClassDefinition(
	category = "collaboration",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	id = "com.liferay.knowledge.base.web.internal.configuration.KBDisplayPortletInstanceConfiguration",
	localization = "content/Language",
	name = "knowledge-base-display-portlet-instance-configuration-name"
)
public interface KBDisplayPortletInstanceConfiguration {

	@Meta.AD(deflt = "0", name = "resource-prim-key", required = false)
	public long resourcePrimKey();

	@Meta.AD(deflt = "0", name = "resource-class-name-id", required = false)
	public long resourceClassNameId();

	@Meta.AD(
		deflt = "false", name = "enable-kb-article-description",
		required = false
	)
	public boolean enableKBArticleDescription();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-ratings", required = false
	)
	public boolean enableKBArticleRatings();

	@Meta.AD(
		deflt = "true", name = "show-kb-article-asset-entries", required = false
	)
	public boolean showKBArticleAssetEntries();

	@Meta.AD(
		deflt = "true", name = "show-kb-article-attachments", required = false
	)
	public boolean showKBArticleAttachments();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-asset-links", required = false
	)
	public boolean enableKBArticleAssetLinks();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-view-count-increment",
		required = false
	)
	public boolean enableKBArticleViewCountIncrement();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-subscriptions",
		required = false
	)
	public boolean enableKBArticleSubscriptions();

	@Meta.AD(
		deflt = "true", name = "enable-kb-article-history", required = false
	)
	public boolean enableKBArticleHistory();

	@Meta.AD(deflt = "true", name = "enable-kb-article-print", required = false)
	public boolean enableKBArticlePrint();

	@Meta.AD(
		deflt = "menu", name = "social-bookmarks-display-style",
		required = false
	)
	public String socialBookmarksDisplayStyle();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/social.bookmark.types}",
		name = "social-bookmarks-types", required = false
	)
	public String socialBookmarksTypes();

	@Meta.AD(
		deflt = StringPool.BLANK, name = "content-root-prefix", required = false
	)
	public String contentRootPrefix();

	@Meta.AD(deflt = "3", name = "maximum-nesting-level", required = false)
	public int maxNestingLevel();

}