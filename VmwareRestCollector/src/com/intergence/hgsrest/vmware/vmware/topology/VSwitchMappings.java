/**
 * Copyright 2011 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.vmware.vim25.ArrayOfHostVirtualSwitch;
import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VSwitchMappings {
	private static final String VSWITCH_PATH_THROUGH_HOST = "config.network.vswitch";
	private static final String NETWORK_PATH_THROUGH_HOST = "network";
	
	private static final String VSWITCH_BINDING = "vSwitches";
	private static final String NETWORK_SWITCHES_BINDING = "networksSwitchIds";
	
	private static AttributeMap attributesOnHost = buildAttributes();
	
	public static AttributeMap getPropertiesOnHost() {
		return attributesOnHost;
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(VSWITCH_PATH_THROUGH_HOST);
		attributes.addAbsolute(NETWORK_PATH_THROUGH_HOST);
		return attributes;
	}
	
	public VSwitchMappings(ModelMapper modelMapper, Inventory hosts, Inventory vms, Inventory networks) {
		for (Entity host : hosts.allEntities()) {			
			ArrayOfHostVirtualSwitch vSwitchArrayProxy = host.getAttribute(VSWITCH_PATH_THROUGH_HOST);
			if (vSwitchArrayProxy == null) { continue; }
			
			HostVirtualSwitch[] vSwitches = vSwitchArrayProxy.getHostVirtualSwitch();
			if (vSwitches == null) { continue; }
			
			// re-seat the attribute to the unboxed array
			host.setAttribute(VSWITCH_BINDING, vSwitches);
			
			// map portgroup/network names to the switches that connect to them
			Map<String, String> portgroupsToSwitches = mapPortgroupsToSwitchesWithinHost(vSwitches, host.getId());
			bindSwitchIdsToNetworks(modelMapper, host, networks, portgroupsToSwitches);
		}
	}
	
	public HostVirtualSwitch[] getVSwitchesInHost(Entity host) {
		HostVirtualSwitch[] result = host.getAttribute(VSWITCH_BINDING);
		return result == null ? new HostVirtualSwitch[0] : result;
	}
	
	public List<String> getSwitchIdsInNetwork(Entity network) {
		List<String> result = network.getAttribute(NETWORK_SWITCHES_BINDING);
		return result == null ? new ArrayList<String>() : result;
	}

	private static void bindSwitchIdsToNetworks(ModelMapper modelMapper, Entity host, Inventory networks, Map<String, String> portgroupsToSwitches) {
		ArrayOfManagedObjectReference networkArrayProxy = host.getAttribute(NETWORK_PATH_THROUGH_HOST);
		ManagedObjectReference[] networkMors = networkArrayProxy.getManagedObjectReference();
		if (networkMors != null) {
			for (ManagedObjectReference networkMor : networkMors) {
				String networkId = modelMapper.getVSphereID(networkMor);
				Entity network = networks.get(networkId);
				if (network == null) { continue; }
				
				List<String> switchIds = network.getAttribute(NETWORK_SWITCHES_BINDING);
				if (switchIds == null) {
					switchIds = new ArrayList<String>();
					network.setAttribute(NETWORK_SWITCHES_BINDING, switchIds);
				}
				String networkName = network.getAttribute("name");
				String switchId = portgroupsToSwitches.get(networkName);
				if (switchId != null) {
					switchIds.add(switchId);
				}
			}
		}
	}
	
	private static Map<String, String> mapPortgroupsToSwitchesWithinHost(HostVirtualSwitch[] switchesOnHost, String hostId) {
		Map<String, String> portgroupsToSwitches = new HashMap<String, String>();
		
		for (HostVirtualSwitch vSwitch : switchesOnHost) {
			String[] portgroups = vSwitch.getPortgroup();
			if (portgroups != null) {
				for (String portgroup : portgroups) {
					portgroup = VSphereHelper.getKeyId(portgroup, "PortGroup");
					String vSwitchId = VSphereHelper.getNestedEntityId(vSwitch.getKey(), "VirtualSwitch", hostId);
					portgroupsToSwitches.put(portgroup, vSwitchId);
				}
			}
		}

		return portgroupsToSwitches;
	}
}
