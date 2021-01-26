/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;
import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.vmware.vim25.HostVirtualSwitch;

import java.util.HashMap;
import java.util.Map;

public class VSwitchNestedNodeCollector {	
	public static AttributeMap getPropertiesOnHost() {
		return VSwitchMappings.getPropertiesOnHost();
	}
	
	public static void persist(ModelMapper modelMapper, VSwitchMappings vSwitchMappings, Inventory hosts) {
		for (Entity host : hosts.allEntities()) {
			HostVirtualSwitch[] vSwitches = vSwitchMappings.getVSwitchesInHost(host);
			for (HostVirtualSwitch vSwitch : vSwitches) {
				String switchId = VSphereHelper.getNestedEntityId(vSwitch.getKey(), "VirtualSwitch", host.getId());
				modelMapper.addNode(switchId, NodeTypeEnum.SWITCH, getAttributes(vSwitch, host));
			}
		}
	}
	
	private static Map<String, String> getAttributes(HostVirtualSwitch vSwitch, Entity host) {
		Map<String, String> attributes = new HashMap<String, String>();
		
		attributes.put(NodeCollectorConstants.ATTRIBUTE_NAME, vSwitch.getName());
		attributes.put("ownerId", host.getId());
		attributes.put("Cluster", host.getNiceAttribute("Cluster"));
		
		if (vSwitch.getMtu() != null) {
			attributes.put("MTU", String.valueOf(vSwitch.getMtu()));
		}
		
		return attributes;
	}
}
