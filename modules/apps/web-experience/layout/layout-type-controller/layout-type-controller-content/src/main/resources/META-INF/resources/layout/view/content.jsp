<%--
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
--%>

<%@ include file="/layout/view/init.jsp" %>

<%
String ppid = ParamUtil.getString(request, "p_p_id");

if ((themeDisplay.isStatePopUp() || themeDisplay.isWidget() || layoutTypePortlet.hasStateMax()) && Validator.isNotNull(ppid)) {
	String templateId = null;
	String templateContent = null;
	String langType = null;

	if (themeDisplay.isStatePopUp() || themeDisplay.isWidget()) {
		templateId = theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR + "pop_up";
		templateContent = LayoutTemplateLocalServiceUtil.getContent("pop_up", true, theme.getThemeId());
		langType = LayoutTemplateLocalServiceUtil.getLangType("pop_up", true, theme.getThemeId());
	}
	else {
		ppid = StringUtil.split(layoutTypePortlet.getStateMax())[0];

		templateId = theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR + "max";
		templateContent = LayoutTemplateLocalServiceUtil.getContent("max", true, theme.getThemeId());
		langType = LayoutTemplateLocalServiceUtil.getLangType("max", true, theme.getThemeId());
	}

	if (Validator.isNotNull(templateContent)) {
		RuntimePageUtil.processTemplate(request, response, ppid, new StringTemplateResource(templateId, templateContent), langType);
	}
}
else {
	StringBundler sb = new StringBundler(fragmentEntryLinks.size());

	for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
		sb.append(FragmentEntryRenderUtil.renderFragmentEntryLink(fragmentEntryLink));
	}

	TemplateResource templateResource = new StringTemplateResource("template_id", sb.toString());

	Template template = TemplateManagerUtil.getTemplate(TemplateConstants.LANG_TYPE_FTL, templateResource, false);

	TemplateManager templateManager = TemplateManagerUtil.getTemplateManager(TemplateConstants.LANG_TYPE_FTL);

	templateManager.addTaglibSupport(template, request, response);
	templateManager.addTaglibTheme(template, "taglibLiferay", request, response);

	UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

	template.put(TemplateConstants.WRITER, unsyncStringWriter);

	template.processTemplate(unsyncStringWriter);
%>

	<%= unsyncStringWriter.toString() %>

<%
}
%>