/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;
import com.vmware.vim25.ArrayOfGuestDiskInfo;
import com.vmware.vim25.GuestDiskInfo;
import com.vmware.vim25.ManagedObjectReference;

public class VirtualMachineNodeCollector {
	private static final AttributeMap nodeAttributes = buildAttributeMap();
	private static final String HOST_ATTRIBUTE = "summary.runtime.host";
	private static final String GUEST_DISK_ATTRIBUTE = "guest.disk";
		
	public static AttributeMap getProperties() {
		return nodeAttributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory vms, Inventory hosts) {
		for (Entity vm : vms.allEntities()) {
			addHostAsOwner(modelMapper, vm);
			addGuestDiskInfo(vm);
			modelMapper.addNode(vm, NodeTypeEnum.OTHER, nodeAttributes);
		}
	}
	
	private static AttributeMap buildAttributeMap() {
		AttributeMap attributes = new AttributeMap();
		
		addBasicAttributes(attributes);
		addRuntimeSummary(attributes);
		addGuestSummary(attributes);
		addConfigSummary(attributes);
		attributes.mapAbsolute("summary.overallStatus", "traffic-light status");
		
		attributes.addAbsolute(HOST_ATTRIBUTE);
		attributes.addAbsolute(GUEST_DISK_ATTRIBUTE);
		
		return attributes;
	}
	
	private static void addBasicAttributes(AttributeMap attributes) {
		attributes.mapAbsolute("guest.ipAddress", NodeCollectorConstants.ATTRIBUTE_IPADDRESS);
		attributes.mapAbsolute("config.name", NodeCollectorConstants.ATTRIBUTE_NAME);
		
	}
	
	private static void addRuntimeSummary(AttributeMap attributes) {
		attributes.setRoot("summary.runtime");
		attributes.map("bootTime", "boot time");
		attributes.map("cleanPowerOff", "shutdown cleanly", 4.0, null);
		attributes.map("connectionState", "connection state");
		attributes.map("powerState", "power state");
	}
	
	private static void addGuestSummary(AttributeMap attributes) {
		attributes.setRoot("guest");
		attributes.map("guestFullName", "guest OS");
		attributes.map("toolsStatus", "VmWare tools status");
		attributes.map("guestState", "guest status");
	}
	
	private static void addConfigSummary(AttributeMap attributes) {
		attributes.setRoot("summary.config");
		attributes.map("memorySizeMB", "memory size (MB)");
		attributes.map("numCpu", "CPU count");
		attributes.map("numEthernetCards", "ethernet-card count");
		attributes.map("numVirtualDisks", "virtual disk count");
		attributes.map("template", "VM is a template");
	}
	
	private static void addHostAsOwner(ModelMapper modelMapper, Entity vm) {
		ManagedObjectReference hostMor = vm.getAttribute(HOST_ATTRIBUTE);
		if (hostMor != null) {
			String hostId = modelMapper.getVSphereID(hostMor);
			vm.setNiceAttribute("ownerId", hostId);
		}
	}
	
	private static void addGuestDiskInfo(Entity vm) {
		ArrayOfGuestDiskInfo diskInfoProxy = vm.getAttribute(GUEST_DISK_ATTRIBUTE);
		if (diskInfoProxy != null) {
			GuestDiskInfo[] diskInfos = diskInfoProxy.getGuestDiskInfo();
			if (diskInfos != null) {
				for (GuestDiskInfo diskInfo : diskInfos) {
					String path = diskInfo.getDiskPath();
					if (path != null) {
						if (diskInfo.getCapacity() != null) {
							vm.setNiceAttribute("Disk ( " + path + " ) capacity", String.valueOf(diskInfo.getCapacity()));
						}
						if (diskInfo.getFreeSpace() != null) {
							vm.setNiceAttribute("Disk ( " + path + " ) free space", String.valueOf(diskInfo.getFreeSpace()));
						}
					}
				}
			}
		}
	}
}
