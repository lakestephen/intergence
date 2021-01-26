/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;


import com.intergence.hgsrest.vmware.vmware.VSphereHelper;
import com.vmware.vim25.ArrayOfHostHostBusAdapter;
import com.vmware.vim25.ArrayOfHostVirtualNic;
import com.vmware.vim25.ArrayOfPhysicalNic;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.HostVirtualNic;
import com.vmware.vim25.HostVirtualNicSpec;
import com.vmware.vim25.PhysicalNic;

import java.util.HashMap;
import java.util.Map;

public class HostEndPointCollector {
	private static final String pnicProperty = "config.network.pnic";
	private static final String hbaProperty = "config.storageDevice.hostBusAdapter";
	private static final String consoleVnicProperty = "config.network.consoleVnic";
	private static final String vnicProperty = "config.network.vnic";
	private static final AttributeMap endpointAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return endpointAttributes;
	}
	
	public static void persist(ModelMapper modelMapper, Inventory hosts) {
		for (Entity host : hosts.allEntities()) {
			persistPnics(host, modelMapper);
			persistHostBusAdapter(host, modelMapper);
			persistVnics(host, modelMapper, consoleVnicProperty);
			persistVnics(host, modelMapper, vnicProperty);
		}
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(pnicProperty);
		attributes.addAbsolute(hbaProperty);
		attributes.addAbsolute(vnicProperty);
		attributes.addAbsolute(consoleVnicProperty);
		return attributes;
	}
	
	private static void persistVnics(Entity host, ModelMapper modelMapper, String propertyName) {
		ArrayOfHostVirtualNic vnicArrayProxy = host.getAttribute(propertyName);
		if (vnicArrayProxy != null) {
			HostVirtualNic[] vnics = vnicArrayProxy.getHostVirtualNic();
			if (vnics != null) {
				for (HostVirtualNic vnic : vnics) {
					HostVirtualNicSpec spec = vnic.getSpec();
					
					Map<String, String> attributes = new HashMap<String, String>();
					
					attributes.put("VNIC Device Name", vnic.getDevice());
					
					if (spec.getMac() != null) {
						attributes.put("Physical Address", spec.getMac());
					}
					
					if (spec.getIp() != null && spec.getIp().getIpAddress() != null) {
						attributes.put("IP Address", spec.getIp().getIpAddress());
					}
					
					String vnicId = VSphereHelper.getKeyId(vnic.getKey(), "VirtualNic");
					modelMapper.addEndPoint(host.getId(), vnicId, attributes);
				}
			}
		}
	}
	
	private static void persistPnics(Entity host, ModelMapper modelMapper) {
		ArrayOfPhysicalNic pnicArrayProxy = host.getAttribute(pnicProperty);
		if (pnicArrayProxy != null) {
			PhysicalNic[] pnics = pnicArrayProxy.getPhysicalNic();
			if (pnics != null) {
				for (PhysicalNic pnic : pnics) {
					String pnicId = VSphereHelper.getKeyId(pnic.getKey(), "PhysicalNic");
					modelMapper.addEndPoint(host.getId(), pnicId);
				}
			}
		}
	}

	private static void persistHostBusAdapter(Entity host, ModelMapper modelMapper) {
		ArrayOfHostHostBusAdapter hostBusAdaptersProxy = host.getAttribute(hbaProperty);
		if (hostBusAdaptersProxy != null) {
			HostHostBusAdapter[] hbas = hostBusAdaptersProxy.getHostHostBusAdapter();
			if (hbas != null) {
				Map<String, String> attributes = new HashMap<String, String>();
				for (HostHostBusAdapter hba : hbas) {
					attributes.clear();
					attributes.put("HBA Device Name", hba.getDevice());
					modelMapper.addEndPoint(host.getId(), hba.getDevice(), attributes);
				}
			}
		}
	}
}
