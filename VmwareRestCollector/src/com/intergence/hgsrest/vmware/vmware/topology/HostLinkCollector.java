/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.vmware.vim25.ArrayOfHostScsiTopologyInterface;
import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ArrayOfScsiLun;
import com.vmware.vim25.HostScsiTopologyInterface;
import com.vmware.vim25.HostScsiTopologyLun;
import com.vmware.vim25.HostScsiTopologyTarget;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ScsiLun;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HostLinkCollector {
	private static final String scsiTopologyProperty = "config.storageDevice.scsiTopology.adapter";
	private static final String lunProperty = "config.storageDevice.scsiLun";
	private static final AttributeMap linkAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return linkAttributes;
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(scsiTopologyProperty);
		attributes.addAbsolute(lunProperty);
		attributes.addAbsolute("vm");
		attributes.addAbsolute("datastore");
		return attributes;
	}

	public static void persist(ModelMapper modelMapper, Inventory hosts, Inventory datastores, Inventory vms, VSwitchMappings vSwitchMappings) {
		for (Entity host : hosts.allEntities()) {
			persistInventoryLinks(host, datastores, vms, modelMapper);
			persistVSwitchLinks(host, vSwitchMappings, modelMapper);
			persistScsiDeviceLinks(host, modelMapper);
		}
	}
	
	private static void persistInventoryLinks(Entity host, Inventory datastores, Inventory vms, ModelMapper modelMapper) {
		VSphereHelper.linkToManagedObjectReferences(modelMapper, host, "vm", vms);
		
		ArrayOfManagedObjectReference dsArrayProxy = host.getAttribute("datastore");
		ManagedObjectReference[] linkedDatastores = dsArrayProxy.getManagedObjectReference();
		Map<String, String> scsiDisksToDatastores = new HashMap<String, String>();
		host.setAttribute("scsiDisksToDatastores", scsiDisksToDatastores);
		if (linkedDatastores != null) {
			for (ManagedObjectReference dsMor : linkedDatastores) {
				String dsId = modelMapper.getVSphereID(dsMor);
				Entity datastore = datastores.get(dsId);
				if (datastore != null) {
					Set<String> diskNames = datastore.getAttribute("diskNames");
					if (diskNames != null) {
						for (String disk : diskNames) {
							scsiDisksToDatastores.put(disk, dsId);
						}
					}
				}
			}
		}
	}
	
	private static void persistVSwitchLinks(Entity host, VSwitchMappings vSwitchMappings, ModelMapper modelMapper) {
		HostVirtualSwitch[] vSwitches = vSwitchMappings.getVSwitchesInHost(host);
		for (HostVirtualSwitch vSwitch : vSwitches) {
			String vSwitchId = VSphereHelper.getNestedEntityId(vSwitch.getKey(), "VirtualSwitch", host.getId());
			String[] pnics = vSwitch.getPnic();
			if (pnics != null) {
				for (String pnic : pnics) {
					pnic = VSphereHelper.getKeyId(pnic, "PhysicalNic");
					modelMapper.addLink(host.getId(), pnic, vSwitchId, null, false);
				}
			}
			else {
				modelMapper.addLink(host.getId(), null, vSwitchId, null, true); // configured on but not otherwise connected to the host
			}
		}
	}

	private static void persistScsiDeviceLinks(Entity host, ModelMapper modelMapper) {
		ArrayOfHostScsiTopologyInterface scsiTopologyProxy = host.getAttribute(scsiTopologyProperty);
		if (scsiTopologyProxy != null) {
			HostScsiTopologyInterface[] scsiTopology = scsiTopologyProxy.getHostScsiTopologyInterface();
			if (scsiTopology != null) {
				Map<String, String> keysToNames = mapLunKeysToNames(host);
				if (keysToNames != null) {
					Map<String, String> scsiDisksToDatastores = host.getAttribute("scsiDisksToDatastores");
					for (HostScsiTopologyInterface scsiInterface : scsiTopology) {
						String hbaId = VSphereHelper.getKeyId(scsiInterface.getKey(), "ScsiTopology.Interface");
						
						HostScsiTopologyTarget[] targets = scsiInterface.getTarget();
						if (targets != null) {
							for (HostScsiTopologyTarget target : targets) {
								HostScsiTopologyLun[] luns = target.getLun();
								if (luns != null) {
									for (HostScsiTopologyLun lun : luns) {
										String lunKey = lun.getScsiLun();
										String name = keysToNames.get(lunKey);
										String datastoreId = scsiDisksToDatastores.get(name);
										if (datastoreId != null) {
											modelMapper.addLink(host.getId(), hbaId, datastoreId, name, false);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static Map<String, String> mapLunKeysToNames(Entity host) {
		Map<String, String> keysToNames = null;
		
		ArrayOfScsiLun scsiLunsProxy = host.getAttribute(lunProperty);
		if (scsiLunsProxy != null) {
			ScsiLun scsiLuns[] = scsiLunsProxy.getScsiLun();
			if (scsiLuns != null) {
				keysToNames = new HashMap<String, String>();
				for (ScsiLun lun : scsiLuns) {
					if (lun.getKey() != null) {
						keysToNames.put(lun.getKey(), lun.getCanonicalName());
					}
				}
				
			}
		}
		
		return keysToNames;
	}
}
