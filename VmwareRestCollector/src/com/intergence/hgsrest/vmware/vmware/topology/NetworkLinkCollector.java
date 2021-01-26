package com.intergence.hgsrest.vmware.vmware.topology;
/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property
 * belonging to Real-Status Ltd and its licensors.
 */

import java.util.List;

public class NetworkLinkCollector {
	
	public static AttributeMap getProperties() {
		return new AttributeMap();
	}
	
	public static void persist(ModelMapper modelMapper, Inventory networks, VSwitchMappings vSwitchMappings) {
		for (Entity network : networks.allEntities()) {
			List<String> switchIds = vSwitchMappings.getSwitchIdsInNetwork(network);
			for (String vSwitch : switchIds) {
				modelMapper.addLink(network.getId(), null, vSwitch, null, true);
			}
		}
	}
}
