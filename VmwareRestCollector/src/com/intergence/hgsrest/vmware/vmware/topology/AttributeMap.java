/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware.topology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeMap {
	private static class VersionedAttribute {
		public VersionedAttribute(String name, Double minVersion, Double maxVersion) {
			this.name = name;
			this.minVersion = minVersion;
			this.maxVersion = maxVersion;
		}
		
		public String name;
		public Double minVersion;
		public Double maxVersion;
	}
	
	
	private String root = "";
	private Map<String, VersionedAttribute> attributeMappings = new HashMap<String, VersionedAttribute>();
	private List<VersionedAttribute> basicAttributes = new ArrayList<VersionedAttribute>();
	
	
	public void map(String attributePath, String niceName, Double minVersion, Double maxVersion) {
		mapAbsolute(root + attributePath, niceName, minVersion, maxVersion);
	}
	
	public void map(String attributePath, String niceName) {
		map(attributePath, niceName, null, null);
	}
	
	public void map(String attributePath, Double minVersion, Double maxVersion) {
		map(attributePath, attributePath, minVersion, maxVersion);
	}
	
	public void map(String attributePath) {
		map(attributePath, null, null);
	}
	
	public void mapAbsolute(String attributePath, String niceName, Double minVersion, Double maxVersion) {
		attributeMappings.put(niceName, new VersionedAttribute(attributePath, minVersion, maxVersion));
	}
	
	public void mapAbsolute(String attributePath, String niceName) {
		mapAbsolute(attributePath, niceName, null, null);
	}
	
	public void add(String attributePath, Double minVersion, Double maxVersion) {
		addAbsolute(root + attributePath, minVersion, maxVersion);
	}
	
	public void add(String attributePath) {
		add(attributePath, null, null);
	}
	
	public void addAbsolute(String attributePath, Double minVersion, Double maxVersion) {
		basicAttributes.add(new VersionedAttribute(attributePath, minVersion, maxVersion));
	}
	
	public void addAbsolute(String attributePath) {
		addAbsolute(attributePath, null, null);
	}
	
	public void setRoot(String root) {
		if (root != null && !root.isEmpty()) {
			this.root = root + ".";
		}
		else {
			this.root = "";
		}
	}
	
	public Collection<String> getAttributePaths(double version) {
		List<String> paths = new ArrayList<String>();
		addAttributesForVersion(paths, attributeMappings.values(), version);
		addAttributesForVersion(paths, basicAttributes, version);
		return paths;
	}
	
	private void addAttributesForVersion(List<String> paths, Collection<VersionedAttribute> values, double version) {
		for (VersionedAttribute attribute : values) {
			if ((attribute.minVersion == null || attribute.minVersion < version) &&
				(attribute.maxVersion == null || attribute.maxVersion > version)) {
				paths.add(attribute.name);
			}
		}
	}
	
	public int size() {
		return attributeMappings.size();
	}
	
	public void collectFromEntity(Entity entity) {
		for (Map.Entry<String, VersionedAttribute> attribute : this.attributeMappings.entrySet()) {
			String niceName = attribute.getKey();
			VersionedAttribute versionedAttr = attribute.getValue();
			String value = getValue(entity.getAttribute(versionedAttr.name));
			if (value != null) {
				entity.setNiceAttribute(niceName, value);
			}
		}
	}
	
	// performs simple to-string conversions, for more complex conversions an AttributeMap is inappropriate to use
	private static <T> String getValue(T value) {
		if (value == null) { return null; }
		if (value instanceof Calendar) {
			Calendar calendar = (Calendar)value;
			return String.valueOf(calendar.getTimeInMillis());
		}
		return String.valueOf(value);
	}
}
