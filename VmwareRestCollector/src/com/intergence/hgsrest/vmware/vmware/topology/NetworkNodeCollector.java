/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;

public class NetworkNodeCollector {
	private static final AttributeMap nodeAttributes = buildAttributeMap();
	
	public static AttributeMap getProperties() {
		return nodeAttributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory networks) {
		modelMapper.addNodes(networks,  NodeTypeEnum.PORT_GROUP, nodeAttributes);
	}

	
	private static AttributeMap buildAttributeMap() {
		AttributeMap attributes = new AttributeMap();
		attributes.mapAbsolute("name", NodeCollectorConstants.ATTRIBUTE_NAME);
		attributes.mapAbsolute("summary.accessible", "accessible");
		return attributes;
	}
}