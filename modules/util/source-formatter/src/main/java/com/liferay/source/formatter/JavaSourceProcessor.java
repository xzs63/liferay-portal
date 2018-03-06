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

package com.liferay.source.formatter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.source.formatter.checkstyle.Checker;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.DebugUtil;

import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JavaSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws Exception {
		String[] includes = getIncludes();

		if (ArrayUtil.isEmpty(includes)) {
			return new ArrayList<>();
		}

		Collection<String> fileNames = null;

		if (portalSource || subrepository) {
			fileNames = _getPortalJavaFiles(includes);
		}
		else {
			fileNames = _getPluginJavaFiles(includes);
		}

		return new ArrayList<>(fileNames);
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected void format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		Set<String> modifiedContents = new HashSet<>();
		Set<String> modifiedMessages = new TreeSet<>();

		String newContent = format(
			file, fileName, absolutePath, content, content, modifiedContents,
			modifiedMessages, 0);

		file = processFormattedFile(
			file, fileName, content, newContent, modifiedMessages);

		_processCheckstyle(file);
	}

	@Override
	protected void postFormat() throws Exception {
		if (_ungeneratedFiles.isEmpty()) {
			return;
		}

		Checker checker = _getChecker();

		checker.process(_ungeneratedFiles);

		Set<SourceFormatterMessage> sourceFormatterMessages =
			checker.getSourceFormatterMessages();

		for (SourceFormatterMessage sourceFormatterMessage :
				sourceFormatterMessages) {

			processMessage(
				sourceFormatterMessage.getFileName(), sourceFormatterMessage);

			printError(
				sourceFormatterMessage.getFileName(),
				sourceFormatterMessage.toString());
		}
	}

	private Checker _getChecker() throws Exception {
		if (_checker != null) {
			return _checker;
		}

		Configuration configuration = CheckstyleUtil.getConfiguration(
			"checkstyle.xml");

		configuration = CheckstyleUtil.addAttribute(
			configuration, "maxLineLength",
			String.valueOf(sourceFormatterArgs.getMaxLineLength()),
			"com.liferay.source.formatter.checkstyle.checks.Append");
		configuration = CheckstyleUtil.addAttribute(
			configuration, "maxLineLength",
			String.valueOf(sourceFormatterArgs.getMaxLineLength()),
			"com.liferay.source.formatter.checkstyle.checks.Concat");
		configuration = CheckstyleUtil.addAttribute(
			configuration, "maxLineLength",
			String.valueOf(sourceFormatterArgs.getMaxLineLength()),
			"com.liferay.source.formatter.checkstyle.checks.PlusStatement");
		configuration = CheckstyleUtil.addAttribute(
			configuration, "showDebugInformation",
			String.valueOf(sourceFormatterArgs.isShowDebugInformation()),
			"com.liferay.*");

		if (sourceFormatterArgs.isShowDebugInformation()) {
			DebugUtil.addCheckNames(
				CheckType.CHECKSTYLE,
				CheckstyleUtil.getCheckNames(configuration));
		}

		_checker = CheckstyleUtil.getChecker(
			configuration, getSourceFormatterSuppressions(),
			sourceFormatterArgs);

		return _checker;
	}

	private String[] _getPluginExcludes(String pluginDirectoryName) {
		return new String[] {
			pluginDirectoryName + "**/model/*Clp.java",
			pluginDirectoryName + "**/model/impl/*BaseImpl.java",
			pluginDirectoryName + "**/model/impl/*Model.java",
			pluginDirectoryName + "**/model/impl/*ModelImpl.java",
			pluginDirectoryName + "**/service/**/service/*Service.java",
			pluginDirectoryName + "**/service/**/service/*ServiceClp.java",
			pluginDirectoryName + "**/service/**/service/*ServiceFactory.java",
			pluginDirectoryName + "**/service/**/service/*ServiceUtil.java",
			pluginDirectoryName + "**/service/**/service/*ServiceWrapper.java",
			pluginDirectoryName + "**/service/**/service/ClpSerializer.java",
			pluginDirectoryName +
				"**/service/**/service/messaging/*ClpMessageListener.java",
			pluginDirectoryName +
				"**/service/**/service/persistence/*Finder.java",
			pluginDirectoryName +
				"**/service/**/service/persistence/*Util.java",
			pluginDirectoryName + "**/service/base/*ServiceBaseImpl.java",
			pluginDirectoryName + "**/service/base/*ServiceClpInvoker.java",
			pluginDirectoryName + "**/service/http/*JSONSerializer.java",
			pluginDirectoryName + "**/service/http/*ServiceHttp.java",
			pluginDirectoryName + "**/service/http/*ServiceJSON.java",
			pluginDirectoryName + "**/service/http/*ServiceSoap.java",
			pluginDirectoryName + "**/tools/templates/**"
		};
	}

	private Collection<String> _getPluginJavaFiles(String[] includes)
		throws Exception {

		Collection<String> fileNames = new TreeSet<>();

		String[] excludes = _getPluginExcludes(StringPool.BLANK);

		fileNames.addAll(getFileNames(excludes, includes));

		return fileNames;
	}

	private Collection<String> _getPortalJavaFiles(String[] includes)
		throws Exception {

		Collection<String> fileNames = new TreeSet<>();

		String[] excludes = {
			"**/*_IW.java", "**/counter/service/**",
			"**/model/impl/*Model.java", "**/model/impl/*ModelImpl.java",
			"**/portal/service/**", "**/portal-client/**",
			"**/portal-web/test/**/*Test.java", "**/test/*-generated/**"
		};

		if (subrepository) {
			excludes = ArrayUtil.append(
				excludes, _getPluginExcludes(StringPool.BLANK));
		}
		else {
			for (String directoryName :
					getPluginsInsideModulesDirectoryNames()) {

				excludes = ArrayUtil.append(
					excludes, _getPluginExcludes("**" + directoryName));
			}
		}

		fileNames.addAll(getFileNames(excludes, includes));

		excludes = new String[] {
			"**/portal-client/**", "**/tools/ext_tmpl/**", "**/*_IW.java",
			"**/test/**/*PersistenceTest.java"
		};
		includes = new String[] {
			"**/com/liferay/portal/kernel/service/ServiceContext*.java",
			"**/model/BaseModel.java", "**/model/impl/BaseModelImpl.java",
			"**/portal-test/**/portal/service/**/*.java",
			"**/portal-test-integration/**/portal/service/**/*.java",
			"**/service/Base*.java",
			"**/service/PersistedModelLocalService*.java",
			"**/service/configuration/**/*.java",
			"**/service/http/*HttpTest.java", "**/service/http/*SoapTest.java",
			"**/service/http/TunnelUtil.java", "**/service/impl/*.java",
			"**/service/jms/*.java", "**/service/permission/*.java",
			"**/service/persistence/BasePersistence.java",
			"**/service/persistence/BatchSession*.java",
			"**/service/persistence/*FinderImpl.java",
			"**/service/persistence/*Query.java",
			"**/service/persistence/impl/*.java",
			"**/portal-impl/test/**/*.java", "**/util-bridges/**/*.java"
		};

		fileNames.addAll(getFileNames(excludes, includes));

		return fileNames;
	}

	private synchronized void _processCheckstyle(File file) throws Exception {
		_ungeneratedFiles.add(file);

		if (_ungeneratedFiles.size() == _CHECKSTYLE_BATCH_SIZE) {
			Checker checker = _getChecker();

			checker.process(_ungeneratedFiles);

			_ungeneratedFiles = new ArrayList<>();
		}
	}

	private static final int _CHECKSTYLE_BATCH_SIZE = 1000;

	private static final String[] _INCLUDES = {"**/*.java"};

	private Checker _checker;
	private List<File> _ungeneratedFiles = new ArrayList<>();

}