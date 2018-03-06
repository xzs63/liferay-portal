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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.CreateDBClusterRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DeleteDBClusterRequest;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DescribeDBClustersRequest;
import com.amazonaws.services.rds.model.DescribeDBClustersResult;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.vm.provisioner.VMProvisioner;

import java.util.List;

/**
 * @author Kiyoshi Lee
 */
public abstract class AuroraAmazonVMProvisioner extends VMProvisioner {

	public void create() {
		CreateDBClusterRequest createDBClusterRequest =
			new CreateDBClusterRequest();

		createDBClusterRequest.withBackupRetentionPeriod(1);
		createDBClusterRequest.withDBClusterIdentifier(_dbClusterId);
		createDBClusterRequest.withEngine(_dbEngine);
		createDBClusterRequest.withEngineVersion(_dbEngineVersion);
		createDBClusterRequest.withMasterUsername(_dbUsername);
		createDBClusterRequest.withMasterUserPassword(_dbPassword);
		createDBClusterRequest.withVpcSecurityGroupIds("sg-9ce452fb");

		_amazonRDS.createDBCluster(createDBClusterRequest);

		String dbClusterStatus = _getDBClusterStatus();

		System.out.println("Waiting for the DB cluster to start");

		long timeout = System.currentTimeMillis() + TIMEOUT_DURATION;

		while (!dbClusterStatus.equals("available")) {
			if (System.currentTimeMillis() >= timeout) {
				throw new RuntimeException(
					"Timeout occurred while waiting for DB cluster status " +
						"\"available\"");
			}

			JenkinsResultsParserUtil.sleep(1000 * 30);

			dbClusterStatus = _getDBClusterStatus();
		}

		CreateDBInstanceRequest createDBInstanceRequest =
			new CreateDBInstanceRequest();

		createDBInstanceRequest.withDBClusterIdentifier(_dbClusterId);
		createDBInstanceRequest.withDBInstanceClass(_dbInstanceClass);
		createDBInstanceRequest.withDBInstanceIdentifier(_dbInstanceId);
		createDBInstanceRequest.withEngine(_dbEngine);
		createDBInstanceRequest.withMultiAZ(false);
		createDBInstanceRequest.withPubliclyAccessible(true);

		_amazonRDS.createDBInstance(createDBInstanceRequest);

		String dbInstanceStatus = _getDBInstanceStatus();

		System.out.println("Waiting for the DB instance to start");

		timeout = System.currentTimeMillis() + TIMEOUT_DURATION;

		while (!dbInstanceStatus.equals("available")) {
			if (System.currentTimeMillis() >= timeout) {
				throw new RuntimeException(
					"Timeout occurred while waiting for DB instance status " +
						"\"available\"");
			}

			JenkinsResultsParserUtil.sleep(1000 * 30);

			dbInstanceStatus = _getDBInstanceStatus();
		}
	}

	public void delete() {
		DeleteDBInstanceRequest deleteDBInstanceRequest =
			new DeleteDBInstanceRequest();

		deleteDBInstanceRequest.withDBInstanceIdentifier(_dbInstanceId);
		deleteDBInstanceRequest.withSkipFinalSnapshot(true);

		_amazonRDS.deleteDBInstance(deleteDBInstanceRequest);

		DeleteDBClusterRequest deleteDBClusterRequest =
			new DeleteDBClusterRequest();

		deleteDBClusterRequest.withDBClusterIdentifier(_dbClusterId);

		deleteDBClusterRequest.withSkipFinalSnapshot(true);

		_amazonRDS.deleteDBCluster(deleteDBClusterRequest);
	}

	public String getAddress() {
		DBInstance dbInstance = _getDBInstance();

		Endpoint endpoint = dbInstance.getEndpoint();

		return endpoint.getAddress();
	}

	public String getDBEngine() {
		return _dbEngine;
	}

	public String getDBEngineVersion() {
		return _dbEngineVersion;
	}

	public String getDBPassword() {
		return _dbPassword;
	}

	public String getDBUsername() {
		return _dbUsername;
	}

	protected AuroraAmazonVMProvisioner(
		String awsAccessKeyId, String awsSecretAccessKey, String dbInstanceId) {

		_dbInstanceId = dbInstanceId;

		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
			awsAccessKeyId, awsSecretAccessKey);

		AmazonRDSClientBuilder amazonRDSClientBuilder =
			AmazonRDSClientBuilder.standard();

		amazonRDSClientBuilder.withCredentials(
			new AWSStaticCredentialsProvider(basicAWSCredentials));
		amazonRDSClientBuilder.withRegion(Regions.US_WEST_1);

		_amazonRDS = amazonRDSClientBuilder.build();

		_dbClusterId = _getDbClusterId();
	}

	protected AuroraAmazonVMProvisioner(
		String awsAccessKeyId, String awsSecretAccessKey, String dbClusterId,
		String dbEngine, String dbEngineVersion, String dbInstanceClass,
		String dbInstanceId, String dbPassword, String dbUsername) {

		_dbClusterId = dbClusterId;
		_dbEngine = dbEngine;
		_dbEngineVersion = dbEngineVersion;
		_dbInstanceClass = dbInstanceClass;
		_dbInstanceId = dbInstanceId;
		_dbPassword = dbPassword;
		_dbUsername = dbUsername;

		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
			awsAccessKeyId, awsSecretAccessKey);

		AmazonRDSClientBuilder amazonRDSClientBuilder =
			AmazonRDSClientBuilder.standard();

		amazonRDSClientBuilder.withCredentials(
			new AWSStaticCredentialsProvider(basicAWSCredentials));
		amazonRDSClientBuilder.withRegion(Regions.US_WEST_1);

		_amazonRDS = amazonRDSClientBuilder.build();
	}

	private String _getDbClusterId() {
		DBInstance dbInstance = _getDBInstance();

		return dbInstance.getDBClusterIdentifier();
	}

	private String _getDBClusterStatus() {
		DescribeDBClustersRequest describeDBClustersRequest =
			new DescribeDBClustersRequest();

		describeDBClustersRequest.withDBClusterIdentifier(_dbClusterId);

		DescribeDBClustersResult describeDBClustersResult =
			_amazonRDS.describeDBClusters(describeDBClustersRequest);

		List<DBCluster> auroraClusters =
			describeDBClustersResult.getDBClusters();

		DBCluster auroraCluster = auroraClusters.get(0);

		return auroraCluster.getStatus();
	}

	private DBInstance _getDBInstance() {
		DescribeDBInstancesRequest describeDBInstancesRequest =
			new DescribeDBInstancesRequest();

		describeDBInstancesRequest.withDBInstanceIdentifier(_dbInstanceId);

		DescribeDBInstancesResult describeDBInstancesResult =
			_amazonRDS.describeDBInstances(describeDBInstancesRequest);

		List<DBInstance> dbInstances =
			describeDBInstancesResult.getDBInstances();

		return dbInstances.get(0);
	}

	private String _getDBInstanceStatus() {
		DBInstance dbInstance = _getDBInstance();

		return dbInstance.getDBInstanceStatus();
	}

	private final AmazonRDS _amazonRDS;
	private final String _dbClusterId;
	private String _dbEngine;
	private String _dbEngineVersion;
	private String _dbInstanceClass;
	private final String _dbInstanceId;
	private String _dbPassword;
	private String _dbUsername;

}