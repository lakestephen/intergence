/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.HostScsiDiskPartition;
import com.vmware.vim25.HostVmfsVolume;
import com.vmware.vim25.VmfsDatastoreInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatastoreEndPointCollector {
	private static final String infoProperty = "info";
	private static final AttributeMap endpointAttributes = buildAttributes();
	
	public static AttributeMap getProperties() {
		return endpointAttributes;
	}

	public static void persist(com.intergence.hgsrest.vmware.vmware.topology.ModelMapper modelMapper, com.intergence.hgsrest.vmware.vmware.topology.Inventory datastores) {
		for (Entity datastore : datastores.allEntities()) {
			DatastoreInfo info = datastore.getAttribute(infoProperty);
			if (info instanceof VmfsDatastoreInfo) {
				HostVmfsVolume volume = ((VmfsDatastoreInfo)info).getVmfs();
				if (volume != null) {
					Set<String> diskNames = new HashSet<String>();
					datastore.setAttribute("diskNames", diskNames);

					HostScsiDiskPartition[] extents = volume.getExtent();
					for (HostScsiDiskPartition disk : extents) {
						Map<String, String> attributes = new HashMap<String, String>();
						attributes.put("Disk", disk.getDiskName());
						modelMapper.addEndPoint(datastore.getId(), disk.getDiskName(), attributes);
						diskNames.add(disk.getDiskName());
					}
				}
			}
		}
	}
	
	private static AttributeMap buildAttributes() {
		AttributeMap attributes = new AttributeMap();
		attributes.addAbsolute(infoProperty);
		return attributes;
	}
}
