/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;


import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;

public class DatastoreNodeCollector {
	private static final AttributeMap nodeAttributes = buildAttributeMap();
	
	public static AttributeMap getProperties() {
		return nodeAttributes;
	}
	
	public static void persist(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, com.intergence.hgsrest.vmware.vmware.topology.Inventory datastores) {
		for (Entity datastore : datastores.allEntities()) {
			addUuid(datastore);
			modelMapper.addNode(datastore, NodeTypeEnum.STORAGE, nodeAttributes);
		}
	}
	
	private static void addUuid(Entity entity) {
		String url = entity.getAttribute("info.url"); // TODO can this be null?
		int start = url.lastIndexOf(':') + 1;
		int end = url.length() - 1;
		String uuid = url.substring(start, end);
		entity.setNiceAttribute("UUID", uuid);
	}
	
	private static AttributeMap buildAttributeMap() {
		AttributeMap attributes = new AttributeMap();
		
		attributes.mapAbsolute("info.name", NodeCollectorConstants.ATTRIBUTE_NAME);
		attributes.mapAbsolute("info.url", "URL");
		
		attributes.setRoot("summary");
		attributes.map("accessible");
		attributes.map("capacity");
		attributes.map("multipleHostAccess", "multi-host access");
		attributes.map("type", "filesystem type");
		attributes.map("uncommitted", "uncommitted space (bytes)", 4.0, null);
		attributes.map("freeSpace", "free space (bytes)");
		
		return attributes;
	}
}
