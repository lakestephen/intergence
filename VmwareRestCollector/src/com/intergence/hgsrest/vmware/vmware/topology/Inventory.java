/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import com.intergence.hgsrest.vmware.vmware.VSphereWrapper;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ObjectContent;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
	private static Logger logger = Logger.getLogger(Inventory.class);
	private Map<String, Entity> entities = new HashMap<String, Entity>();

	public Entity addEntity(String entityId) {
		Entity entity = new Entity(entityId, new HashMap<String, Object>());
		entities.put(entityId, entity);
		return entity;
	}
	
	public Entity get(String id) {
		return entities.get(id);
	}
	
	public Collection<Entity> allEntities() {
		return entities.values();
	}
	
	public <T> T getAttribute(String entityId, String attributeName) {
		Entity entity = get(entityId);
		return entity == null ? null : entity.<T>getAttribute(attributeName);
	}
	
	public void setAttribute(String entityId, String attributeName, Object value) {
		Entity entity = get(entityId);
		if (entity != null) {
			entity.setAttribute(attributeName, value);
		}
	}
	
	public String getNiceAttribute(String entityId, String attributeName) {
		Entity entity = get(entityId);
		return entity == null ? null : entity.getNiceAttribute(attributeName);
	}
	
	public void setNiceAttribute(String entityId, String attributeName, String value) {
		Entity entity = get(entityId);
		if (entity != null) {
			entity.setNiceAttribute(attributeName, value);
		}
		else {
			logger.warn("Entity assumed to exist was infact not found!");
		}
	}
	
	public static Inventory parse(VSphereWrapper service, ObjectContent[] entities) {
		Inventory inventory = new Inventory();
		for (ObjectContent entityContents : entities) {
			Entity entity = inventory.addEntity( service.getEntityId(entityContents.getObj()) );
			
			DynamicProperty[] properties = entityContents.getPropSet();
			if (properties != null) {
				for (DynamicProperty property : properties) {
					entity.setAttribute(property.getName(), property.getVal());
				}
			}
		}
		
		return inventory;
	}
}
