/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;


import com.intergence.hgsrest.vmware.vmware.VSphereHelper;

public class DVSwitchLinkCollector {
	private static AttributeMap linkAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return linkAttributes;
	}
	
	public static void persist(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, com.intergence.hgsrest.vmware.vmware.topology.Inventory dvss, com.intergence.hgsrest.vmware.vmware.topology.Inventory hosts, com.intergence.hgsrest.vmware.vmware.topology.Inventory networks, com.intergence.hgsrest.vmware.vmware.topology.Inventory vms) {
		for (Entity dvs : dvss.allEntities()) {
			VSphereHelper.linkToManagedObjectReferences(modelMapper, dvs, "summary.host", hosts);
			VSphereHelper.linkToManagedObjectReferences(modelMapper, dvs, "summary.vm", vms); // TODO remove this once VMs link to DVPortgroups INMS-463
			VSphereHelper.linkToManagedObjectReferences(modelMapper, dvs, "portgroup", networks);
			
		}
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute("summary.host");
		attributes.addAbsolute("summary.vm");
		attributes.addAbsolute("portgroup");
		return attributes;
	}
}
