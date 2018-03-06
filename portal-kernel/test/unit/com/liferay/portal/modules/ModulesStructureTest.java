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

package com.liferay.portal.modules;

import aQute.bnd.version.Version;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.modules.util.GradleDependency;
import com.liferay.portal.modules.util.ModulesStructureTestUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 * @author Andrea Di Giorgi
 */
public class ModulesStructureTest {

	@BeforeClass
	public static void setUpClass() throws IOException {
		_buildProperties = new Properties();

		try (InputStream inputStream = Files.newInputStream(
				Paths.get("build.properties"))) {

			_buildProperties.load(inputStream);
		}

		_modulesDirPath = Paths.get("modules");
	}

	@Test
	public void testScanBuildScripts() throws IOException {
		final String gitRepoBuildGradleTemplate = _getGradleTemplate(
			"dependencies/git_repo_build_gradle.tmpl");
		final String gitRepoSettingsGradleTemplate = _getGradleTemplate(
			"dependencies/git_repo_settings_gradle.tmpl");

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (dirPath.equals(_modulesDirPath)) {
						return FileVisitResult.CONTINUE;
					}

					String dirName = String.valueOf(dirPath.getFileName());

					if ((dirName.charAt(0) == '.') ||
						dirName.equals("node_modules")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					Path buildGradlePath = dirPath.resolve("build.gradle");
					Path buildXMLPath = dirPath.resolve("build.xml");
					Path ivyXmlPath = dirPath.resolve("ivy.xml");

					boolean gitRepo = Files.exists(
						dirPath.resolve(_GIT_REPO_FILE_NAME));

					if (gitRepo) {
						_testGitRepoBuildScripts(
							dirPath, gitRepoBuildGradleTemplate,
							gitRepoSettingsGradleTemplate);
					}
					else {
						Path gradlePropertiesPath = dirPath.resolve(
							"gradle.properties");

						Assert.assertFalse(
							"Forbidden " + gradlePropertiesPath,
							Files.deleteIfExists(gradlePropertiesPath));

						Path settingsGradlePath = dirPath.resolve(
							"settings.gradle");

						Assert.assertFalse(
							"Forbidden " + settingsGradlePath,
							Files.deleteIfExists(settingsGradlePath));

						if (Files.exists(dirPath.resolve("app.bnd"))) {
							_testEquals(buildGradlePath, _APP_BUILD_GRADLE);
						}
						else {
							Assert.assertFalse(
								"Forbidden " + buildGradlePath,
								Files.exists(buildGradlePath) &&
								ModulesStructureTestUtil.contains(
									buildGradlePath, _APP_BUILD_GRADLE,
									"apply plugin: " +
										"\"com.liferay.defaults.plugin\"",
									"apply plugin: " +
										"\"com.liferay.root.defaults." +
											"plugin\""));
						}

						Path buildExtGradlePath = dirPath.resolve(
							"build-ext.gradle");

						Assert.assertFalse(
							"Forbidden " + buildExtGradlePath,
							Files.deleteIfExists(buildExtGradlePath));
					}

					if (Files.exists(dirPath.resolve("bnd.bnd"))) {
						Assert.assertTrue(
							"Missing " + buildGradlePath,
							Files.exists(buildGradlePath));
						Assert.assertFalse(
							"Forbidden " + buildXMLPath,
							Files.deleteIfExists(buildXMLPath));

						Assert.assertFalse(
							"Forbidden " + ivyXmlPath,
							Files.deleteIfExists(ivyXmlPath));

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(buildXMLPath)) {
						Assert.assertFalse(
							"Forbidden " + buildGradlePath,
							Files.notExists(ivyXmlPath) &&
							Files.exists(buildGradlePath));

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(dirPath.resolve("package.json"))) {
						Path packageJSONPath = dirPath.resolve("package.json");

						if (ModulesStructureTestUtil.contains(
								packageJSONPath, "\"liferay-theme-tasks\":")) {

							_testThemeBuildScripts(dirPath);
						}
						else {
							Path packageLockJSONPath = dirPath.resolve(
								"package-lock.json");

							Assert.assertTrue(
								"Missing " + packageLockJSONPath,
								Files.exists(packageLockJSONPath));
						}

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanGitHub() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals("node_modules")) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.equals("CODEOWNERS")) {
						_testGitHubCodeOwners(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanGradleFiles() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals("archetype-resources") ||
						dirName.equals("gradleTest")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (StringUtil.endsWith(fileName, ".gradle")) {
						_testGradleFile(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanIgnoreFiles() throws IOException {
		final String gitRepoGitIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/git_repo_gitignore.tmpl");
		final String themeGitIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/theme_gitignore.tmpl");
		final String themeNpmIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/theme_npmignore.tmpl");

		final Set<String> gitRepoGitIgnoreTemplateLines = SetUtil.fromString(
			gitRepoGitIgnoreTemplate);

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals("gradleTest") ||
						dirName.equals("project-templates")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (dirPath.equals(_modulesDirPath) ||
						Files.exists(dirPath.resolve(_GIT_REPO_FILE_NAME))) {

						_testGitRepoIgnoreFiles(
							dirPath, gitRepoGitIgnoreTemplate);
					}
					else if (!dirPath.equals(_modulesDirPath) &&
							 Files.exists(dirPath.resolve("build.xml"))) {

						_testAntPluginIgnoreFiles(dirPath);
					}
					else if (StringUtil.startsWith(
								dirName, "frontend-theme-") &&
							 Files.exists(dirPath.resolve("gulpfile.js"))) {

						_testThemeIgnoreFiles(
							dirPath, themeGitIgnoreTemplate,
							themeNpmIgnoreTemplate);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.equals(".gitignore")) {
						_testGitIgnoreFile(path, gitRepoGitIgnoreTemplateLines);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanLog4JConfigurationXML() throws IOException {
		final Map<String, String> renameMap = new HashMap<>();

		renameMap.put(
			"src/main/resources/META-INF/portal-log4j-ext.xml",
			"module-log4j-ext.xml");
		renameMap.put(
			"src/main/resources/META-INF/portal-log4j.xml", "module-log4j.xml");
		renameMap.put(
			"src/META-INF/portal-log4j-ext.xml", "module-log4j-ext.xml");
		renameMap.put("src/META-INF/portal-log4j.xml", "module-log4j.xml");

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					if (Files.exists(dirPath.resolve("bnd.bnd"))) {
						for (Entry<String, String> entry :
								renameMap.entrySet()) {

							Path path = dirPath.resolve(entry.getKey());

							if (Files.exists(path)) {
								Assert.fail(
									StringBundler.concat(
										"Please rename ", String.valueOf(path),
										" to ",
										String.valueOf(
											path.resolveSibling(
												entry.getValue()))));
							}
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanMarkerFiles() throws IOException {
		final Set<String> fileNames = new HashSet<>();

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (StringUtil.startsWith(fileName, ".lfrbuild-")) {
						fileNames.add(fileName);

						if (_nonemptyMarkerFileNames.contains(fileName)) {
							String content = ModulesStructureTestUtil.read(
								path);

							Assert.assertEquals(
								"Forbidden leading or trailing whitespaces " +
									"in " + path,
								content.trim(), content);
						}
						else {
							Assert.assertEquals(
								"Marker file " + path + " must be empty", 0,
								basicFileAttributes.size());
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		Path readmePath = _modulesDirPath.resolve("README.markdown");

		String readme = ModulesStructureTestUtil.read(readmePath);

		for (String fileName : fileNames) {
			Assert.assertTrue(
				StringBundler.concat(
					"Please document the \"", fileName, "\" marker file in ",
					String.valueOf(readmePath)),
				readme.contains("`" + fileName + "`"));
		}
	}

	@Test
	public void testScanReadmeFiles() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals("node_modules")) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					Path path = dirPath.resolve("README.markdown");

					if (Files.exists(path)) {
						BasicFileAttributes readmeBasicFileAttributes =
							Files.readAttributes(
								path, BasicFileAttributes.class);

						Assert.assertNotEquals(
							"Please delete the empty readme file " + path, 0,
							readmeBasicFileAttributes.size());
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _addGradlePluginNames(
			Set<String> pluginNames, String pluginNamePrefix,
			Path buildGradlePath, String pluginIdPrefix,
			String[] pluginIdSuffixes)
		throws IOException {

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new FileReader(buildGradlePath.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = StringUtil.trim(line);

				for (String pluginIdSuffix : pluginIdSuffixes) {
					String pluginLine = StringBundler.concat(
						"apply plugin: \"", pluginIdPrefix, pluginIdSuffix,
						"\"");

					if (line.equals(pluginLine)) {
						pluginNames.add(pluginNamePrefix + pluginIdSuffix);
					}
				}
			}
		}
	}

	private GradleDependency _getActiveGradleDependency(
		List<GradleDependency> gradleDependencies,
		GradleDependency gradleDependency) {

		int configurationPos = _gradleConfigurations.indexOf(
			gradleDependency.getConfiguration());
		String moduleGroup = gradleDependency.getModuleGroup();
		String moduleName = gradleDependency.getModuleName();
		Version moduleVersion = gradleDependency.getModuleVersion();

		for (GradleDependency curGradleDependency : gradleDependencies) {
			if (!moduleGroup.equals(curGradleDependency.getModuleGroup()) ||
				!moduleName.equals(curGradleDependency.getModuleName()) ||
				!_gradleConfigurations.contains(
					curGradleDependency.getConfiguration())) {

				continue;
			}

			int curConfigurationPos = _gradleConfigurations.indexOf(
				curGradleDependency.getConfiguration());

			int value = moduleVersion.compareTo(
				curGradleDependency.getModuleVersion());

			if (((curConfigurationPos == configurationPos) && (value < 0)) ||
				(curConfigurationPos < configurationPos) && (value <= 0)) {

				return curGradleDependency;
			}
		}

		return gradleDependency;
	}

	private String _getAntPluginsGitIgnore(final Path dirPath, String gitIgnore)
		throws IOException {

		if (dirPath.equals(_modulesDirPath)) {
			return gitIgnore;
		}

		final Set<String> pluginDirNames = new TreeSet<>();

		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path pluginDirPath,
					BasicFileAttributes basicFileAttributes) {

					if (Files.exists(pluginDirPath.resolve("build.xml")) &&
						Files.exists(
							pluginDirPath.resolve(
								"docroot/WEB-INF/service.xml"))) {

						String pluginDirName = String.valueOf(
							dirPath.relativize(pluginDirPath));

						if (File.separatorChar != CharPool.SLASH) {
							pluginDirName = StringUtil.replace(
								pluginDirName, File.separatorChar,
								CharPool.SLASH);
						}

						pluginDirNames.add(pluginDirName);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		if (pluginDirNames.isEmpty()) {
			return gitIgnore;
		}

		StringBundler sb = new StringBundler(pluginDirNames.size() * 14 + 2);

		if (Validator.isNotNull(gitIgnore)) {
			sb.append(gitIgnore);
			sb.append(CharPool.NEW_LINE);
			sb.append(CharPool.NEW_LINE);
		}

		boolean first = true;

		for (String pluginDirName : pluginDirNames) {
			if (!first) {
				sb.append(CharPool.NEW_LINE);
			}

			first = false;

			sb.append("!/");
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib");

			sb.append(CharPool.NEW_LINE);

			sb.append(CharPool.SLASH);
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib/*");

			sb.append(CharPool.NEW_LINE);

			sb.append("!/");
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib/");
			sb.append(pluginDirName);
			sb.append("-service.jar");
		}

		return sb.toString();
	}

	private String _getGitRepoBuildGradle(
			Path dirPath, String buildGradleTemplate)
		throws IOException {

		if (Files.notExists(dirPath.resolve("build-ext.gradle"))) {
			buildGradleTemplate = StringUtil.removeSubstring(
				buildGradleTemplate,
				StringPool.NEW_LINE + StringPool.NEW_LINE +
					"apply from: \"build-ext.gradle\"");
		}

		final Set<String> pluginNames = new TreeSet<>();

		pluginNames.add("com.liferay.gradle.plugins.defaults");

		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path buildGradlePath = dirPath.resolve("build.gradle");

					if (Files.exists(buildGradlePath) &&
						Files.notExists(dirPath.resolve(_GIT_REPO_FILE_NAME))) {

						_addGradlePluginNames(
							pluginNames, "com.liferay.gradle.plugins.",
							buildGradlePath, "com.liferay.",
							new String[] {"maven.plugin.builder"});

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		StringBundler sb = new StringBundler(pluginNames.size() * 4 - 1);

		int i = 0;

		for (String pluginName : pluginNames) {
			if (i > 0) {
				sb.append(CharPool.NEW_LINE);
			}

			sb.append("\t\tclasspath group: \"com.liferay\", name: \"");
			sb.append(pluginName);
			sb.append("\", version: \"latest.release\"");

			i++;
		}

		return StringUtil.replace(
			buildGradleTemplate, "[$BUILDSCRIPT_DEPENDENCIES$]", sb.toString());
	}

	private Path _getGitRepoPath(Path dirPath) {
		while (dirPath != null) {
			Path gitRepoPath = dirPath.resolve(_GIT_REPO_FILE_NAME);

			if (Files.exists(gitRepoPath)) {
				return gitRepoPath;
			}

			dirPath = dirPath.getParent();
		}

		return null;
	}

	private String _getGradleTemplate(String name) {
		String template = StringUtil.read(ModulesStructureTest.class, name);

		return StringUtil.replace(
			template, "[$REPOSITORY_URL$]", _REPOSITORY_URL);
	}

	private String _getProjectPathPrefix(Path dirPath) {
		String projectPathPrefix = String.valueOf(
			_modulesDirPath.relativize(dirPath));

		projectPathPrefix =
			":" +
				StringUtil.replace(
					projectPathPrefix, File.separatorChar, CharPool.COLON);

		return projectPathPrefix;
	}

	private boolean _isEmptyGitRepo(Path dirPath) {
		File dir = dirPath.toFile();

		String[] fileNames = dir.list();

		if ((fileNames.length == 1) &&
			_GIT_REPO_FILE_NAME.equals(fileNames[0])) {

			return true;
		}

		return false;
	}

	@SuppressWarnings("unused")
	private boolean _isInGitRepoReadOnly(Path dirPath) throws IOException {
		Path gitRepoPath = _getGitRepoPath(dirPath);

		if (gitRepoPath == null) {
			return false;
		}

		if (ModulesStructureTestUtil.contains(gitRepoPath, "mode = pull")) {
			return true;
		}

		return false;
	}

	private boolean _isInPrivateModulesDir(Path dirPath) {
		Path relativePath = _modulesDirPath.relativize(dirPath);

		if (relativePath.startsWith("private")) {
			return true;
		}

		return false;
	}

	private void _testAntPluginIgnoreFiles(Path dirPath) throws IOException {
		if (_getGitRepoPath(dirPath) == null) {
			Path parentDirPath = dirPath.getParent();

			_testEquals(
				parentDirPath.resolve(".gitignore"),
				_getAntPluginsGitIgnore(parentDirPath, null));
		}
	}

	private void _testEquals(Path path, String expected) throws IOException {
		if (Validator.isNotNull(expected)) {
			String actual = ModulesStructureTestUtil.read(path);

			Assert.assertEquals("Incorrect " + path, expected, actual);
		}
		else {
			Assert.assertFalse("Forbidden " + path, Files.exists(path));
		}
	}

	private void _testGitHubCodeOwners(Path path) {
		Path dirPath = path.getParent();

		Assert.assertEquals(
			"Forbidden " + path, ".github",
			String.valueOf(dirPath.getFileName()));

		Path rootDirPath = dirPath.getParent();

		Assert.assertTrue(
			"Forbidden " + path,
			Files.exists(rootDirPath.resolve(_GIT_REPO_FILE_NAME)));
	}

	private void _testGitIgnoreFile(
			Path path, Set<String> gitRepoGitIgnoreTemplateLines)
		throws IOException {

		Path dirPath = path.getParent();

		boolean gitRepoGitIgnore = Files.exists(
			dirPath.resolve(_GIT_REPO_FILE_NAME));

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(path.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = StringUtil.trim(line);

				if (!gitRepoGitIgnore && !dirPath.equals(_modulesDirPath)) {
					Assert.assertFalse(
						"Forbidden \"" + line + "\" in " + path,
						gitRepoGitIgnoreTemplateLines.contains(line));
				}

				if (!StringUtil.startsWith(line, "!/")) {
					continue;
				}

				int end = line.indexOf(CharPool.SLASH, 2);

				if (end == -1) {
					end = line.length();
				}

				String name = line.substring(2, end);

				Assert.assertTrue(
					StringBundler.concat(
						"Incorrect \"", line, "\" in ", String.valueOf(path)),
					Files.exists(dirPath.resolve(name)));
			}
		}
	}

	private void _testGitRepoBuildScripts(
			Path dirPath, String buildGradleTemplate,
			String settingsGradleTemplate)
		throws IOException {

		if (_isEmptyGitRepo(dirPath)) {
			return;
		}

		boolean privateRepo = _isInPrivateModulesDir(dirPath);

		Path buildGradlePath = dirPath.resolve("build.gradle");
		Path gradlePropertiesPath = dirPath.resolve("gradle.properties");
		Path settingsGradlePath = dirPath.resolve("settings.gradle");

		String buildGradle = ModulesStructureTestUtil.read(buildGradlePath);

		Assert.assertEquals(
			"Incorrect " + buildGradlePath,
			_getGitRepoBuildGradle(dirPath, buildGradleTemplate), buildGradle);

		String gradleProperties = ModulesStructureTestUtil.read(
			gradlePropertiesPath);

		Assert.assertEquals(
			"Forbidden leading or trailing whitespaces in " +
				gradlePropertiesPath,
			gradleProperties.trim(), gradleProperties);

		String gradlePropertiesPrefix = StringUtil.replace(
			String.valueOf(dirPath.getFileName()), CharPool.DASH,
			CharPool.PERIOD);

		gradlePropertiesPrefix = "com.liferay." + gradlePropertiesPrefix;

		Pattern gradlePropertiesPattern = Pattern.compile(
			StringUtil.replace(gradlePropertiesPrefix, CharPool.PERIOD, "\\.") +
				"(\\.[a-z0-9]+)+");

		String previousKey = null;
		String projectGroup = null;
		String projectPathPrefix = null;
		String repositoryPrivatePassword = null;
		String repositoryPrivateUrl = null;
		String repositoryPrivateUsername = null;

		String[] lines = StringUtil.split(gradleProperties, CharPool.NEW_LINE);

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			Assert.assertEquals(
				StringBundler.concat(
					"Forbidden leading or trailing whitespaces in line ",
					String.valueOf(i + 1), " of ",
					String.valueOf(gradlePropertiesPath)),
				line.trim(), line);

			Assert.assertFalse(
				"Forbidden empty line in " + gradlePropertiesPath,
				Validator.isNull(line));

			int pos = line.indexOf(CharPool.EQUAL);

			Assert.assertTrue(
				StringBundler.concat(
					"Incorrect line \"", line, "\" in ",
					String.valueOf(gradlePropertiesPath)),
				pos != -1);

			String key = line.substring(0, pos);
			String value = line.substring(pos + 1);

			Assert.assertTrue(
				gradlePropertiesPath +
					" contains duplicate lines or is not sorted",
				(previousKey == null) || (key.compareTo(previousKey) > 0));

			if (key.equals(_GIT_REPO_GRADLE_PROJECT_GROUP_KEY)) {
				projectGroup = value;
			}
			else if (key.equals(_GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY)) {
				projectPathPrefix = value;
			}
			else if (privateRepo &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD)) {

				repositoryPrivatePassword = value;
			}
			else if (privateRepo &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL)) {

				repositoryPrivateUrl = value;
			}
			else if (privateRepo &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME)) {

				repositoryPrivateUsername = value;
			}
			else {
				Matcher matcher = gradlePropertiesPattern.matcher(key);

				if (!_gitRepoGradlePropertiesKeys.contains(key) &&
					!matcher.matches()) {

					StringBundler sb = new StringBundler(
						(_gitRepoGradlePropertiesKeys.size() + 5) * 3 + 8);

					sb.append("Incorrect key \"");
					sb.append(key);
					sb.append("\" in ");
					sb.append(gradlePropertiesPath);
					sb.append(". Allowed keys are: ");

					List<String> allowedKeys = new ArrayList<>(
						_gitRepoGradlePropertiesKeys);

					allowedKeys.add(_GIT_REPO_GRADLE_PROJECT_GROUP_KEY);
					allowedKeys.add(_GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY);

					if (privateRepo) {
						allowedKeys.add(
							_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD);
						allowedKeys.add(
							_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL);
						allowedKeys.add(
							_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME);
					}

					Collections.sort(allowedKeys);

					for (String allowedKey : allowedKeys) {
						sb.append(CharPool.QUOTE);
						sb.append(allowedKey);
						sb.append("\", ");
					}

					sb.append("and keys matching the pattern \"");
					sb.append(gradlePropertiesPattern.pattern());
					sb.append("\".");

					Assert.fail(sb.toString());
				}
			}

			previousKey = key;
		}

		_testGitRepoProjectGroup(
			StringBundler.concat(
				"Property \"", _GIT_REPO_GRADLE_PROJECT_GROUP_KEY, "\" in ",
				String.valueOf(gradlePropertiesPath)),
			projectGroup);

		Assert.assertEquals(
			StringBundler.concat(
				"Incorrect \"", _GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY,
				"\" in ", String.valueOf(gradlePropertiesPath)),
			_getProjectPathPrefix(dirPath), projectPathPrefix);

		if (privateRepo) {
			_testGradleBuildProperty(
				gradlePropertiesPath,
				_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD,
				repositoryPrivatePassword, "build.repository.private.password");
			_testGradleBuildProperty(
				gradlePropertiesPath, _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL,
				repositoryPrivateUrl, "build.repository.private.url");
			_testGradleBuildProperty(
				gradlePropertiesPath,
				_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME,
				repositoryPrivateUsername, "build.repository.private.username");
		}

		String settingsGradle = ModulesStructureTestUtil.read(
			settingsGradlePath);

		if (Files.notExists(dirPath.resolve("settings-ext.gradle"))) {
			settingsGradleTemplate = StringUtil.removeSubstring(
				settingsGradleTemplate,
				StringPool.NEW_LINE + StringPool.NEW_LINE +
					"apply from: \"settings-ext.gradle\"");
		}

		Assert.assertEquals(
			"Incorrect " + settingsGradlePath, settingsGradleTemplate,
			settingsGradle);
	}

	private void _testGitRepoIgnoreFiles(Path dirPath, String gitIgnoreTemplate)
		throws IOException {

		if (_isEmptyGitRepo(dirPath)) {
			return;
		}

		Path gitIgnorePath = dirPath.resolve(".gitignore");

		String gitIgnore = ModulesStructureTestUtil.read(gitIgnorePath);

		Assert.assertEquals(
			"Incorrect " + gitIgnorePath,
			_getAntPluginsGitIgnore(dirPath, gitIgnoreTemplate), gitIgnore);
	}

	private void _testGitRepoProjectGroup(
		String messagePrefix, String projectGroup) {

		if (Validator.isNull(projectGroup)) {
			return;
		}

		for (String prefix : _GIT_REPO_GRADLE_PROJECT_GROUP_RESERVED_PREFIXES) {
			Assert.assertFalse(
				StringBundler.concat(
					messagePrefix, " cannot start with the reserved prefix \"",
					prefix, "\""),
				StringUtil.startsWith(projectGroup, prefix));
		}

		Matcher matcher = _gitRepoGradleProjectGroupPattern.matcher(
			projectGroup);

		Assert.assertTrue(
			StringBundler.concat(
				messagePrefix, " must match pattern \"",
				_gitRepoGradleProjectGroupPattern.pattern(), "\""),
			matcher.matches());
	}

	private void _testGradleBuildProperty(
		Path gradlePropertiesPath, String key, String value,
		String buildPropertyKey) {

		String expectedValue = _buildProperties.getProperty(buildPropertyKey);

		if (Validator.isNotNull(expectedValue) && Validator.isNotNull(value)) {
			Assert.assertEquals(
				StringBundler.concat(
					"Incorrect \"", key, "\" in ",
					String.valueOf(gradlePropertiesPath)),
				expectedValue, value);
		}
	}

	private void _testGradleFile(Path path) throws IOException {
		String content = ModulesStructureTestUtil.read(path);

		Assert.assertFalse(
			StringBundler.concat(
				"Incorrect repository URL in ", String.valueOf(path),
				", please use ", _REPOSITORY_URL, " instead"),
			content.contains("plugins.gradle.org/m2"));

		Assert.assertFalse(
			"Plugins DSL forbidden in " + path +
				", please use \"apply plugin:\" instead",
			!content.contains("pluginBundle {") &&
			content.contains("plugins {"));

		List<GradleDependency> gradleDependencies =
			ModulesStructureTestUtil.getGradleDependencies(
				content, path, _modulesDirPath);

		Path dirPath = path.getParent();

		Map<String, Boolean> allowedConfigurationsMap = new TreeMap<>();

		boolean hasSrcMainDir = Files.exists(dirPath.resolve("src/main"));
		boolean hasSrcTestDir = Files.exists(dirPath.resolve("src/test"));
		boolean hasSrcTestIntegrationDir = Files.exists(
			dirPath.resolve("src/testIntegration"));

		boolean mainConfigurationsAllowed = false;

		if (hasSrcMainDir ||
			(!hasSrcMainDir && !hasSrcTestDir && !hasSrcTestIntegrationDir) ||
			content.contains("copyLibs {\n\tenabled = true")) {

			mainConfigurationsAllowed = true;
		}

		allowedConfigurationsMap.put("compile", mainConfigurationsAllowed);
		allowedConfigurationsMap.put("compileOnly", mainConfigurationsAllowed);
		allowedConfigurationsMap.put("provided", mainConfigurationsAllowed);

		allowedConfigurationsMap.put("testCompile", hasSrcTestDir);
		allowedConfigurationsMap.put("testRuntime", hasSrcTestDir);

		allowedConfigurationsMap.put(
			"testIntegrationCompile", hasSrcTestIntegrationDir);
		allowedConfigurationsMap.put(
			"testIntegrationRuntime", hasSrcTestIntegrationDir);

		for (GradleDependency gradleDependency : gradleDependencies) {
			Boolean allowed = allowedConfigurationsMap.get(
				gradleDependency.getConfiguration());

			if ((allowed != null) && !allowed.booleanValue()) {
				StringBundler sb = new StringBundler(
					allowedConfigurationsMap.size() * 4 + 4);

				sb.append("Incorrect configuration of dependency {");
				sb.append(gradleDependency);
				sb.append("} in ");
				sb.append(path);
				sb.append(", use one of these instead: ");

				boolean first = true;

				for (Map.Entry<String, Boolean> entry :
						allowedConfigurationsMap.entrySet()) {

					if (!entry.getValue()) {
						continue;
					}

					if (!first) {
						sb.append(StringPool.COMMA_AND_SPACE);
					}

					first = false;

					sb.append(CharPool.QUOTE);
					sb.append(entry.getKey());
					sb.append(CharPool.QUOTE);
				}

				Assert.fail(sb.toString());
			}

			GradleDependency activeGradleDependency =
				_getActiveGradleDependency(
					gradleDependencies, gradleDependency);

			Assert.assertEquals(
				"Redundant dependency detected in " + path,
				activeGradleDependency, gradleDependency);
		}
	}

	private void _testThemeBuildScripts(Path dirPath) throws IOException {
		if (!ModulesStructureTestUtil.contains(
				dirPath.resolve("package.json"), "\"liferay-theme-tasks\":")) {

			return;
		}

		Path gulpfileJsPath = dirPath.resolve("gulpfile.js");

		Assert.assertTrue(
			"Missing " + gulpfileJsPath, Files.exists(gulpfileJsPath));
	}

	private void _testThemeIgnoreFiles(
			Path dirPath, String gitIgnoreTemplate, String npmIgnoreTemplate)
		throws IOException {

		Path resourcesImporterDirPath = dirPath.resolve("resources-importer");

		if (Files.exists(resourcesImporterDirPath)) {
			Path resourcesImporterIgnorePath = resourcesImporterDirPath.resolve(
				_SOURCE_FORMATTER_IGNORE_FILE_NAME);

			Assert.assertTrue(
				"Missing " + resourcesImporterIgnorePath,
				Files.exists(resourcesImporterIgnorePath));
		}

		Path gitIgnorePath = dirPath.resolve(".gitignore");

		String gitIgnore = ModulesStructureTestUtil.read(gitIgnorePath);

		Assert.assertEquals(
			"Incorrect " + gitIgnorePath, gitIgnoreTemplate, gitIgnore);

		Path npmIgnorePath = dirPath.resolve(".npmignore");

		String npmIgnore = ModulesStructureTestUtil.read(npmIgnorePath);

		Assert.assertEquals(
			"Incorrect " + npmIgnorePath, npmIgnoreTemplate, npmIgnore);
	}

	private static final String _APP_BUILD_GRADLE =
		"apply plugin: \"com.liferay.app.defaults.plugin\"";

	private static final String _GIT_REPO_FILE_NAME = ".gitrepo";

	private static final String _GIT_REPO_GRADLE_PROJECT_GROUP_KEY =
		"project.group";

	private static final String[]
		_GIT_REPO_GRADLE_PROJECT_GROUP_RESERVED_PREFIXES =
			{"com.liferay.plugins", "com.liferay.portal"};

	private static final String _GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY =
		"project.path.prefix";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD =
		"systemProp.repository.private.password";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL =
		"systemProp.repository.private.url";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME =
		"systemProp.repository.private.username";

	private static final String _REPOSITORY_URL =
		"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups" +
			"/public";

	private static final String _SOURCE_FORMATTER_IGNORE_FILE_NAME =
		"source_formatter.ignore";

	private static Properties _buildProperties;
	private static final Pattern _gitRepoGradleProjectGroupPattern =
		Pattern.compile("com\\.liferay(?:\\.[a-z]+)+");
	private static final Set<String> _gitRepoGradlePropertiesKeys =
		SetUtil.fromList(
			Arrays.asList(
				"com.liferay.portal.tools.service.builder.version",
				"com.liferay.source.formatter.version"));
	private static final List<String> _gradleConfigurations = Arrays.asList(
		"compileOnly", "provided", "compile", "runtime", "testCompile",
		"testRuntime", "testIntegrationCompile", "testIntegrationRuntime");
	private static Path _modulesDirPath;
	private static final Set<String> _nonemptyMarkerFileNames =
		Collections.singleton(".lfrbuild-lowest-major-version");

}