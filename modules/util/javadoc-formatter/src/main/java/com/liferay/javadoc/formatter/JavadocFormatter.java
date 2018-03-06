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

package com.liferay.javadoc.formatter;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.xml.Dom4jUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.JavaImportsFormatter;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.portal.xml.SAXReaderFactory;
import com.liferay.util.xml.Dom4jDocUtil;
import com.liferay.util.xml.XMLSafeReader;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaExecutable;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMember;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModel;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.DirectoryScanner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Brian Wing Shun Chan
 * @author Connor McKay
 * @author James Hinkey
 * @author Hugo Huijser
 */
public class JavadocFormatter {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		try {
			new JavadocFormatter(arguments);
		}
		catch (Exception e) {
			ArgumentsUtil.processMainException(arguments, e);
		}
	}

	public JavadocFormatter(Map<String, String> arguments) throws Exception {
		String author = ArgumentsUtil.getString(
			arguments, "javadoc.author", JavadocFormatterArgs.AUTHOR);

		_author = author;

		_generateXml = GetterUtil.getBoolean(
			arguments.get("javadoc.generate.xml"));

		String init = arguments.get("javadoc.init");

		_initializeMissingJavadocs = GetterUtil.getBoolean(init);

		String inputDirName = ArgumentsUtil.getString(
			arguments, "javadoc.input.dir", "./");

		if (!inputDirName.endsWith("/")) {
			inputDirName += "/";
		}

		_inputDirName = inputDirName;

		System.out.println("Input directory is " + _inputDirName);

		String[] limits = StringUtil.split(arguments.get("javadoc.limit"), ",");

		if (limits.length == 0) {
			limits = new String[] {StringPool.BLANK};
		}

		File languagePropertiesFile = new File(
			"src/content/Language.properties");

		if (!languagePropertiesFile.exists()) {
			languagePropertiesFile = new File(
				"src/main/resources/content/Language.properties");
		}

		if (languagePropertiesFile.exists()) {
			_languageProperties = new Properties();
			_languagePropertiesFile = languagePropertiesFile;

			_languageProperties.load(
				new FileInputStream(_languagePropertiesFile.getAbsolutePath()));
		}
		else {
			_languageProperties = null;
			_languagePropertiesFile = null;
		}

		String outputFilePrefix = ArgumentsUtil.getString(
			arguments, "javadoc.output.file.prefix",
			JavadocFormatterArgs.OUTPUT_FILE_PREFIX);

		_outputFilePrefix = outputFilePrefix;

		_updateJavadocs = GetterUtil.getBoolean(
			arguments.get("javadoc.update"));

		DirectoryScanner directoryScanner = new DirectoryScanner();

		directoryScanner.setBasedir(_inputDirName);
		directoryScanner.setExcludes(
			new String[] {
				"**\\build\\**", "**\\classes\\**", "**\\portal-client\\**"
			});

		for (String limit : limits) {
			List<String> includes = new ArrayList<>();

			if (Validator.isNotNull(limit) && !limit.startsWith("$")) {
				System.out.println("Limit on " + limit);

				String[] limitArray = StringUtil.split(limit, '/');

				for (String curLimit : limitArray) {
					includes.add(
						"**\\" + StringUtil.replace(curLimit, '.', '\\') +
							"\\**\\*.java");
					includes.add("**\\" + curLimit + ".java");
				}
			}
			else {
				includes.add("**\\*.java");
			}

			directoryScanner.setIncludes(
				includes.toArray(new String[includes.size()]));

			directoryScanner.scan();

			String[] fileNames = StringPool.EMPTY_ARRAY;

			fileNames = directoryScanner.getIncludedFiles();

			if ((fileNames.length == 0) && Validator.isNotNull(limit) &&
				!limit.startsWith("$")) {

				StringBundler sb = new StringBundler("Limit file not found: ");

				sb.append(limit);

				if (limit.contains(".")) {
					sb.append(" Specify limit filename without package path ");
					sb.append("or file type suffix.");
				}

				System.out.println(sb.toString());
			}

			for (String fileName : fileNames) {
				fileName = StringUtil.replace(fileName, '\\', '/');

				try {
					_format(fileName);
				}
				catch (Exception e) {
					throw new RuntimeException(
						"Unable to format file " + fileName, e);
				}
			}
		}

		if (_generateXml) {
			for (Map.Entry<String, Tuple> entry :
					_javadocxXmlTuples.entrySet()) {

				Tuple tuple = entry.getValue();

				File javadocsXmlFile = (File)tuple.getObject(1);
				String oldJavadocsXmlContent = (String)tuple.getObject(2);
				Document javadocsXmlDocument = (Document)tuple.getObject(3);

				Element javadocsXmlRootElement =
					javadocsXmlDocument.getRootElement();

				_sortElementsByChildElement(
					javadocsXmlRootElement, "javadoc", "type");

				String newJavadocsXmlContent = _formattedString(
					javadocsXmlDocument);

				if (!oldJavadocsXmlContent.equals(newJavadocsXmlContent)) {
					_write(javadocsXmlFile, newJavadocsXmlContent);

					_modifiedFileNames.add(javadocsXmlFile.getAbsolutePath());
				}

				_detachUnnecessaryTypes(javadocsXmlRootElement);

				File javadocsRuntimeXmlFile = new File(
					StringUtil.replaceLast(
						javadocsXmlFile.toString(), "-all.xml", "-rt.xml"));

				String oldJavadocsRuntimeXmlContent = StringPool.BLANK;

				if (javadocsRuntimeXmlFile.exists()) {
					oldJavadocsRuntimeXmlContent = _read(
						javadocsRuntimeXmlFile);
				}

				String newJavadocsRuntimeXmlContent = _compactString(
					javadocsXmlDocument);

				if (!oldJavadocsRuntimeXmlContent.equals(
						newJavadocsRuntimeXmlContent)) {

					_write(
						javadocsRuntimeXmlFile, newJavadocsRuntimeXmlContent);

					_modifiedFileNames.add(
						javadocsRuntimeXmlFile.getAbsolutePath());
				}
			}
		}
	}

	public Set<String> getModifiedFileNames() {
		return _modifiedFileNames;
	}

	private void _addClassCommentElement(
		Element rootElement, JavaClass javaClass) {

		String comment = _getCDATA(javaClass);

		if (comment.startsWith("Copyright (c)")) {
			comment = StringPool.BLANK;
		}

		if (Validator.isNull(comment)) {
			return;
		}

		Element commentElement = rootElement.addElement("comment");

		commentElement.addCDATA(comment);
	}

	private void _addClassDocletElements(
			Element parentElement, JavaClass javaClass, boolean nestedClass)
		throws Exception {

		Dom4jDocUtil.add(parentElement, "name", javaClass.getName());
		Dom4jDocUtil.add(
			parentElement, "type", javaClass.getFullyQualifiedName());

		_addClassCommentElement(parentElement, javaClass);

		if (!nestedClass) {
			_addDocletElements(parentElement, javaClass, "author");
		}

		_addParamElements(
			parentElement, javaClass, javaClass.getTagsByName("param"));
		_addDocletElements(parentElement, javaClass, "version");
		_addDocletElements(parentElement, javaClass, "see");
		_addDocletElements(parentElement, javaClass, "since");
		_addDocletElements(parentElement, javaClass, "serial");
		_addDocletElements(parentElement, javaClass, "deprecated");
		_addDocletElements(parentElement, javaClass, "review");
	}

	private void _addClassElement(
			Element parentElement, JavaClass javaClass, boolean nestedClass)
		throws Exception {

		Element classElement = null;

		if (nestedClass) {
			classElement = parentElement.addElement("class");
		}
		else {
			classElement = parentElement;
		}

		_addClassDocletElements(classElement, javaClass, nestedClass);

		List<JavaConstructor> javaConstructors = javaClass.getConstructors();

		for (JavaConstructor javaConstructor : javaConstructors) {
			_addConstructorElement(classElement, javaConstructor);
		}

		List<JavaMethod> javaMethods = javaClass.getMethods();

		for (JavaMethod javaMethod : javaMethods) {
			_addMethodElement(classElement, javaMethod);
		}

		List<JavaField> javaFields = javaClass.getFields();

		for (JavaField javaField : javaFields) {
			_addFieldElement(classElement, javaField);
		}

		List<JavaClass> nestedJavaClasses = javaClass.getNestedClasses();

		for (JavaClass nestedJavaClass : nestedJavaClasses) {
			_addClassElement(classElement, nestedJavaClass, true);
		}
	}

	private Map<Integer, String> _addComments(
			Map<Integer, String> commentsMap, Element classElement,
			JavaClass javaClass, String content, String[] lines)
		throws Exception {

		int lineNumber = _getJavaModelLineNumber(javaClass, content);

		String indent = _getIndent(lines, lineNumber);

		String javaClassComment = _getJavaClassComment(
			classElement, javaClass, indent);

		javaClassComment = _addDeprecatedTag(
			javaClassComment, javaClass, indent);

		commentsMap.put(
			_getJavaModelLineNumber(javaClass, content), javaClassComment);

		Map<String, Element> constructorElementsMap = new HashMap<>();

		List<Element> constructorElements = classElement.elements(
			"constructor");

		for (Element constructorElement : constructorElements) {
			String constructorKey = _getExecutableKey(constructorElement);

			constructorElementsMap.put(constructorKey, constructorElement);
		}

		List<JavaConstructor> javaConstructors = javaClass.getConstructors();

		for (JavaConstructor javaConstructor : javaConstructors) {
			lineNumber = _getJavaModelLineNumber(javaConstructor, content);

			if (commentsMap.containsKey(lineNumber)) {
				continue;
			}

			indent = _getIndent(lines, lineNumber);

			String javaConstructorComment = _getJavaExecutableComment(
				constructorElementsMap, javaConstructor, indent);

			javaConstructorComment = _addDeprecatedTag(
				javaConstructorComment, javaConstructor, indent);

			commentsMap.put(lineNumber, javaConstructorComment);
		}

		Map<String, Element> methodElementsMap = new HashMap<>();

		List<Element> methodElements = classElement.elements("method");

		for (Element methodElement : methodElements) {
			String methodKey = _getExecutableKey(methodElement);

			methodElementsMap.put(methodKey, methodElement);
		}

		List<JavaMethod> javaMethods = javaClass.getMethods();

		for (JavaMethod javaMethod : javaMethods) {
			lineNumber = _getJavaModelLineNumber(javaMethod, content);

			if (commentsMap.containsKey(lineNumber)) {
				continue;
			}

			indent = _getIndent(lines, lineNumber);

			String javaMethodComment = _getJavaExecutableComment(
				methodElementsMap, javaMethod, indent);

			javaMethodComment = _addDeprecatedTag(
				javaMethodComment, javaMethod, indent);

			commentsMap.put(lineNumber, javaMethodComment);
		}

		Map<String, Element> fieldElementsMap = new HashMap<>();

		List<Element> fieldElements = classElement.elements("field");

		for (Element fieldElement : fieldElements) {
			String fieldKey = _getFieldKey(fieldElement);

			fieldElementsMap.put(fieldKey, fieldElement);
		}

		List<JavaField> javaFields = javaClass.getFields();

		for (JavaField javaField : javaFields) {
			lineNumber = _getJavaModelLineNumber(javaField, content);

			if (commentsMap.containsKey(lineNumber)) {
				continue;
			}

			indent = _getIndent(lines, lineNumber);

			String javaFieldComment = _getJavaFieldComment(
				fieldElementsMap, javaField, indent);

			javaFieldComment = _addDeprecatedTag(
				javaFieldComment, javaField, indent);

			commentsMap.put(lineNumber, javaFieldComment);
		}

		Map<String, Element> nestedClassElementsMap = new HashMap<>();

		List<Element> nestedClassElements = classElement.elements("class");

		for (Element nestedClassElement : nestedClassElements) {
			String nestedClassKey = _getClassKey(nestedClassElement);

			nestedClassElementsMap.put(nestedClassKey, nestedClassElement);
		}

		List<JavaClass> nestedClasses = javaClass.getNestedClasses();

		for (JavaClass nestedClass : nestedClasses) {
			String nestedClassKey = _getClassKey(nestedClass);

			Element nestedClassElement = nestedClassElementsMap.get(
				nestedClassKey);

			commentsMap = _addComments(
				commentsMap, nestedClassElement, nestedClass, content, lines);
		}

		return commentsMap;
	}

	private void _addConstructorElement(
			Element parentElement, JavaConstructor javaConstructor)
		throws Exception {

		Element constructorElement = parentElement.addElement("constructor");

		Dom4jDocUtil.add(constructorElement, "name", javaConstructor.getName());

		String comment = _getCDATA(javaConstructor);

		if (Validator.isNotNull(comment)) {
			Element commentElement = constructorElement.addElement("comment");

			commentElement.addCDATA(_getCDATA(javaConstructor));
		}

		_addDocletElements(constructorElement, javaConstructor, "version");
		_addParamElements(
			constructorElement, javaConstructor,
			javaConstructor.getTagsByName("param"));
		_addThrowsElements(
			constructorElement, javaConstructor,
			javaConstructor.getTagsByName("throws"));
		_addDocletElements(constructorElement, javaConstructor, "see");
		_addDocletElements(constructorElement, javaConstructor, "since");
		_addDocletElements(constructorElement, javaConstructor, "deprecated");
		_addDocletElements(constructorElement, javaConstructor, "review");
	}

	private String _addDeprecatedTag(
			String comment, JavaAnnotatedElement javaAnnotatedElement,
			String indent)
		throws Exception {

		if (comment == null) {
			return null;
		}

		comment = ToolsUtil.stripFullyQualifiedClassNames(
			comment, _imports, _packagePath);

		if (!comment.contains("* @deprecated ") ||
			_hasAnnotation(javaAnnotatedElement, "Deprecated")) {

			return comment;
		}

		return comment + indent + "@Deprecated\n";
	}

	private void _addDocletElements(
			Element parentElement, JavaAnnotatedElement javaAnnotatedElement,
			String name)
		throws Exception {

		List<DocletTag> docletTags = javaAnnotatedElement.getTagsByName(name);

		for (DocletTag docletTag : docletTags) {
			String value = docletTag.getValue();

			value = ToolsUtil.stripFullyQualifiedClassNames(
				value, _imports, _packagePath);

			value = _trimMultilineText(value);

			value = StringUtil.replace(value, " </", "</");

			Element element = parentElement.addElement(name);

			element.addCDATA(value);
		}

		if (docletTags.isEmpty() && name.equals("author")) {
			Element element = parentElement.addElement(name);

			element.addCDATA(_author);
		}
	}

	private String _addDocletTags(
		Element parentElement, String[] tagNames, String indent,
		boolean publicAccess) {

		List<String> allTagNames = new ArrayList<>();
		List<String> customTagNames = new ArrayList<>();
		List<String> requiredTagNames = new ArrayList<>();

		for (String tagName : tagNames) {
			List<Element> elements = parentElement.elements(tagName);

			for (Element element : elements) {
				Element commentElement = element.element("comment");

				String comment = null;

				// Get comment by comment element's text or the element's text

				if (commentElement != null) {
					comment = commentElement.getText();
				}
				else {
					comment = element.getText();
				}

				if (tagName.equals("param") || tagName.equals("return") ||
					tagName.equals("throws")) {

					if (Validator.isNotNull(comment)) {
						requiredTagNames.add(tagName);
					}
					else if (tagName.equals("param")) {
						if (GetterUtil.getBoolean(
								element.elementText("required"))) {

							requiredTagNames.add(tagName);
						}
					}
					else if (tagName.equals("throws")) {
						if (GetterUtil.getBoolean(
								element.elementText("required"))) {

							requiredTagNames.add(tagName);
						}
					}
				}
				else {
					customTagNames.add(tagName);
				}

				allTagNames.add(tagName);
			}
		}

		int maxTagNameLength = 0;

		List<String> maxTagNameLengthTags = new ArrayList<>();

		if (_initializeMissingJavadocs) {
			maxTagNameLengthTags.addAll(allTagNames);
		}
		else if (_updateJavadocs) {
			if (!requiredTagNames.isEmpty()) {
				maxTagNameLengthTags.addAll(allTagNames);
			}
			else {
				maxTagNameLengthTags.addAll(customTagNames);
				maxTagNameLengthTags.addAll(requiredTagNames);
			}
		}
		else {
			maxTagNameLengthTags.addAll(customTagNames);
			maxTagNameLengthTags.addAll(requiredTagNames);
		}

		for (String name : maxTagNameLengthTags) {
			if (name.length() > maxTagNameLength) {
				maxTagNameLength = name.length();
			}
		}

		// There should be an @ sign before the tag and a space after it

		maxTagNameLength += 2;

		String tagNameIndent = _getSpacesIndent(maxTagNameLength);

		StringBundler sb = new StringBundler();

		for (String tagName : tagNames) {
			List<Element> elements = parentElement.elements(tagName);

			for (Element element : elements) {
				Element commentElement = element.element("comment");

				String comment = null;

				if (commentElement != null) {
					comment = commentElement.getText();
				}
				else {
					comment = element.getText();
				}

				String elementName = element.elementText("name");

				if (Validator.isNotNull(comment)) {
					comment = _assembleTagComment(
						tagName, elementName, comment, indent, tagNameIndent);

					sb.append(comment);
				}
				else {
					if (_initializeMissingJavadocs && publicAccess) {

						// Write out all tags

						comment = _assembleTagComment(
							tagName, elementName, comment, indent,
							tagNameIndent);

						sb.append(comment);
					}
					else if (_updateJavadocs && publicAccess) {
						if (!tagName.equals("param") &&
							!tagName.equals("return") &&
							!tagName.equals("throws")) {

							// Write out custom tag

							comment = _assembleTagComment(
								tagName, elementName, comment, indent,
								tagNameIndent);

							sb.append(comment);
						}
						else if (!requiredTagNames.isEmpty()) {

							// Write out all tags

							comment = _assembleTagComment(
								tagName, elementName, comment, indent,
								tagNameIndent);

							sb.append(comment);
						}
						else {

							// Skip empty common tag

						}
					}
					else {
						if (!tagName.equals("param") &&
							!tagName.equals("return") &&
							!tagName.equals("throws")) {

							// Write out custom tag

							comment = _assembleTagComment(
								tagName, elementName, comment, indent,
								tagNameIndent);

							sb.append(comment);
						}
						else if (tagName.equals("param") ||
								 tagName.equals("return") ||
								 tagName.equals("throws")) {

							if (GetterUtil.getBoolean(
									element.elementText("required"))) {

								elementName = element.elementText("name");

								comment = _assembleTagComment(
									tagName, elementName, comment, indent,
									tagNameIndent);

								sb.append(comment);
							}
						}
						else {

							// Skip empty common tag

						}
					}
				}
			}
		}

		return sb.toString();
	}

	private void _addFieldElement(Element parentElement, JavaField javaField)
		throws Exception {

		Element fieldElement = parentElement.addElement("field");

		Dom4jDocUtil.add(fieldElement, "name", javaField.getName());

		String comment = _getCDATA(javaField);

		if (Validator.isNotNull(comment)) {
			Element commentElement = fieldElement.addElement("comment");

			commentElement.addCDATA(comment);
		}

		_addDocletElements(fieldElement, javaField, "version");
		_addDocletElements(fieldElement, javaField, "see");
		_addDocletElements(fieldElement, javaField, "since");
		_addDocletElements(fieldElement, javaField, "deprecated");
		_addDocletElements(fieldElement, javaField, "review");
	}

	private void _addMethodElement(Element parentElement, JavaMethod javaMethod)
		throws Exception {

		Element methodElement = parentElement.addElement("method");

		Dom4jDocUtil.add(methodElement, "name", javaMethod.getName());

		String comment = _getCDATA(javaMethod);

		if (Validator.isNotNull(comment)) {
			Element commentElement = methodElement.addElement("comment");

			commentElement.addCDATA(_getCDATA(javaMethod));
		}

		_addDocletElements(methodElement, javaMethod, "version");
		_addParamElements(
			methodElement, javaMethod, javaMethod.getTagsByName("param"));
		_addReturnElement(methodElement, javaMethod);
		_addThrowsElements(
			methodElement, javaMethod, javaMethod.getTagsByName("throws"));
		_addDocletElements(methodElement, javaMethod, "see");
		_addDocletElements(methodElement, javaMethod, "since");
		_addDocletElements(methodElement, javaMethod, "deprecated");
		_addDocletElements(methodElement, javaMethod, "review");
	}

	private void _addParamElement(
			Element executableElement, String parameterName,
			String parameterValue, List<DocletTag> paramDocletTags)
		throws Exception {

		String value = null;

		for (DocletTag paramDocletTag : paramDocletTags) {
			String curValue = paramDocletTag.getValue();

			if (curValue.equals(parameterName) ||
				curValue.startsWith(parameterName + " ")) {

				value = curValue;

				break;
			}
		}

		Element paramElement = executableElement.addElement("param");

		Dom4jDocUtil.add(paramElement, "name", parameterName);

		if (parameterValue != null) {
			Dom4jDocUtil.add(paramElement, "type", parameterValue);
		}

		if (value != null) {
			value = value.substring(parameterName.length());

			Dom4jDocUtil.add(paramElement, "required", true);
		}

		value = ToolsUtil.stripFullyQualifiedClassNames(
			value, _imports, _packagePath);

		value = _trimMultilineText(value);

		Element commentElement = paramElement.addElement("comment");

		commentElement.addCDATA(value);
	}

	private void _addParamElements(
			Element classElement, JavaClass javaClass,
			List<DocletTag> paramDocletTags)
		throws Exception {

		for (JavaTypeVariable<?> typeParameter :
				javaClass.getTypeParameters()) {

			_addParamElement(
				classElement, "<" + typeParameter.getName() + ">", null,
				paramDocletTags);
		}
	}

	private void _addParamElements(
			Element executableElement, JavaExecutable javaExecutable,
			List<DocletTag> paramDocletTags)
		throws Exception {

		List<JavaParameter> javaParameters = javaExecutable.getParameters();

		for (JavaParameter javaParameter : javaParameters) {
			_addParamElement(
				executableElement, javaParameter.getName(),
				_getTypeValue(javaParameter), paramDocletTags);
		}
	}

	private void _addReturnElement(Element methodElement, JavaMethod javaMethod)
		throws Exception {

		JavaType returnType = javaMethod.getReturnType();

		if (returnType == null) {
			return;
		}

		String returnTypeValue = returnType.getValue();

		if (returnTypeValue.equals("void")) {
			return;
		}

		Element returnElement = methodElement.addElement("return");

		List<DocletTag> returnDocletTags = javaMethod.getTagsByName("return");

		String comment = StringPool.BLANK;

		if (!returnDocletTags.isEmpty()) {
			DocletTag returnDocletTag = returnDocletTags.get(0);

			comment = GetterUtil.getString(returnDocletTag.getValue());

			Dom4jDocUtil.add(returnElement, "required", true);
		}

		comment = ToolsUtil.stripFullyQualifiedClassNames(
			comment, _imports, _packagePath);

		comment = _trimMultilineText(comment);

		Element commentElement = returnElement.addElement("comment");

		commentElement.addCDATA(comment);
	}

	private void _addThrowsElement(
			Element executableElement, JavaClass exceptionJavaClass,
			List<DocletTag> throwsDocletTags)
		throws Exception {

		String name = exceptionJavaClass.getName();

		int pos = name.lastIndexOf(StringPool.PERIOD);

		if (pos != -1) {
			name = name.substring(pos + 1);
		}

		String value = null;

		for (DocletTag throwsDocletTag : throwsDocletTags) {
			String curValue = throwsDocletTag.getValue();

			if (!curValue.startsWith(name)) {
				continue;
			}
			else {
				value = curValue;

				break;
			}
		}

		Element throwsElement = executableElement.addElement("throws");

		Dom4jDocUtil.add(throwsElement, "name", name);
		Dom4jDocUtil.add(throwsElement, "type", exceptionJavaClass.getValue());

		if (value != null) {
			value = value.substring(name.length());

			Dom4jDocUtil.add(throwsElement, "required", true);
		}

		value = ToolsUtil.stripFullyQualifiedClassNames(
			value, _imports, _packagePath);

		value = _trimMultilineText(value);

		Element commentElement = throwsElement.addElement("comment");

		commentElement.addCDATA(_getCDATA(value));
	}

	private void _addThrowsElements(
			Element executableElement, JavaExecutable javaExecutable,
			List<DocletTag> throwsDocletTags)
		throws Exception {

		List<JavaClass> exceptionJavaClasses = javaExecutable.getExceptions();

		for (JavaClass exceptionJavaClass : exceptionJavaClasses) {
			_addThrowsElement(
				executableElement, exceptionJavaClass, throwsDocletTags);
		}
	}

	private String _assembleTagComment(
		String tagName, String elementName, String comment, String indent,
		String tagNameIndent) {

		String indentAndTagName = indent + StringPool.AT + tagName;

		if (Validator.isNotNull(elementName)) {
			if (Validator.isNotNull(comment)) {
				comment = elementName + StringPool.SPACE + comment;
			}
			else {
				comment = elementName;
			}

			// <name indent> elementName [comment]

			comment = _wrapText(comment, indent + tagNameIndent);

			// * @name <name indent> elementName [comment]

			comment =
				indentAndTagName + comment.substring(indentAndTagName.length());
		}
		else {
			if (Validator.isNotNull(comment)) {

				// <name indent> comment

				comment = _wrapText(comment, indent + tagNameIndent);

				// * @name <name indent> comment

				comment =
					indentAndTagName +
						comment.substring(indentAndTagName.length());
			}
			else {

				// * @name

				comment = indentAndTagName + "\n";
			}
		}

		return comment;
	}

	private String _compactString(Node node) throws IOException {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		OutputFormat outputFormat = OutputFormat.createCompactFormat();

		XMLWriter xmlWriter = new XMLWriter(
			unsyncByteArrayOutputStream, outputFormat);

		xmlWriter.write(node);

		return unsyncByteArrayOutputStream.toString(StringPool.UTF8);
	}

	private void _detachUnnecessaryTypes(Element rootElement) {
		List<Element> elements = rootElement.elements();

		for (Element element : elements) {
			String type = element.elementText("type");

			if (!type.contains(".service.") || !type.endsWith("ServiceImpl")) {
				element.detach();
			}
		}
	}

	private void _format(String fileName) throws Exception {
		File file = new File(_inputDirName, fileName);

		String originalContent = _read(file);

		String absolutePath = _getAbsolutePath(fileName);

		if (absolutePath.contains("modules/third-party") ||
			fileName.endsWith("Application.java") ||
			fileName.endsWith("JavadocFormatter.java") ||
			fileName.endsWith("Mojo.java") ||
			fileName.endsWith("SourceFormatter.java") ||
			fileName.endsWith("WebProxyPortlet.java") ||
			_hasGeneratedTag(originalContent)) {

			return;
		}

		_imports = JavaImportsFormatter.getImports(originalContent);
		_packagePath = ToolsUtil.getPackagePath(fileName);

		JavaClass javaClass = null;

		try {
			javaClass = _getJavaClass(
				fileName, new UnsyncStringReader(originalContent));
		}
		catch (Exception e) {
			if (!fileName.contains("__")) {
				System.out.println(
					"Qdox parsing error while formatting file " + fileName);
			}

			return;
		}

		String javadocLessContent = _removeJavadocFromJava(
			javaClass, originalContent);

		Document document = _getJavadocDocument(javaClass);

		if (_generateXml) {
			_updateJavadocsXmlFile(fileName, javaClass, document);
		}

		String newContent = _getUpdateJavaFromDocument(
			fileName, javadocLessContent, document);

		if (!originalContent.equals(newContent)) {
			_write(file, newContent);

			_modifiedFileNames.add(file.getAbsolutePath());

			System.out.println("Writing " + file);
		}
	}

	private String _formatCDATA(String cdata) {
		cdata = cdata.replaceAll(
			"(?s)\\s*<(p|[ou]l)>\\s*(.*?)\\s*</\\1>\\s*",
			"\n\n<$1>\n$2\n</$1>\n\n");
		cdata = cdata.replaceAll(
			"(?s)\\s*<li>\\s*(.*?)\\s*</li>\\s*", "\n<li>\n$1\n</li>\n");
		cdata = StringUtil.replace(cdata, "</li>\n\n<li>", "</li>\n<li>");
		cdata = cdata.replaceAll("\n\\s+\n", "\n\n");
		cdata = cdata.replaceAll(" +", " ");

		// Trim whitespace inside paragraph tags or in the first paragraph

		Matcher matcher = _paragraphTagPattern.matcher(cdata);

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String trimmed = _trimMultilineText(matcher.group());

			// Escape dollar signs

			trimmed = StringUtil.replace(trimmed, '$', "\\$");

			matcher.appendReplacement(sb, trimmed);
		}

		matcher.appendTail(sb);

		cdata = sb.toString();

		return cdata.trim();
	}

	private String _formatInlines(String text) {

		// Capitalize ID

		text = text.replaceAll("[?@param id](?i)\\bid(s)?\\b", " ID$1");

		// Wrap special constants in code tags

		text = text.replaceAll(
			"(?i)(?<!<code>|\\{@code |\\w)(null|false|true)(?!\\w)",
			"<code>$1</code>");

		return text;
	}

	private String _formattedString(Node node) throws IOException {
		return Dom4jUtil.toString(node);
	}

	private String _getAbsolutePath(String fileName) {
		Path filePath = Paths.get(fileName);

		filePath = filePath.toAbsolutePath();

		filePath = filePath.normalize();

		return StringUtil.replace(
			filePath.toString(), CharPool.BACK_SLASH, CharPool.SLASH);
	}

	private int _getAdjustedLineNumber(int lineNumber, JavaModel javaModel) {
		JavaAnnotatedElement javaAnnotatedElement =
			(JavaAnnotatedElement)javaModel;

		List<JavaAnnotation> annotations =
			javaAnnotatedElement.getAnnotations();

		if (annotations.isEmpty()) {
			return lineNumber;
		}

		for (JavaAnnotation annotation : annotations) {
			int annotationLineNumber = annotation.getLineNumber();

			Map<String, AnnotationValue> propertyMap =
				annotation.getPropertyMap();

			if (propertyMap.isEmpty()) {
				annotationLineNumber--;
			}

			if (annotationLineNumber < lineNumber) {
				lineNumber = annotationLineNumber;
			}
		}

		return lineNumber;
	}

	private String _getCDATA(JavaAnnotatedElement javaAnnotatedElement) {
		return _getCDATA(javaAnnotatedElement.getComment());
	}

	private String _getCDATA(String cdata) {
		StringBundler sb = new StringBundler();

		if ((cdata == null) || cdata.isEmpty()) {
			return StringPool.BLANK;
		}

		int cdataBeginIndex = 0;

		while (!cdata.isEmpty()) {
			int preTagIndex = cdata.indexOf("<pre>");
			int tableTagIndex = cdata.indexOf("<table>");

			boolean hasPreTag = false;

			if (preTagIndex != -1) {
				hasPreTag = true;
			}

			boolean hasTableTag = false;

			if (tableTagIndex != -1) {
				hasTableTag = true;
			}

			if (!hasPreTag && !hasTableTag) {
				sb.append(_formatCDATA(cdata));

				break;
			}

			boolean startsWithPreTag = false;

			if (preTagIndex == 0) {
				startsWithPreTag = true;
			}

			boolean startsWithTableTag = false;

			if (tableTagIndex == 0) {
				startsWithTableTag = true;
			}

			if (startsWithPreTag || startsWithTableTag) {
				sb.append("\n");

				String tagName = null;

				if (preTagIndex == 0) {
					tagName = "pre";
				}
				else {
					tagName = "table";
				}

				String startTag = "<" + tagName + ">";
				String endTag = "</" + tagName + ">";

				int startTagLength = startTag.length();
				int endTagLength = endTag.length();

				int endTagIndex = cdata.indexOf(endTag, startTagLength - 1);

				sb.append(cdata.substring(0, endTagIndex + endTagLength));

				sb.append("\n");

				cdataBeginIndex = endTagIndex + endTagLength;
			}
			else {

				// Format the cdata up to the next pre or table tag

				int startTagIndex = 0;

				if (hasPreTag && hasTableTag) {
					if (preTagIndex < tableTagIndex) {
						startTagIndex = preTagIndex;
					}
					else {
						startTagIndex = tableTagIndex;
					}
				}
				else if (hasPreTag && !hasTableTag) {
					startTagIndex = preTagIndex;
				}
				else {

					// Must have table tag and no pre tag

					startTagIndex = tableTagIndex;
				}

				sb.append(_formatCDATA(cdata.substring(0, startTagIndex)));

				cdataBeginIndex = startTagIndex;
			}

			cdata = cdata.substring(cdataBeginIndex);
		}

		cdata = sb.toString();

		return cdata.trim();
	}

	private String _getClassKey(Element classElement) {
		return classElement.elementText("name");
	}

	private String _getClassKey(JavaClass javaClass) {
		return javaClass.getName();
	}

	private String _getClassName(String fileName) {
		int pos = fileName.lastIndexOf(StringPool.SLASH);

		return fileName.substring(pos + 1, fileName.length() - 5);
	}

	private String _getExecutableKey(Element executableElement) {
		StringBundler sb = new StringBundler();

		sb.append(executableElement.elementText("name"));
		sb.append(StringPool.OPEN_PARENTHESIS);

		List<Element> paramElements = executableElement.elements("param");

		for (Element paramElement : paramElements) {
			sb.append(paramElement.elementText("name"));
			sb.append("|");
			sb.append(paramElement.elementText("type"));
			sb.append(",");
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getExecutableKey(JavaExecutable javaExecutable) {
		StringBundler sb = new StringBundler();

		sb.append(javaExecutable.getName());
		sb.append(StringPool.OPEN_PARENTHESIS);

		List<JavaParameter> javaParameters = javaExecutable.getParameters();

		for (JavaParameter javaParameter : javaParameters) {
			sb.append(javaParameter.getName());
			sb.append("|");
			sb.append(_getTypeValue(javaParameter));
			sb.append(",");
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private String _getFieldKey(Element fieldElement) {
		return fieldElement.elementText("name");
	}

	private String _getFieldKey(JavaField javaField) {
		return javaField.getName();
	}

	private String _getIndent(String[] lines, int lineNumber) {
		String line = lines[lineNumber - 1];

		String indent = StringPool.BLANK;

		for (char c : line.toCharArray()) {
			if (Character.isWhitespace(c)) {
				indent += c;
			}
			else {
				break;
			}
		}

		return indent;
	}

	private int _getIndentLength(String indent) {
		int indentLength = 0;

		for (char c : indent.toCharArray()) {
			if (c == '\t') {
				indentLength = indentLength + 4;
			}
			else {
				indentLength++;
			}
		}

		return indentLength;
	}

	private JavaClass _getJavaClass(String fileName, Reader reader) {
		if (reader != null) {
			_javaProjectBuilder = new JavaProjectBuilder();

			_javaProjectBuilder.addSource(reader);
		}

		return _javaProjectBuilder.getClassByName(
			_packagePath + StringPool.PERIOD + _getClassName(fileName));
	}

	private String _getJavaClassComment(
			Element rootElement, JavaClass javaClass, String indent)
		throws Exception {

		StringBundler sb = new StringBundler(8);

		sb.append(indent);
		sb.append("/**\n");

		String comment = rootElement.elementText("comment");

		if (Validator.isNotNull(comment)) {
			comment = ToolsUtil.stripFullyQualifiedClassNames(
				comment, _imports, _packagePath);

			sb.append(_wrapText(comment, indent + " * "));
		}

		String docletTags = _addDocletTags(
			rootElement,
			new String[] {
				"author", "version", "param", "see", "since", "serial",
				"deprecated", "review"
			},
			indent + " * ", _hasPublicModifier(javaClass));

		if (Validator.isNotNull(docletTags)) {
			if (_initializeMissingJavadocs || Validator.isNotNull(comment)) {
				sb.append(indent);
				sb.append(" *\n");
			}

			sb.append(docletTags);
		}

		sb.append(indent);
		sb.append(" */\n");

		if (!_initializeMissingJavadocs && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		if (!_hasPublicModifier(javaClass) && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		return sb.toString();
	}

	private Document _getJavadocDocument(JavaClass javaClass) throws Exception {
		Element rootElement = DocumentHelper.createElement("javadoc");

		Document document = DocumentHelper.createDocument(rootElement);

		_addClassElement(rootElement, javaClass, false);

		return document;
	}

	private Tuple _getJavadocsXmlTuple(String fileName) throws Exception {
		File file = new File(_inputDirName + fileName);

		String absolutePath = file.getAbsolutePath();

		absolutePath = StringUtil.replace(absolutePath, '\\', '/');
		absolutePath = StringUtil.replace(absolutePath, "/./", "/");

		int pos = absolutePath.indexOf("/portal-impl/src/");

		String srcDirName = null;

		if (pos != -1) {
			srcDirName = absolutePath.substring(0, pos + 17);
		}

		if (srcDirName == null) {
			pos = absolutePath.indexOf("/portal-kernel/src/");

			if (pos == -1) {
				pos = absolutePath.indexOf("/portal-kernel/src/");
			}

			if (pos == -1) {
				pos = absolutePath.indexOf("/util-bridges/src/");
			}

			if (pos == -1) {
				pos = absolutePath.indexOf("/util-java/src/");
			}

			if (pos == -1) {
				pos = absolutePath.indexOf("/util-taglib/src/");
			}

			if (pos != -1) {
				srcDirName =
					absolutePath.substring(0, pos) + "/portal-impl/src/";
			}
		}

		if (srcDirName == null) {
			pos = absolutePath.indexOf("/WEB-INF/src/");

			if (pos != -1) {
				srcDirName = absolutePath.substring(0, pos + 13);
			}
		}

		if (srcDirName == null) {
			pos = absolutePath.indexOf("/src/main/java/");

			if (pos != -1) {
				srcDirName =
					absolutePath.substring(0, pos) + "/src/main/resources";
			}
		}

		if (srcDirName == null) {
			return null;
		}

		Tuple tuple = _javadocxXmlTuples.get(srcDirName);

		if (tuple != null) {
			return tuple;
		}

		File metaInfDir = new File(srcDirName, "META-INF");

		if (!metaInfDir.exists()) {
			metaInfDir.mkdirs();
		}

		File javadocsXmlFile = new File(
			metaInfDir, _outputFilePrefix + "-all.xml");

		String javadocsXmlContent = null;

		if (!javadocsXmlFile.exists()) {
			javadocsXmlContent =
				"<?xml version=\"1.0\"?>\n\n<javadocs>\n</javadocs>";

			_write(javadocsXmlFile, javadocsXmlContent);

			_modifiedFileNames.add(javadocsXmlFile.getAbsolutePath());
		}

		javadocsXmlContent = _read(javadocsXmlFile);

		SAXReader saxReader = _getSAXReader();

		Document javadocsXmlDocument = saxReader.read(
			new XMLSafeReader(javadocsXmlContent));

		tuple = new Tuple(
			srcDirName, javadocsXmlFile, javadocsXmlContent,
			javadocsXmlDocument);

		_javadocxXmlTuples.put(srcDirName, tuple);

		return tuple;
	}

	private String _getJavaExecutableComment(
			Map<String, Element> executableElementsMap,
			JavaExecutable javaExecutable, String indent)
		throws Exception {

		String executableKey = _getExecutableKey(javaExecutable);

		Element executableElement = executableElementsMap.get(executableKey);

		if (executableElement == null) {
			return null;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(indent);
		sb.append("/**\n");

		String comment = executableElement.elementText("comment");

		if (Validator.isNotNull(comment)) {
			comment = ToolsUtil.stripFullyQualifiedClassNames(
				comment, _imports, _packagePath);

			sb.append(_wrapText(comment, indent + " * "));
		}

		String[] tags = null;

		if (javaExecutable instanceof JavaMethod) {
			tags = new String[] {
				"version", "param", "return", "throws", "see", "since",
				"deprecated", "review"
			};
		}
		else {
			tags = new String[] {
				"version", "param", "throws", "see", "since", "deprecated",
				"review"
			};
		}

		String docletTags = _addDocletTags(
			executableElement, tags, indent + " * ",
			_hasPublicModifier(javaExecutable));

		if (Validator.isNotNull(docletTags)) {
			if (_initializeMissingJavadocs || Validator.isNotNull(comment)) {
				sb.append(indent);
				sb.append(" *\n");
			}

			sb.append(docletTags);
		}

		sb.append(indent);
		sb.append(" */\n");

		if (!_initializeMissingJavadocs && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		if (!_hasPublicModifier(javaExecutable) && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		return sb.toString();
	}

	private String _getJavaFieldComment(
			Map<String, Element> fieldElementsMap, JavaField javaField,
			String indent)
		throws Exception {

		String fieldKey = _getFieldKey(javaField);

		Element fieldElement = fieldElementsMap.get(fieldKey);

		if (fieldElement == null) {
			return null;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(indent);
		sb.append("/**\n");

		String comment = fieldElement.elementText("comment");

		if (Validator.isNotNull(comment)) {
			comment = ToolsUtil.stripFullyQualifiedClassNames(
				comment, _imports, _packagePath);

			sb.append(_wrapText(comment, indent + " * "));
		}

		String docletTags = _addDocletTags(
			fieldElement,
			new String[] {"version", "see", "since", "deprecated", "review"},
			indent + " * ", _hasPublicModifier(javaField));

		if (Validator.isNotNull(docletTags)) {
			if (_initializeMissingJavadocs || Validator.isNotNull(comment)) {
				sb.append(indent);
				sb.append(" *\n");
			}

			sb.append(docletTags);
		}

		sb.append(indent);
		sb.append(" */\n");

		if (!_initializeMissingJavadocs && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		if (!_hasPublicModifier(javaField) && Validator.isNull(comment) &&
			Validator.isNull(docletTags)) {

			return null;
		}

		return sb.toString();
	}

	private int _getJavaModelLineNumber(JavaModel javaModel, String content) {
		String[] lines = StringUtil.splitLines(content);

		if (javaModel instanceof JavaClass) {
			JavaClass javaClass = (JavaClass)javaModel;

			List<String> modifiers = javaClass.getModifiers();

			if (modifiers.isEmpty()) {
				return _getAdjustedLineNumber(
					javaModel.getLineNumber(), javaModel);
			}

			String modifier = modifiers.get(0);

			for (int i = javaClass.getLineNumber() - 1; i < lines.length; i++) {
				String line = StringUtil.trim(lines[i - 1]);

				if (line.startsWith(modifier + StringPool.SPACE)) {
					return _getAdjustedLineNumber(i, javaModel);
				}
			}

			return -1;
		}

		if (javaModel instanceof JavaField) {
			JavaField javaField = (JavaField)javaModel;

			if (javaField.isEnumConstant()) {
				for (int i = javaModel.getLineNumber(); i < lines.length; i++) {
					String line = lines[i - 1];

					if (line.matches(
							".*\\W" + javaField.getName() + "(\\W.*)?")) {

						return _getAdjustedLineNumber(i, javaModel);
					}
				}
			}

			return _getAdjustedLineNumber(javaModel.getLineNumber(), javaModel);
		}

		JavaMember javaMember = (JavaMember)javaModel;

		List<String> modifiers = javaMember.getModifiers();

		if (modifiers.isEmpty()) {
			return _getAdjustedLineNumber(javaModel.getLineNumber(), javaModel);
		}

		String modifier = modifiers.get(0);

		for (int i = javaModel.getLineNumber(); i > 0; i--) {
			String line = StringUtil.trim(lines[i - 1]);

			if (line.startsWith(modifier + StringPool.SPACE)) {
				return _getAdjustedLineNumber(i, javaModel);
			}
		}

		return -1;
	}

	private Set<Integer> _getJavaTermLineNumbers(
		Set<Integer> lineNumbers, JavaClass javaClass, String content) {

		lineNumbers.add(_getJavaModelLineNumber(javaClass, content));

		List<JavaConstructor> javaConstructors = javaClass.getConstructors();

		for (JavaConstructor javaConstructor : javaConstructors) {
			lineNumbers.add(_getJavaModelLineNumber(javaConstructor, content));
		}

		List<JavaMethod> javaMethods = javaClass.getMethods();

		for (JavaMethod javaMethod : javaMethods) {
			lineNumbers.add(_getJavaModelLineNumber(javaMethod, content));
		}

		List<JavaField> javaFields = javaClass.getFields();

		for (JavaField javaField : javaFields) {
			lineNumbers.add(_getJavaModelLineNumber(javaField, content));
		}

		List<JavaClass> nestedClasses = javaClass.getNestedClasses();

		for (JavaClass nestedClass : nestedClasses) {
			lineNumbers = _getJavaTermLineNumbers(
				lineNumbers, nestedClass, content);
		}

		return lineNumbers;
	}

	private SAXReader _getSAXReader() {
		return SAXReaderFactory.getSAXReader(null, false, false);
	}

	private String _getSpacesIndent(int length) {
		String indent = StringPool.BLANK;

		for (int i = 0; i < length; i++) {
			indent += StringPool.SPACE;
		}

		return indent;
	}

	private String _getTypeValue(JavaParameter javaParameter) {
		JavaType type = javaParameter.getType();

		return type.getValue();
	}

	private String _getUpdateJavaFromDocument(
			String fileName, String javadocLessContent, Document document)
		throws Exception {

		String[] lines = StringUtil.splitLines(javadocLessContent);

		JavaClass javaClass = _getJavaClass(
			fileName, new UnsyncStringReader(javadocLessContent));

		_updateLanguageProperties(document, javaClass.getName());

		Element rootElement = document.getRootElement();

		Map<Integer, String> commentsMap = _addComments(
			new TreeMap<Integer, String>(), rootElement, javaClass,
			javadocLessContent, lines);

		StringBundler sb = new StringBundler(javadocLessContent.length());

		for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
			String line = lines[lineNumber - 1];

			String comments = commentsMap.get(lineNumber);

			if (comments != null) {
				sb.append(comments);
			}

			sb.append(line);
			sb.append("\n");
		}

		String formattedContent = sb.toString();

		return formattedContent.trim();
	}

	private boolean _hasAnnotation(
		JavaAnnotatedElement javaAnnotatedElement, String annotationName) {

		List<JavaAnnotation> annotations =
			javaAnnotatedElement.getAnnotations();

		if (annotations == null) {
			return false;
		}

		for (JavaAnnotation javaAnnotation : annotations) {
			JavaClass javaClass = javaAnnotation.getType();

			if (annotationName.equals(javaClass.getName())) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasGeneratedTag(String content) {
		if ((content.contains("* @generated") || content.contains("$ANTLR") ||
			 content.contains("auto-generated from WSDL")) &&
			!content.contains("hasGeneratedTag")) {

			return true;
		}
		else {
			return false;
		}
	}

	private boolean _hasPublicModifier(JavaClass javaClass) {
		return _hasPublicModifier(javaClass.getModifiers());
	}

	private boolean _hasPublicModifier(JavaMember javaMember) {
		return _hasPublicModifier(javaMember.getModifiers());
	}

	private boolean _hasPublicModifier(List<String> modifiers) {
		if (modifiers == null) {
			return false;
		}

		for (String modifier : modifiers) {
			if (modifier.equals("public")) {
				return true;
			}
		}

		return false;
	}

	private String _read(File file) throws IOException {
		String s = new String(
			Files.readAllBytes(file.toPath()), StringPool.UTF8);

		return StringUtil.replace(
			s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
	}

	private String _removeJavadocFromJava(JavaClass javaClass, String content) {
		Set<Integer> lineNumbers = _getJavaTermLineNumbers(
			new HashSet<Integer>(), javaClass, content);

		String[] lines = StringUtil.splitLines(content);

		for (int lineNumber : lineNumbers) {
			if (lineNumber == -1) {
				continue;
			}

			int pos = lineNumber - 2;

			String line = lines[pos];

			if (line == null) {
				continue;
			}

			int blankLines = 0;

			while (line.equals(StringPool.BLANK)) {
				line = lines[--pos];

				blankLines++;
			}

			line = line.trim();

			if (line.endsWith("*/")) {
				while (true) {
					lines[pos] = null;

					if (line.startsWith("/**") || line.startsWith("/*")) {
						break;
					}

					line = lines[--pos].trim();
				}

				for (int i = 0; i < blankLines; i++) {
					lines[lineNumber - i - 2] = null;
				}
			}
		}

		StringBundler sb = new StringBundler(content.length());

		for (String line : lines) {
			if (line != null) {
				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		return content.trim();
	}

	private void _sortElementsByChildElement(
		Element element, String elementName, String childElementName) {

		Map<String, Element> elementsMap = new TreeMap<>();

		List<Element> elements = element.elements();

		for (Element curElement : elements) {
			curElement.detach();

			if (elementName.equals(curElement.getName())) {
				String childElementValue = curElement.elementText(
					childElementName);

				elementsMap.put(childElementValue, curElement);
			}
		}

		for (Element curElement : elements) {
			if (elementName.equals(curElement.getName())) {
				break;
			}

			element.add(curElement);
		}

		for (Map.Entry<String, Element> entry : elementsMap.entrySet()) {
			Element curElement = entry.getValue();

			element.add(curElement);
		}

		boolean foundLastElementWithElementName = false;

		for (int i = 0; i < elements.size(); i++) {
			Element curElement = elements.get(i);

			if (!foundLastElementWithElementName) {
				if (elementName.equals(curElement.getName())) {
					if ((i + 1) < elements.size()) {
						Element nextElement = elements.get(i + 1);

						if (!elementName.equals(nextElement.getName())) {
							foundLastElementWithElementName = true;
						}
					}
				}
			}
			else {
				element.add(curElement);
			}
		}
	}

	private String _trimMultilineText(String text) {
		String[] lines = StringUtil.splitLines(text);

		StringBundler sb = new StringBundler();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();

			sb.append(line);

			if (!line.endsWith(StringPool.OPEN_PARENTHESIS) &&
				(i < (lines.length - 1))) {

				sb.append(StringPool.SPACE);
			}
		}

		return sb.toString();
	}

	private void _updateJavadocsXmlFile(
			String fileName, JavaClass javaClass, Document javaClassDocument)
		throws Exception {

		String javaClassFullyQualifiedName = javaClass.getFullyQualifiedName();

		/*if (!javaClassFullyQualifiedName.contains(".service.") ||
			!javaClassFullyQualifiedName.endsWith("ServiceImpl")) {

			return;
		}*/

		Tuple javadocsXmlTuple = _getJavadocsXmlTuple(fileName);

		if (javadocsXmlTuple == null) {
			return;
		}

		Document javadocsXmlDocument = (Document)javadocsXmlTuple.getObject(3);

		Element javadocsXmlRootElement = javadocsXmlDocument.getRootElement();

		List<Element> javadocElements = javadocsXmlRootElement.elements(
			"javadoc");

		for (Element javadocElement : javadocElements) {
			String type = javadocElement.elementText("type");

			if (type.equals(javaClassFullyQualifiedName)) {
				Element javaClassRootElement =
					javaClassDocument.getRootElement();

				if (Objects.equals(
						_formattedString(javadocElement),
						_formattedString(javaClassRootElement))) {

					return;
				}

				javadocElement.detach();

				break;
			}
		}

		javadocsXmlRootElement.add(javaClassDocument.getRootElement());
	}

	private void _updateLanguageProperties(Document document, String className)
		throws IOException {

		if (_languageProperties == null) {
			return;
		}

		int index = className.indexOf("ServiceImpl");

		if (index <= 0) {
			return;
		}

		StringBundler sb = new StringBundler();

		sb.append(Character.toLowerCase(className.charAt(0)));

		for (int i = 1; i < index; i++) {
			char c = className.charAt(i);

			if (Character.isUpperCase(c)) {
				if (((i + 1) < index) &&
					Character.isLowerCase(className.charAt(i + 1))) {

					sb.append(CharPool.DASH);
				}

				sb.append(Character.toLowerCase(c));
			}
			else {
				sb.append(c);
			}
		}

		sb.append("-service-help");

		String key = sb.toString();

		String value = _languageProperties.getProperty(key);

		if (value == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		String comment = rootElement.elementText("comment");

		if ((comment == null) || value.equals(comment)) {
			return;
		}

		index = comment.indexOf("\n\n");

		if (index != -1) {
			value = comment.substring(0, index);
		}
		else {
			value = comment;
		}

		_updateLanguageProperties(key, value);
	}

	private void _updateLanguageProperties(String key, String value)
		throws IOException {

		StringBundler sb = new StringBundler();

		try (BufferedReader bufferedReader = Files.newBufferedReader(
				_languagePropertiesFile.toPath(), StandardCharsets.UTF_8)) {

			boolean begin = false;
			boolean firstLine = true;
			String linePrefix = key + "=";

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.equals(StringPool.BLANK)) {
					begin = !begin;
				}

				if (firstLine) {
					firstLine = false;
				}
				else {
					sb.append(StringPool.NEW_LINE);
				}

				if (line.startsWith(linePrefix)) {
					sb.append(linePrefix);
					sb.append(value);
				}
				else {
					sb.append(line);
				}
			}
		}

		try (Writer writer = new OutputStreamWriter(
				new FileOutputStream(_languagePropertiesFile, false),
				StandardCharsets.UTF_8)) {

			sb.writeTo(writer);
		}

		System.out.println(
			StringBundler.concat(
				"Updating ", String.valueOf(_languagePropertiesFile), " key ",
				key));
	}

	private String _wrap(String text, int width) {
		if (text == null) {
			return null;
		}

		StringBundler sb = new StringBundler();

		for (String line : StringUtil.splitLines(text)) {
			if (line.isEmpty()) {
				sb.append("\n");

				continue;
			}

			int lineLength = 0;

			for (String token : StringUtil.split(line, CharPool.SPACE)) {
				if ((lineLength + token.length() + 1) > width) {
					if (lineLength > 0) {
						sb.append("\n");
					}

					if (token.length() > width) {
						int pos = token.indexOf(CharPool.OPEN_PARENTHESIS);

						if (pos != -1) {
							sb.append(token.substring(0, pos + 1));
							sb.append("\n");

							token = token.substring(pos + 1);

							sb.append(token);

							lineLength = token.length();
						}
						else {
							sb.append(token);

							lineLength = token.length();
						}
					}
					else {
						sb.append(token);

						lineLength = token.length();
					}
				}
				else {
					if (lineLength > 0) {
						sb.append(StringPool.SPACE);

						lineLength++;
					}

					sb.append(token);

					lineLength += token.length();
				}
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	private String _wrapText(String text, int indentLength, String exclude) {
		StringBuffer sb = new StringBuffer();

		StringBundler regexSB = new StringBundler("(?<=^|</");

		regexSB.append(exclude);
		regexSB.append(">).+?(?=$|<");
		regexSB.append(exclude);
		regexSB.append(">)");

		Pattern pattern = Pattern.compile(regexSB.toString(), Pattern.DOTALL);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			String wrapped = _formatInlines(matcher.group());

			wrapped = _wrap(wrapped, 80 - indentLength);

			matcher.appendReplacement(sb, wrapped);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _wrapText(String text, String indent) {
		int indentLength = _getIndentLength(indent);

		if (text.contains("<pre>")) {
			text = _wrapText(text, indentLength, "pre");
		}
		else if (text.contains("<table>")) {
			text = _wrapText(text, indentLength, "table");
		}
		else {
			text = _formatInlines(text);
			text = _wrap(text, 80 - indentLength);
		}

		text = text.replaceAll("(?m)^", indent);
		text = text.replaceAll("(?m) +$", StringPool.BLANK);

		return text;
	}

	private void _write(File file, String s) throws IOException {
		Files.write(file.toPath(), s.getBytes(StandardCharsets.UTF_8));
	}

	private final String _author;
	private final boolean _generateXml;
	private String _imports;
	private final boolean _initializeMissingJavadocs;
	private final String _inputDirName;
	private final Map<String, Tuple> _javadocxXmlTuples = new HashMap<>();
	private JavaProjectBuilder _javaProjectBuilder;
	private final Properties _languageProperties;
	private final File _languagePropertiesFile;
	private final Set<String> _modifiedFileNames = new HashSet<>();
	private final String _outputFilePrefix;
	private String _packagePath;
	private final Pattern _paragraphTagPattern = Pattern.compile(
		"(^.*?(?=\n\n|$)+|(?<=<p>\n).*?(?=\n</p>))", Pattern.DOTALL);
	private final boolean _updateJavadocs;

}