/**
 * Copyright 2011 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ManagedObjectReference;


public class ComputeResourceGroupCollector {
	private static final AttributeMap clusterAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return clusterAttributes;
	}
	
	public static void collect(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, com.intergence.hgsrest.vmware.vmware.topology.Inventory hosts, com.intergence.hgsrest.vmware.vmware.topology.Inventory datastores, com.intergence.hgsrest.vmware.vmware.topology.Inventory networks, com.intergence.hgsrest.vmware.vmware.topology.Inventory clusters) {
		for (Entity cluster : clusters.allEntities()) {
			String name = cluster.getAttribute("name");
			annotateClusterName(modelMapper, name, cluster.getAttribute("host"), hosts);
			annotateClusterName(modelMapper, name, cluster.getAttribute("datastore"), datastores);
			annotateClusterName(modelMapper, name, cluster.getAttribute("network"), networks);
		}
	}
	
	private static void annotateClusterName(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, String name, Object subset, com.intergence.hgsrest.vmware.vmware.topology.Inventory entities) {
		ManagedObjectReference[] subsetMORs = ((ArrayOfManagedObjectReference)subset).getManagedObjectReference();
		if (subsetMORs != null) {
			for (ManagedObjectReference entity : subsetMORs) {
				String entityId = modelMapper.getVSphereID(entity);
				entities.setNiceAttribute(entityId, "Cluster", name);
			}
		}
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute("name");
		attributes.addAbsolute("host");
		attributes.addAbsolute("datastore");
		attributes.addAbsolute("network");
		return attributes;
	}
}
