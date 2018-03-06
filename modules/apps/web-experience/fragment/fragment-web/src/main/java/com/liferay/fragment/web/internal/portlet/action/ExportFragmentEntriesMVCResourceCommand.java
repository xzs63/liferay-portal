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

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.web.internal.portlet.util.ExportUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/export_fragment_entries"
	},
	service = MVCResourceCommand.class
)
public class ExportFragmentEntriesMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long[] exportFragmentEntryIds = null;

		long fragmentEntryId = ParamUtil.getLong(
			resourceRequest, "fragmentEntryId");

		if (fragmentEntryId > 0) {
			exportFragmentEntryIds = new long[] {fragmentEntryId};
		}
		else {
			exportFragmentEntryIds = ParamUtil.getLongValues(
				resourceRequest, "rowIds");
		}

		try {
			List<FragmentEntry> fragmentEntries = new ArrayList<>();

			for (long exportFragmentEntryId : exportFragmentEntryIds) {
				FragmentEntry fragmentEntry =
					_fragmentEntryService.fetchFragmentEntry(
						exportFragmentEntryId);

				fragmentEntries.add(fragmentEntry);
			}

			File file = _exportUtil.exportFragmentEntries(fragmentEntries);

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"entries-" + Time.getTimestamp() + ".zip",
				new FileInputStream(file), ContentTypes.APPLICATION_ZIP);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		return true;
	}

	@Reference
	private ExportUtil _exportUtil;

	@Reference
	private FragmentEntryService _fragmentEntryService;

}