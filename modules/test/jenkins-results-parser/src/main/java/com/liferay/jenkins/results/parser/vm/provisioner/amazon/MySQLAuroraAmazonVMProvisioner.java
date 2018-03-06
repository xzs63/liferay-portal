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

package com.liferay.jenkins.results.parser.vm.provisioner.amazon;

/**
 * @author Kiyoshi Lee
 */
public class MySQLAuroraAmazonVMProvisioner extends AuroraAmazonVMProvisioner {

	public MySQLAuroraAmazonVMProvisioner(
		String awsAccessKeyId, String awsSecretAccessKey, String dbInstanceId) {

		super(awsAccessKeyId, awsSecretAccessKey, dbInstanceId);
	}

	public MySQLAuroraAmazonVMProvisioner(
		String awsAccessKeyId, String awsSecretAccessKey, String dbClusterId,
		String dbInstanceClass, String dbInstanceId) {

		super(
			awsAccessKeyId, awsSecretAccessKey, dbClusterId, "aurora",
			"5.6.10a", dbInstanceClass, dbInstanceId, "password", "root");
	}

	public MySQLAuroraAmazonVMProvisioner(
		String awsAccessKeyId, String awsSecretAccessKey, String dbClusterId,
		String dbEngineVersion, String dbInstanceClass, String dbInstanceId,
		String dbPassword, String dbUsername) {

		super(
			awsAccessKeyId, awsSecretAccessKey, dbClusterId, "aurora",
			dbEngineVersion, dbInstanceClass, dbInstanceId, dbPassword,
			dbUsername);
	}

}