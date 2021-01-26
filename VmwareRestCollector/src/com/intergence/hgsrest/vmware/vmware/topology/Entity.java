/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private String id;
	private Map<String, Object> attributes;
	private Map<String, String> niceAttributes = new HashMap<String, String>();
	
	public Entity(String id, Map<String, Object> attributes) {
		this.id = id;
		this.attributes = attributes;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public Map<String, String> getNiceAttributes() {
		return niceAttributes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String attributeName) {
		return (T)attributes.get(attributeName);
	}
	
	public void setAttribute(String attributeName, Object value) {
		attributes.put(attributeName, value);
	}
	
	public void setNiceAttribute(String attributeName, String value) {
		niceAttributes.put(attributeName, value);
	}
	
	public String getNiceAttribute(String attributeName) {
		return niceAttributes.get(attributeName);
	}
}
