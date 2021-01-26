/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualController;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualDeviceFileBackingInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer1BackingInfo;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualDiskRawDiskMappingVer1BackingInfo;
import com.vmware.vim25.VirtualDiskSparseVer1BackingInfo;
import com.vmware.vim25.VirtualDiskSparseVer2BackingInfo;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualHardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualMachineEndPointCollector {
	
	private static final String hardwareProperty = "config.hardware";
	private static final AttributeMap endpointAttributes = buildAttributes();
		
	public static AttributeMap getProperties() {
		return endpointAttributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory vms, String networksToNicsBindingName) {
		for (Entity vm : vms.allEntities()) {
			Map<String, List<String>> networksToNics = new HashMap<String, List<String>>();
			
			// attach the net2nics map directly to the vm
			vm.setAttribute(networksToNicsBindingName, networksToNics);
			
			VirtualHardware hardwareConfig = vm.getAttribute(hardwareProperty);
			if (hardwareConfig != null) {
				VirtualDevice[] devices = hardwareConfig.getDevice();
				if (devices != null) {
					Map<Integer, VirtualDevice> devicesByKey = new HashMap<Integer, VirtualDevice>();
					for (VirtualDevice device : devices) {
						devicesByKey.put(device.getKey(), device);
					}
					
					for (VirtualDevice device : devices) {
						persistIfNetworkAdapter(modelMapper, vm.getId(), device, networksToNics);
						persistIfVirtualDisk(modelMapper, vm, device, devicesByKey);
					}
				}
			}
		}
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(hardwareProperty);
		return attributes;
	}
	
	private static void persistIfNetworkAdapter(ModelMapper modelMapper, String vmId, VirtualDevice device, Map<String, List<String>> networksToNics) {
		if (device instanceof VirtualEthernetCard) {
			
			String macAddress = ((VirtualEthernetCard)device).getMacAddress();
			if (macAddress != null) {
				Map<String, String> nicAttributes = new HashMap<String, String>();
				nicAttributes.put("Physical Address", macAddress);
				modelMapper.addEndPoint(vmId, macAddress, nicAttributes);
			}
			
			VirtualDeviceBackingInfo backingInfo = device.getBacking();
			if (backingInfo != null) {
				if (backingInfo instanceof VirtualEthernetCardNetworkBackingInfo) {
					ManagedObjectReference connectedNetwork = ((VirtualEthernetCardNetworkBackingInfo)backingInfo).getNetwork();
					if (connectedNetwork != null) {
						String networkId = modelMapper.getVSphereID(connectedNetwork);
						
						List<String> nics = networksToNics.get(networkId);
						if (nics == null) {
							nics = new ArrayList<String>();
							networksToNics.put(networkId, nics);
						}
						nics.add(macAddress); // it's ok that the macAddress might be null
					}
				}
				else if (backingInfo instanceof VirtualEthernetCardDistributedVirtualPortBackingInfo) {
					DistributedVirtualSwitchPortConnection portConn = ((VirtualEthernetCardDistributedVirtualPortBackingInfo)backingInfo).getPort();
					if (portConn != null) {
						// TODO implement VM<->DVPortgroup linking INMS-463
					}
				}
			}
		}
	}
	
	private static void persistIfVirtualDisk(ModelMapper modelMapper, Entity vm, VirtualDevice device, Map<Integer, VirtualDevice> devicesByKey) {
		if (device instanceof VirtualDisk) {
			VirtualDisk disk = (VirtualDisk)device;
			
			VirtualDeviceBackingInfo backingInfo = device.getBacking();
			if (backingInfo != null && backingInfo instanceof VirtualDeviceFileBackingInfo) {
				ManagedObjectReference datastore = ((VirtualDeviceFileBackingInfo)backingInfo).getDatastore();
				String datastoreId = modelMapper.getVSphereID(datastore);
				String diskId = VSphereHelper.getNestedEntityId(String.valueOf(device.getKey()), null, vm.getId());
				
				Map<String, String> diskAttributes = new HashMap<String, String>();
				diskAttributes.put("Capacity (KB)", String.valueOf(disk.getCapacityInKB()));
				
				if (backingInfo instanceof VirtualDiskFlatVer1BackingInfo) {
					VirtualDiskFlatVer1BackingInfo diskInfo = (VirtualDiskFlatVer1BackingInfo)backingInfo;
					putBasicBackingDetails(diskAttributes, "flat", 1, diskInfo.getSplit(), null);
				}
				else if (backingInfo instanceof VirtualDiskFlatVer2BackingInfo) {
					VirtualDiskFlatVer2BackingInfo diskInfo = (VirtualDiskFlatVer2BackingInfo)backingInfo;
					putBasicBackingDetails(diskAttributes, "flat", 2, diskInfo.getSplit(), diskInfo.getThinProvisioned());
				}
				else if (backingInfo instanceof VirtualDiskSparseVer1BackingInfo) {
					VirtualDiskSparseVer1BackingInfo diskInfo = (VirtualDiskSparseVer1BackingInfo)backingInfo;
					putBasicBackingDetails(diskAttributes, "sparse", 1, diskInfo.getSplit(), null);
				}
				else if (backingInfo instanceof VirtualDiskSparseVer2BackingInfo) {
					VirtualDiskSparseVer2BackingInfo diskInfo = (VirtualDiskSparseVer2BackingInfo)backingInfo;
					putBasicBackingDetails(diskAttributes, "sparse", 2, diskInfo.getSplit(), null);
				}
				else if (backingInfo instanceof VirtualDiskRawDiskMappingVer1BackingInfo) {
					//VirtualDiskRawDiskMappingVer1BackingInfo diskInfo = (VirtualDiskRawDiskMappingVer1BackingInfo)backingInfo;
					putBasicBackingDetails(diskAttributes, "raw mapping", 1, null, null);
				}
				
				String virtualDeviceNodeId = null;
				
				VirtualController controller = (VirtualController)devicesByKey.get(disk.getControllerKey());
				if (controller != null) {
					Map<String, String> adapterAttributes = new HashMap<String, String>();

					String controllerLabel = controller.getDeviceInfo() == null ? "unknown controller" : controller.getDeviceInfo().getLabel();
					String controllerType = controllerLabel.substring(0, controllerLabel.indexOf(' '));
					String unitNumber = disk.getUnitNumber() == null ? "?" : disk.getUnitNumber().toString();
					virtualDeviceNodeId = controllerType + "(" + controller.getBusNumber() + ":" + unitNumber + ")";
	
					adapterAttributes.put("Controller", controllerLabel);
					adapterAttributes.put("Bus number", String.valueOf(controller.getBusNumber()));
					adapterAttributes.put("Unit number", unitNumber);
					adapterAttributes.put("Virtual Device Node", virtualDeviceNodeId);
					
					modelMapper.addEndPoint(vm.getId(), virtualDeviceNodeId, adapterAttributes);
				}
				
				modelMapper.addEndPoint(datastoreId, diskId, diskAttributes);
				modelMapper.addLink(vm.getId(), virtualDeviceNodeId, datastoreId, diskId, false);
			}
		}
	}
	
	private static void putBasicBackingDetails(Map<String, String> attributes, String backingType, Integer version, Boolean split, Boolean thinProvisioned) {
		if (backingType != null) {
			attributes.put("Backing type", backingType);
		}
		if (version != null) {
			attributes.put("Backing type version", "version " + version.toString());
		}
		if (split != null) {
			attributes.put("File type", split ? "split" : "monolithic");
		}
		if (thinProvisioned != null) {
			attributes.put("Provisioning", thinProvisioned ? "thin" : "thick");
		}
	}
}
