/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware;

import com.intergence.hgsrest.vmware.vmware.topology.Entity;
import com.intergence.hgsrest.vmware.vmware.topology.Inventory;
import com.intergence.hgsrest.vmware.vmware.topology.ModelMapper;
import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ServiceContent;


public class VSphereHelper {
	public static ManagedObjectReference ensureNotNull(ManagedObjectReference mob, String mobName) {
		if (mob == null) {
			throw new NullPointerException("The ManagedObjectReference for a/an/the " + mobName +
			                               " was unexpectedly found to be null.");
		}	
		return mob;
	}
	
	public static void validateEssentialServices(ServiceContent content) {
		ensureNotNull(content.getViewManager(), "View Manager");
		ensureNotNull(content.getPerfManager(), "Performance Manager");
		ensureNotNull(content.getRootFolder(), "Root Folder");
		ensureNotNull(content.getPropertyCollector(), "Property Collector");
	}
	
	public static String getKeyId(String key, String source) {
		return key.replace("key-vim.host." + source + "-", "");
	}
	
	public static String getNestedEntityId(String key, String source, String outerId) {
		return (source != null ? getKeyId(key, source) : key) + "<-" + outerId;
	}
	
	public static void linkToManagedObjectReferences(ModelMapper modelMapper, Entity entity, String otherEntityProperty, Inventory otherEntities) {
		Object other = entity.getAttribute(otherEntityProperty);
		if (other == null) { return; }
		if (other instanceof ArrayOfManagedObjectReference) {
			ManagedObjectReference[] entities =
				((ArrayOfManagedObjectReference)other).getManagedObjectReference();
			if (entities != null) {
				for (ManagedObjectReference mor : entities) {
					String vsphereId = modelMapper.getVSphereID(mor);
					if (otherEntities.get(vsphereId) != null) {
						modelMapper.addLink(entity.getId(), null, vsphereId, null, true);
					}
				}
			}
		} else if (other instanceof ManagedObjectReference) {
			String vsphereId = modelMapper.getVSphereID((ManagedObjectReference)other);
			if (otherEntities.get(vsphereId) != null) {
				modelMapper.addLink(entity.getId(), null, vsphereId, null, true);
			}
		}
	}
	
	public static Inventory getInventoryView(VSphereWrapper service, String type, String[] properties) {
		ObjectContent[] results = service.queryEntityProperties(type, properties);
		return Inventory.parse(service, results);
	}
}
