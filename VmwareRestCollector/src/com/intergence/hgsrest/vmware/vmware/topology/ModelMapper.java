/**
 * Copyright 2011 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.collection.NodeCollectorConstants;
import com.intergence.hgsrest.model.enumeration.NodeTypeEnum;
import com.intergence.hgsrest.vmware.vmware.VSphereWrapper;
import com.intergence.hgsrest.vmware.vmware.VmWareRepositoryUpdater;
import com.vmware.vim25.ManagedObjectReference;

import java.util.HashMap;
import java.util.Map;

public class ModelMapper {
	private VmWareRepositoryUpdater updater;
	private VSphereWrapper service;
	private String vsphereHostNameOrIp; 
	
	public ModelMapper(VSphereWrapper service, VmWareRepositoryUpdater updater) {
		this.updater = updater;
		this.service = service;
		vsphereHostNameOrIp = service.getSessionWrapper().getSessionCredentials().getHostNameOrIp();
	}
	
	public String getVSphereID(ManagedObjectReference entity) {
		return service.getEntityId(entity);
	}
	
	public void addNode(String id, NodeTypeEnum type, Map<String, String> attributes) {
		addAdditionalNodeAttributes(attributes, type);
		decodeNameAttribute(attributes);
		updater.addNode(id, type, attributes);
	}
	
	public void addNode(Entity entity, NodeTypeEnum type) {
		addNode(entity.getId(), type, entity.getNiceAttributes());
	}
	
	public void addNode(Entity entity, NodeTypeEnum type, AttributeMap attributesMap) {
		attributesMap.collectFromEntity(entity);
		addNode(entity, type);
	}
	
	public void addNodes(Inventory entities, NodeTypeEnum type, AttributeMap attributesMap) {
		for (Entity entity : entities.allEntities()) {
			addNode(entity, type, attributesMap);
		}
	}
	
	private Map<String, String> addAdditionalNodeAttributes(Map<String, String> attributes, NodeTypeEnum type) {
		attributes = addAdditionalSharedAttributes(attributes);
		attributes.put("vmware type", type.toString());
		if (type != NodeTypeEnum.SERVER) {
			attributes.put("virtual", "true");
		}
		return attributes;
	}
	
	private Map<String, String> decodeNameAttribute(Map<String, String> attributes) {
		// VSphere docs say that entity names will percent encode the following 3 characters: %, / and \
		String name = attributes.get(NodeCollectorConstants.ATTRIBUTE_NAME);
		if (name != null) {
			name = name.replace("%25", "%");
			name = name.replace("%2f", "/");
			name = name.replace("%2F", "/");
			name = name.replace("%5c", "\\");
			name = name.replace("%5C", "\\");
			attributes.put(NodeCollectorConstants.ATTRIBUTE_NAME, name);
		}
		return attributes;
	}
	
	private Map<String, String> addAdditionalSharedAttributes(Map<String, String> attributes) {
		attributes.put("vSphere Instance", vsphereHostNameOrIp);
		return attributes;
	}

	public void addEndPoint(String vSphereAttachmentNodeId, String vSphereEndPointId, Map<String, String> attributes) {
		attributes = addAdditionalSharedAttributes(attributes);
		updater.addEndPoint(vSphereAttachmentNodeId, vSphereEndPointId, attributes);
	}
	
	public void addEndPoint(String vSphereAttachmentNodeId, String vSphereEndPointId) {
		addEndPoint(vSphereAttachmentNodeId, vSphereEndPointId, new HashMap<String, String>());
	}

	public void addLink(String vSphereNodeAId, String vSphereNodeAEndPointId, String vSphereNodeBId, String vSphereNodeBEndPointId, boolean isLogical) {
		Map<String, String> sharedEndPointAttributes = addAdditionalSharedAttributes(new HashMap<String, String>());
		updater.addLink(vSphereNodeAId, vSphereNodeAEndPointId, vSphereNodeBId, vSphereNodeBEndPointId, isLogical, sharedEndPointAttributes);
	}
}
