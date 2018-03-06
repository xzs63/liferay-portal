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

package com.liferay.portal.library;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

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
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Matthew Tambara
 * @author Shuyang Zhou
 * @author Andrea Di Giorgi
 */
public class LibraryReferenceTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_portalPath = Paths.get(System.getProperty("user.dir"));

		_initGitIgnoreJars();
		_initLibJars();
		_initModuleSourceDirs();

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		_initEclipse(documentBuilder);
		_initIntelliJ(documentBuilder);
		_initNetBeans(documentBuilder);
		_initVersionsJars(documentBuilder, _VERSIONS_FILE_NAME, _versionsJars);
		_initVersionsJars(
			documentBuilder, _VERSIONS_EXT_FILE_NAME, _versionsExtJars);
	}

	@Test
	public void testEclipseJarsInLib() {
		testNonexistentJarReferences(_eclipseJars, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testEclipseSourceDirsInModules() {
		testNonexistentModuleSourceDirReferences(
			_eclipseModuleSourceDirs, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testIntelliJLibPreModules() {
		for (Map.Entry<String, List<String>> entry :
				_intelliJModuleSourceModules.entrySet()) {

			String intelliJFileName = entry.getKey();
			List<String> modules = entry.getValue();

			List<String> missingModules = new ArrayList<>();

			for (String moduleSourceDir : _moduleSourceDirs) {
				int y = moduleSourceDir.indexOf(_SRC_JAVA_DIR_NAME);

				int x = moduleSourceDir.lastIndexOf(CharPool.SLASH, y - 2);

				String moduleName = moduleSourceDir.substring(x + 1, y - 1);

				if (!modules.contains(moduleName)) {
					missingModules.add(moduleName);
				}
			}

			Assert.assertTrue(
				intelliJFileName +
					" is missing orderEntry elements for modules " +
						missingModules,
				missingModules.isEmpty());
		}
	}

	@Test
	public void testLibDependencyJarsInGitIgnore() {
		testMissingJarReferences(_gitIgnoreJars, _GIT_IGNORE_FILE_NAME);
	}

	@Test
	public void testLibDependencyJarsInVersionsExt() {
		for (String jar : _libDependencyJars) {
			Assert.assertTrue(
				_VERSIONS_EXT_FILE_NAME + " is missing a reference to " +
					jar,
				_versionsExtJars.contains(jar));
		}
	}

	@Test
	public void testLibJarsInEclipse() {
		testMissingJarReferences(_eclipseJars, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testLibJarsInNetBeans() {
		testMissingJarReferences(_netBeansJars, _NETBEANS_PROPERTIES_FILE_NAME);
	}

	@Test
	public void testLibJarsInVersions() {
		testMissingJarReferences(_versionsJars, _VERSIONS_FILE_NAME);
	}

	@Test
	public void testModulesSourceDirsInEclipse() {
		testMissingModuleSourceDirReferences(
			_eclipseModuleSourceDirs, _ECLIPSE_FILE_NAME);
	}

	@Test
	public void testModulesSourceDirsInNetBeans() {
		testMissingModuleSourceDirReferences(
			_netBeansModuleSourceDirs, _NETBEANS_XML_FILE_NAME);
	}

	@Test
	public void testNetBeansJarsInLib() {
		testNonexistentJarReferences(
			_netBeansJars, _NETBEANS_PROPERTIES_FILE_NAME);
	}

	@Test
	public void testNetBeansSourceDirsInModules() {
		testNonexistentModuleSourceDirReferences(
			_netBeansModuleSourceDirs, _NETBEANS_XML_FILE_NAME);
	}

	@Test
	public void testVersionsExtJarsInLib() {
		for (String jar : _versionsExtJars) {
			if (jar.indexOf(CharPool.EXCLAMATION) != -1) {
				continue;
			}

			Assert.assertTrue(
				_VERSIONS_EXT_FILE_NAME + " has a nonexistent reference to " +
					jar,
				_libDependencyJars.contains(jar));
		}
	}

	@Test
	public void testVersionsJarsInLib() {
		testNonexistentJarReferences(_versionsJars, _VERSIONS_FILE_NAME);
	}

	protected void testMissingJarReferences(Set<String> jars, String fileName) {
		Set<String> libJars = _libJars;

		if (fileName.equals(_GIT_IGNORE_FILE_NAME)) {
			libJars = _libDependencyJars;
		}

		for (String jar : libJars) {
			if (fileName.equals(_VERSIONS_FILE_NAME) &&
				(_excludeJars.contains(jar) ||
				 _libDependencyJars.contains(jar))) {

				continue;
			}

			String referenceJar = jar;

			if (fileName.equals(_GIT_IGNORE_FILE_NAME)) {
				referenceJar = referenceJar.substring(LIB_DIR_NAME.length());
			}

			Assert.assertTrue(
				fileName + " is missing a reference to " + referenceJar,
				jars.contains(jar));
		}
	}

	protected void testMissingModuleSourceDirReferences(
		Set<String> dirs, String fileName) {

		for (String dir : _moduleSourceDirs) {
			Assert.assertTrue(
				fileName + " is missing a reference to " + dir,
				dirs.contains(dir));
		}
	}

	protected void testNonexistentJarReferences(
		Set<String> jars, String fileName) {

		for (String jar : jars) {
			if (fileName.equals(_VERSIONS_FILE_NAME)) {
				Assert.assertFalse(
					fileName + " has a forbidden reference to " + jar,
					_libDependencyJars.contains(jar));
			}

			Assert.assertTrue(
				fileName + " has a nonexistent reference to " + jar,
				_libJars.contains(jar));
		}
	}

	protected void testNonexistentModuleSourceDirReferences(
		Set<String> dirs, String fileName) {

		for (String dir : dirs) {
			Assert.assertTrue(
				fileName + " has a nonexistent reference to " + dir,
				_moduleSourceDirs.contains(dir));
		}
	}

	protected static final String LIB_DIR_NAME = "lib";

	private static void _initEclipse(DocumentBuilder documentBuilder)
		throws Exception {

		Document document = documentBuilder.parse(new File(_ECLIPSE_FILE_NAME));

		NodeList nodelist = document.getElementsByTagName("classpathentry");

		for (int i = 0; i < nodelist.getLength(); i++) {
			Node node = nodelist.item(i);

			NamedNodeMap namedNodeMap = node.getAttributes();

			Node kindNode = namedNodeMap.getNamedItem("kind");
			Node pathNode = namedNodeMap.getNamedItem("path");

			String kind = kindNode.getNodeValue();
			String path = pathNode.getNodeValue();

			if (kind.equals(LIB_DIR_NAME)) {
				_eclipseJars.add(path);
			}
			else if (kind.equals("src")) {
				if (path.startsWith(_MODULES_DIR_NAME + CharPool.SLASH)) {
					_eclipseModuleSourceDirs.add(path);
				}
			}
		}
	}

	private static void _initGitIgnoreJars() throws IOException {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new FileReader(new File(_GIT_IGNORE_FILE_NAME)))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				_gitIgnoreJars.add(LIB_DIR_NAME + line);
			}
		}
	}

	private static void _initIntelliJ(DocumentBuilder documentBuilder)
		throws Exception {

		for (String fileName : _intelliJFileNames) {
			Document document = documentBuilder.parse(new File(fileName));

			NodeList nodeList = document.getElementsByTagName("orderEntry");

			List<String> intelliJModuleSourceModules = new ArrayList<>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element)nodeList.item(i);

				if (Objects.equals("module", element.getAttribute("type"))) {
					intelliJModuleSourceModules.add(
						element.getAttribute("module-name"));
				}
			}

			_intelliJModuleSourceModules.put(
				fileName, intelliJModuleSourceModules);
		}
	}

	private static void _initLibJars() throws IOException {
		Path libDirPath = Paths.get(LIB_DIR_NAME);

		_readLines(_excludeJars, libDirPath.resolve("versions-ignore.txt"));

		Files.walkFileTree(
			libDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path path = dirPath.resolve("dependencies.properties");

					if (Files.notExists(path)) {
						return FileVisitResult.CONTINUE;
					}

					Properties properties;

					try (InputStream inputStream = Files.newInputStream(path)) {
						properties = PropertiesUtil.load(
							inputStream, StringPool.UTF8);
					}

					String dirPathString = dirPath.toString();

					if (File.separatorChar != CharPool.SLASH) {
						dirPathString = dirPathString.replace(
							File.separatorChar, CharPool.SLASH);
					}

					dirPathString += CharPool.SLASH;

					for (String fileTitle : properties.stringPropertyNames()) {
						String jar = dirPathString + fileTitle + ".jar";

						_libDependencyJars.add(jar);
						_libJars.add(jar);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					String pathString = path.toString();

					if (pathString.endsWith(".jar")) {
						if (File.separatorChar != CharPool.SLASH) {
							pathString = pathString.replace(
								File.separatorChar, CharPool.SLASH);
						}

						_libJars.add(pathString);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _initModuleSourceDirs() throws IOException {
		Files.walkFileTree(
			_portalPath.resolve(_MODULES_DIR_NAME),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (!dirName.endsWith("-compat") &&
						Files.exists(dirPath.resolve(".lfrbuild-portal-pre"))) {

						Path sourceDirPath = dirPath.resolve(
							_SRC_JAVA_DIR_NAME);

						String sourceDir = String.valueOf(
							_portalPath.relativize(sourceDirPath));

						if (File.separatorChar != CharPool.SLASH) {
							sourceDir = StringUtil.replace(
								sourceDir, File.separatorChar, CharPool.SLASH);
						}

						_moduleSourceDirs.add(sourceDir);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _initNetBeans(DocumentBuilder documentBuilder)
		throws Exception {

		Document document = documentBuilder.parse(
			new File(_NETBEANS_XML_FILE_NAME));

		Properties properties = new Properties();

		try (InputStream in = Files.newInputStream(
				Paths.get(_NETBEANS_PROPERTIES_FILE_NAME))) {

			properties.load(in);
		}

		Collections.addAll(
			_netBeansJars,
			StringUtil.split(properties.getProperty("javac.classpath"), ':'));

		NodeList nodelist = document.getElementsByTagName("source-folder");

		for (int i = 0; i < nodelist.getLength(); i++) {
			Element element = (Element)nodelist.item(i);

			NodeList locationNodeList = element.getElementsByTagName(
				"location");

			Node locationNode = locationNodeList.item(0);

			String location = locationNode.getTextContent();

			if (location.startsWith(_MODULES_DIR_NAME + CharPool.SLASH) &&
				location.endsWith(CharPool.SLASH + _SRC_JAVA_DIR_NAME)) {

				_netBeansModuleSourceDirs.add(location);
			}
		}
	}

	private static void _initVersionsJars(
			DocumentBuilder documentBuilder, String fileName, Set<String> jars)
		throws Exception {

		Document document = documentBuilder.parse(new File(fileName));

		NodeList nodelist = document.getElementsByTagName("file-name");

		for (int i = 0; i < nodelist.getLength(); i++) {
			Node node = nodelist.item(i);

			jars.add(node.getTextContent());
		}
	}

	private static void _readLines(Set<String> lines, Path path)
		throws IOException {

		if (Files.notExists(path)) {
			return;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(path.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (Validator.isNotNull(line)) {
					lines.add(line);
				}
			}
		}
	}

	private static final String _ECLIPSE_FILE_NAME = ".classpath";

	private static final String _GIT_IGNORE_FILE_NAME =
		LIB_DIR_NAME + "/.gitignore";

	private static final String _MODULES_DIR_NAME = "modules";

	private static final String _NETBEANS_PROPERTIES_FILE_NAME =
		"nbproject/project.properties";

	private static final String _NETBEANS_XML_FILE_NAME =
		"nbproject/project.xml";

	private static final String _SRC_JAVA_DIR_NAME = "src/main/java";

	private static final String _VERSIONS_EXT_FILE_NAME =
		LIB_DIR_NAME + "/versions-ext.xml";

	private static final String _VERSIONS_FILE_NAME =
		LIB_DIR_NAME + "/versions.xml";

	private static final Set<String> _eclipseJars = new HashSet<>();
	private static final Set<String> _eclipseModuleSourceDirs = new HashSet<>();
	private static final Set<String> _excludeJars = new HashSet<>();
	private static final Set<String> _gitIgnoreJars = new HashSet<>();
	private static final List<String> _intelliJFileNames = Arrays.asList(
		"portal-impl/portal-impl.iml", "portal-kernel/portal-kernel.iml",
		"portal-test-integration/portal-test-integration.iml",
		"portal-test/portal-test.iml", "portal-web/portal-web.iml",
		"util-bridges/util-bridges.iml", "util-java/util-java.iml",
		"util-slf4j/util-slf4j.iml", "util-taglib/util-taglib.iml");
	private static final Map<String, List<String>>
		_intelliJModuleSourceModules = new HashMap<>();
	private static final Set<String> _libDependencyJars = new HashSet<>();
	private static final Set<String> _libJars = new HashSet<>();
	private static final Set<String> _moduleSourceDirs = new HashSet<>();
	private static final Set<String> _netBeansJars = new HashSet<>();
	private static final Set<String> _netBeansModuleSourceDirs =
		new HashSet<>();
	private static Path _portalPath;
	private static final Set<String> _versionsExtJars = new HashSet<>();
	private static final Set<String> _versionsJars = new HashSet<>();

}