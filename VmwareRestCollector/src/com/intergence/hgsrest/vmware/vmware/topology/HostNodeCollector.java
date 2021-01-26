/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;

public class HostNodeCollector {
	private static final AttributeMap nodeAttributes = buildAttributeMap();

	public static AttributeMap getProperties() {
		return nodeAttributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory hosts) {
		modelMapper.addNodes(hosts, NodeTypeEnum.SERVER, nodeAttributes);
	}
	
	private static AttributeMap buildAttributeMap() {
		AttributeMap attributes = new AttributeMap();
		addBasicAttributes(attributes);
		addHardwareSummary(attributes);
		addRuntimeSummary(attributes);
		addConfigSummary(attributes);
		return attributes;
	}
	
	private static void addBasicAttributes(AttributeMap attributes) {
		attributes.mapAbsolute("name", NodeCollectorConstants.ATTRIBUTE_NAME);
	}
	
	private static void addHardwareSummary(AttributeMap attributes) {
		attributes.setRoot("summary.hardware");
		attributes.map("model");
		attributes.map("vendor");
		attributes.map("cpuModel", "CPU model");
		attributes.map("memorySize", "memory size");
	}
	
	private static void addRuntimeSummary(AttributeMap attributes) {
		attributes.setRoot("runtime");
		attributes.map("bootTime", "boot time");
		attributes.map("powerState", "power state", 2.5, null);
	}
	
	private static void addConfigSummary(AttributeMap attributes) {
		attributes.setRoot("summary.config");
		attributes.map("vmotionEnabled", "VMotion enabled");
	}
}
