/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ManagedObjectReference;

import java.util.List;
import java.util.Map;

public class VirtualMachineLinkCollector {
	private static final String networkProperty   = "network";
	private static final AttributeMap linkAttributes = buildAttributes();

	public static AttributeMap getProperties() {
		return linkAttributes;
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(networkProperty);
		return attributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory vms, Inventory networks, VSwitchMappings vSwitchMappings, String networksToNicsProperty) {		
		for (Entity vm : vms.allEntities()) {
			ArrayOfManagedObjectReference networksArrayProxy = vm.getAttribute(networkProperty);
			ManagedObjectReference[] networkMORs = networksArrayProxy.getManagedObjectReference();
			if (networkMORs != null) {
				
				Map<String, List<String>> networksToNics = vm.getAttribute(networksToNicsProperty);
				
				for (ManagedObjectReference network : networkMORs) {
					String networkId = modelMapper.getVSphereID(network);
					
					List<String> nics = networksToNics.get(networkId);
					if (nics != null) {
						for (String nic : nics) {									
							modelMapper.addLink(vm.getId(), nic, networkId, null, false);
						}
					}
				}
			}
		}
	}
}
