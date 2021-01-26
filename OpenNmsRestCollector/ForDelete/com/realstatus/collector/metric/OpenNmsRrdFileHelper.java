/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.realstatus.collector.metric;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.realstatus.collector.PropertiesKeys;
import com.realstatus.hgs.collection.EndpointCollectorConstants;
import com.realstatus.hgs.collection.NodeCollectorConstants;
import com.realstatus.hgs.model.Endpoint;
import com.realstatus.hgs.model.Node;
import com.realstatus.hgs.util.PropertiesLoader;

public class OpenNmsRrdFileHelper {

	public static final String SNMP_DIRECTORY = "snmp";
	public static final String RESPONSE_DIRECTORY = "response";
	
	private static final Logger logger = Logger.getLogger(OpenNmsRrdFileHelper.class);
	
	private static String DRIVE_LETTER_NOT_END_WITH = "Memory";
	
	private static String FILE_SEPARATOR = System.getProperty("file.separator");
			
	public static Collection<File> getRrdDirectories(Endpoint entity) {
		Collection<File> rrdDirectories = new ArrayList<File>();
		File snmpDirectory = getRrdDirectory(entity, SNMP_DIRECTORY);
		if (snmpDirectory != null) {
			rrdDirectories.add(snmpDirectory);
		}
		
		File responseDirectory = getRrdDirectory(entity, RESPONSE_DIRECTORY);
		if (responseDirectory != null && responseDirectory.isDirectory()) {
			rrdDirectories.add(responseDirectory);
		}
		return rrdDirectories;
	}
	
	public static Collection<File> getRrdDirectories(Node entity) {
		Preconditions.checkNotNull(entity);
		
		Collection<File> rrdDirectories = new ArrayList<File>();
		File snmpDirectory = getRrdDirectory(entity, SNMP_DIRECTORY);
		if (snmpDirectory != null) {
			rrdDirectories.add(snmpDirectory);
		}
		
		File responseDirectory = getRrdDirectory(entity, RESPONSE_DIRECTORY);
		if (responseDirectory != null && responseDirectory.isDirectory()) {
			rrdDirectories.add(responseDirectory);
		}
		return rrdDirectories;
	}
		
	public static File getRrdDirectory(Endpoint endpoint, String lastPathElement) {
		String snmpIfName = endpoint.getAttribute(EndpointCollectorConstants.ATTRIBUTE_IF_NAME);
		String snmpPhysAddr = endpoint.getAttribute(EndpointCollectorConstants.ATTRIBUTE_PHYS_ADDR);
		if (snmpIfName == null || snmpPhysAddr == null) {
			return null;
		}
		
		String root = PropertiesLoader.getStringProperty(PropertiesKeys.OPENNMS_RRD_ROOT);
		
		StringBuilder filename = new StringBuilder(root);
		filename.append(FILE_SEPARATOR);
		filename.append(lastPathElement);
		filename.append(FILE_SEPARATOR);
		
		if (lastPathElement.equals(RESPONSE_DIRECTORY)) {
			filename.append(endpoint.getAttribute(EndpointCollectorConstants.ATTRIBUTE_IP_ADDRESS));
		} else {
			filename.append(endpoint.getNodeKey().getForeignSourceId());
			filename.append(FILE_SEPARATOR);
			filename.append(snmpIfName.replace('/', '_'));
			filename.append('-');
			filename.append(snmpPhysAddr);
		}
		return new File(filename.toString());
	}

	public File getFile(Node node, String filePart) {
		File nodeSnmpDirectory = getRrdDirectory(node, SNMP_DIRECTORY);
		File file = getFile(nodeSnmpDirectory, filePart);
		if (file == null || !file.exists()) {
			File nodeResponseDirectory = getRrdDirectory(node, RESPONSE_DIRECTORY);
			if (nodeResponseDirectory != null) {
				file = getFile(nodeResponseDirectory, filePart);
			}
			
			if (file == null || !file.exists()) {
				throw new RuntimeException("File: '" +filePart +".jrb' not found in either: '" +nodeSnmpDirectory.getAbsolutePath() + "', or the corresponding response directory. Aborting collection attempt");
			}
		}
				
		return file;
	}
	
	public File getFile(Endpoint endpoint, String filePart) {
		File file = getFile(getRrdDirectory(endpoint, SNMP_DIRECTORY), filePart);
		if (file == null || !file.exists()) {
			file = getFile(getRrdDirectory(endpoint, RESPONSE_DIRECTORY), filePart);
		}
		
		return file;
	}

	public static File getFile(File directory, String filePart) {
		String path = directory.getAbsolutePath();
		
		StringBuffer filePath = new StringBuffer(path);
		filePath.append(FILE_SEPARATOR);
		filePath.append(filePart);
		filePath.append(".jrb");
		logger.trace("filePath : " +filePath.toString());
		return new File(filePath.toString());
	}

	public static File getHostResourcesFile(Node node, String driveDirectory, String filePart) {
		File[] drives = getHostResourcesDrivesDirectories(node);
		
		for (File drive : drives) {
			if (drive.getName().endsWith(driveDirectory)) {
				return getFile(drive, filePart);
			}
		}
		return null;
	}
	
	public static Collection<String> getHostResourcesDriveLetters(Node node) {
		Collection<String> driveLetters = new ArrayList<String>();
		File[] drives = getHostResourcesDrivesDirectories(node);
		for (File candidateDrive : drives) {
			if (!candidateDrive.getName().endsWith(DRIVE_LETTER_NOT_END_WITH)) {
				driveLetters.add(candidateDrive.getName());
			}
		}
		return driveLetters;
	}

	public static File[] getHostResourcesDrivesDirectories(Node node) {
		File directory = OpenNmsRrdFileHelper.getRrdDirectory(node, OpenNmsRrdFileHelper.SNMP_DIRECTORY);
		if (directory != null) {
			File[] hrStorageIndex = directory.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith("hrStorageIndex");
				}
			});
			
			if (hrStorageIndex != null) {
				// Should return just one hrStorageIndex directory which will contain 
				// a directory for each drive + PhysicalMemory and VirtualMemory.
				// Each of these directories then contains storage metrics of the same names.
				for (File hrStorageIndexDirectory : hrStorageIndex) {
					return hrStorageIndexDirectory.listFiles();
				}
			}
		}
		return new File[] {};
	}
	
	private static File getRrdDirectory(Node node, String lastPathElement) {
		String root = PropertiesLoader.getStringProperty(PropertiesKeys.OPENNMS_RRD_ROOT);
		
		StringBuilder filename = new StringBuilder(root);
		filename.append(FILE_SEPARATOR);
		filename.append(lastPathElement);
		filename.append(FILE_SEPARATOR);
		
		if (lastPathElement.equals(RESPONSE_DIRECTORY)) {
			String ipAddress = node.getAttribute(NodeCollectorConstants.ATTRIBUTE_IPADDRESS);
			if (ipAddress == null) {
				return null;
			}
			filename.append(ipAddress);
		} else {
			filename.append(node.getForeignSourceId());
		}
		
		return new File(filename.toString());
	}
	
}
