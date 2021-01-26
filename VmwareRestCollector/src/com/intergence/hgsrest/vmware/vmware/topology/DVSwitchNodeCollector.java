/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;


import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;

public class DVSwitchNodeCollector {
	private static final AttributeMap nodeAttributes = buildAttributeMap();
	
	public static AttributeMap getProperties() {
		return nodeAttributes;
	}
	
	public static void persist(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, com.intergence.hgsrest.vmware.vmware.topology.Inventory dvss) {
		for (Entity dvs : dvss.allEntities()) {
			modelMapper.addNode(dvs, NodeTypeEnum.SWITCH, nodeAttributes);
		}
	}
	
	private static AttributeMap buildAttributeMap() {
		AttributeMap attributes = new AttributeMap();
		
		return attributes;
	}
}
