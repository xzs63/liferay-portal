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

<%@ include file="/display/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect", currentURL);

long formInstanceId = ddmFormDisplayContext.getFormInstanceId();

String languageId = LanguageUtil.getLanguageId(request);
Locale displayLocale = LocaleUtil.fromLanguageId(languageId);
%>

<c:choose>
	<c:when test="<%= formInstanceId == 0 %>">
		<div class="alert alert-info">
			<liferay-ui:message key="select-an-existing-form-or-add-a-form-to-be-displayed-in-this-application" />
		</div>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="<%= ddmFormDisplayContext.isShowSuccessPage() %>">

				<%
				DDMFormSuccessPageSettings ddmFormSuccessPageSettings = ddmFormDisplayContext.getDDMFormSuccessPageSettings();

				LocalizedValue title = ddmFormSuccessPageSettings.getTitle();
				LocalizedValue body = ddmFormSuccessPageSettings.getBody();
				%>

				<div class="portlet-forms">
					<div class="ddm-form-basic-info">
						<div class="container-fluid-1280">
							<h1 class="ddm-form-name"><%= GetterUtil.getString(title.getString(displayLocale), title.getString(title.getDefaultLocale())) %></h1>

							<h5 class="ddm-form-description"><%= GetterUtil.getString(body.getString(displayLocale), body.getString(body.getDefaultLocale())) %></h5>
						</div>
					</div>
				</div>
			</c:when>
			<c:when test="<%= ddmFormDisplayContext.isFormAvailable() %>">
				<portlet:actionURL name="addFormInstanceRecord" var="addFormInstanceRecordActionURL" />

				<div class="portlet-forms">
					<aui:form action="<%= addFormInstanceRecordActionURL %>" data-DDMFormInstanceId="<%= formInstanceId %>" method="post" name="fm">

						<%
						String redirectURL = ddmFormDisplayContext.getRedirectURL();
						%>

						<c:if test="<%= Validator.isNull(redirectURL) %>">
							<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
						</c:if>

						<%
						DDMFormInstance formInstance = ddmFormDisplayContext.getFormInstance();
						%>

						<aui:input name="groupId" type="hidden" value="<%= formInstance.getGroupId() %>" />
						<aui:input name="formInstanceId" type="hidden" value="<%= formInstance.getFormInstanceId() %>" />
						<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

						<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
						<liferay-ui:error exception="<%= DDMFormRenderingException.class %>" message="unable-to-render-the-selected-form" />
						<liferay-ui:error exception="<%= DDMFormValuesValidationException.class %>" message="field-validation-failed" />

						<liferay-ui:error exception="<%= DDMFormValuesValidationException.MustSetValidValue.class %>">

							<%
							DDMFormValuesValidationException.MustSetValidValue msvv = (DDMFormValuesValidationException.MustSetValidValue)errorException;
							%>

							<liferay-ui:message arguments="<%= msvv.getFieldName() %>" key="validation-failed-for-field-x" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= DDMFormValuesValidationException.RequiredValue.class %>">

							<%
							DDMFormValuesValidationException.RequiredValue rv = (DDMFormValuesValidationException.RequiredValue)errorException;
							%>

							<liferay-ui:message arguments="<%= rv.getFieldName() %>" key="no-value-is-defined-for-field-x" translateArguments="<%= false %>" />
						</liferay-ui:error>

						<liferay-ui:error exception="<%= NoSuchFormInstanceException.class %>" message="the-selected-form-no-longer-exists" />
						<liferay-ui:error exception="<%= NoSuchStructureException.class %>" message="unable-to-retrieve-the-definition-of-the-selected-form" />
						<liferay-ui:error exception="<%= NoSuchStructureLayoutException.class %>" message="unable-to-retrieve-the-layout-of-the-selected-form" />

						<liferay-ui:error-principal />

						<c:if test="<%= ddmFormDisplayContext.isFormShared() %>">
							<div class="container-fluid-1280">
								<div class="locale-actions">
									<liferay-ui:language formAction="<%= currentURL %>" languageId="<%= languageId %>" languageIds="<%= ddmFormDisplayContext.getAvailableLanguageIds() %>" />
								</div>
							</div>
						</c:if>

						<div class="ddm-form-basic-info">
							<div class="container-fluid-1280">
								<h1 class="ddm-form-name"><%= formInstance.getName(displayLocale) %></h1>

								<%
								String description = formInstance.getDescription(displayLocale);
								%>

								<c:if test="<%= Validator.isNotNull(description) %>">
									<h5 class="ddm-form-description"><%= description %></h5>
								</c:if>
							</div>
						</div>

						<div class="container-fluid-1280 ddm-form-builder-app">
							<%= ddmFormDisplayContext.getDDMFormHTML() %>

							<aui:input name="empty" type="hidden" value="" />
						</div>
					</aui:form>
				</div>

				<aui:script use="aui-base">
					var <portlet:namespace />intervalId;

					function <portlet:namespace />clearPortletHandlers(event) {
						if (<portlet:namespace />intervalId) {
							clearInterval(<portlet:namespace />intervalId);
						}

						Liferay.detach('destroyPortlet', <portlet:namespace />clearPortletHandlers);
					};

					Liferay.on('destroyPortlet', <portlet:namespace />clearPortletHandlers);

					<c:choose>
						<c:when test="<%= ddmFormDisplayContext.isAutosaveEnabled() %>">
							var <portlet:namespace />form;

							<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="addFormInstanceRecord" var="autoSaveFormInstanceRecordURL">
								<portlet:param name="autoSave" value="<%= Boolean.TRUE.toString() %>" />
								<portlet:param name="preview" value="<%= String.valueOf(ddmFormDisplayContext.isPreview()) %>" />
							</liferay-portlet:resourceURL>

							function <portlet:namespace />autoSave() {
								A.io.request(
									'<%= autoSaveFormInstanceRecordURL.toString() %>',
									{
										data: {
											<portlet:namespace />formInstanceId: <%= formInstanceId %>,
											<portlet:namespace />serializedDDMFormValues: JSON.stringify(<portlet:namespace />form.toJSON())
										},
										method: 'POST'
									}
								);
							}

							function <portlet:namespace />startAutoSave() {
								if (<portlet:namespace />intervalId) {
									clearInterval(<portlet:namespace />intervalId);
								}

								<portlet:namespace />intervalId = setInterval(<portlet:namespace />autoSave, 60000);
							}

							function <portlet:namespace />fireFormView() {
								Liferay.fire('ddmFormView', {formId: <%= formInstanceId %>});

								Liferay.fire("ddmFormPageShow", {
									formId: <%= formInstanceId %>,
									page: 1
								});
							}

							<portlet:namespace />form = Liferay.component('<%= ddmFormDisplayContext.getContainerId() %>DDMForm');

							if (<portlet:namespace />form) {
								<portlet:namespace />startAutoSave();

								<portlet:namespace />fireFormView();
							}
							else {
								Liferay.after(
									'<%= ddmFormDisplayContext.getContainerId() %>DDMForm:render',
									function(event) {
										<portlet:namespace />form = Liferay.component('<%= ddmFormDisplayContext.getContainerId() %>DDMForm');

										if (<portlet:namespace />form) {
											<portlet:namespace />startAutoSave();

											<portlet:namespace />fireFormView();
										}
									}
								);
							}
						</c:when>
						<c:otherwise>
							function <portlet:namespace />startAutoExtendSession() {
								if (<portlet:namespace />intervalId) {
									clearInterval(<portlet:namespace />intervalId);
								}

								var tenSeconds = 10000;

								var time = Liferay.Session.get('sessionLength') || tenSeconds;

								<portlet:namespace />intervalId = setInterval(<portlet:namespace />extendSession, (time/2));
							}

							function <portlet:namespace />extendSession() {
								Liferay.Session.extend();
							}

							<portlet:namespace />startAutoExtendSession();
						</c:otherwise>
					</c:choose>
				</aui:script>
			</c:when>
			<c:otherwise>
				<div class="alert alert-warning">
					<liferay-ui:message key="this-form-not-available-or-it-was-not-published" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<c:if test="<%= ddmFormDisplayContext.isShowConfigurationIcon() %>">
	<div class="icons-container lfr-meta-actions">
		<div class="lfr-icon-actions">
			<liferay-ui:icon
				cssClass="lfr-icon-action lfr-icon-action-configuration"
				iconCssClass="icon-cog"
				label="<%= true %>"
				message="select-form"
				method="get"
				onClick="<%= portletDisplay.getURLConfigurationJS() %>"
				url="<%= portletDisplay.getURLConfiguration() %>"
			/>
		</div>
	</div>
</c:if>