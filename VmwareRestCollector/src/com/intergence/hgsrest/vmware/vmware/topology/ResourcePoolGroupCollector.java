/**
 * Copyright 2011 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ManagedObjectReference;

import java.util.ArrayList;
import java.util.List;

public class ResourcePoolGroupCollector {
	private static final AttributeMap resourcePoolAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return resourcePoolAttributes;
	}
	
	public static void collect(ModelMapper modelMapper, Inventory vms, Inventory resourcePools, Inventory clusters) {
		List<Entity> leaves = new ArrayList<Entity>();
		for (Entity parent : resourcePools.allEntities()) {
			ArrayOfManagedObjectReference childrenArrayProxy = parent.getAttribute("resourcePool");
			ManagedObjectReference[] children = childrenArrayProxy.getManagedObjectReference();
				
			if (children == null || children.length == 0) {
				leaves.add(parent);
			}
			else {
				for (ManagedObjectReference child : children) {
					Entity childEntity = resourcePools.get(modelMapper.getVSphereID(child));
					if (childEntity != null) {
						childEntity.setAttribute("parentEntity", parent);
					}
				}
			}
		}
		
		for (Entity leaf : leaves) {
			buildPath(leaf);
		}
		
		for (Entity pool : resourcePools.allEntities()) {
			ArrayOfManagedObjectReference vmsArrayProxy = pool.getAttribute("vm");
			ManagedObjectReference[] containedVms = vmsArrayProxy.getManagedObjectReference();
			
			if (containedVms != null) {
				ManagedObjectReference clusterParent = pool.getAttribute("owner");
				
				if (clusterParent != null) {
					Entity cluster = clusters.get(modelMapper.getVSphereID(clusterParent));
					
					if (cluster != null) {
						for (ManagedObjectReference vmMOR : containedVms) {
							Entity vm = vms.get(modelMapper.getVSphereID(vmMOR));
							if (vm != null) {
								vm.setNiceAttribute("Resource Pool Path", (String)pool.getAttribute("path"));
								vm.setNiceAttribute("Cluster", (String)cluster.getAttribute("name"));
							}
						}
					}
				}
			}
		}
	}

	private static String buildPath(Entity pool) {
		String path = pool.getAttribute("name");
		
		Entity parent = pool.getAttribute("parentEntity");
		if (parent != null) {
			path = buildPath(parent) + "." + path;
		}
		
		pool.setAttribute("path", path);
		return path;
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute("name");
		attributes.addAbsolute("vm");
		attributes.addAbsolute("resourcePool");
		attributes.addAbsolute("owner");
		return attributes;
	}
}
