package com.intergence.hgsrest.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class to all topology definitions.
 *
 * @author Lake
 */
public abstract class SkeletalTopology {

	private final String key;
	private final String type;
	private final List<Attribute> attributes = new ArrayList<Attribute>();

	SkeletalTopology(String key, String type, Map<String, String> attributes) {
		this.key = checkNotNull(key);
		this.type = checkNotNull(type);

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			this.attributes.add(new Attribute(entry.getKey(), entry.getValue()));
		}
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
}
