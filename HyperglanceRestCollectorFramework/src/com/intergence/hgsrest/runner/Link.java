package com.intergence.hgsrest.runner;

import java.util.Map;

/**
 * Created by stephen on 10/02/2015.
 */
public class Link extends SkeletalTopology {

    private String endpointAKey;
    private String endpointBKey;

    public Link(String key, String type, String endpointAKey, String endpointBKey, Map<String, String> attributes) {
	    super(key, type, attributes);

        this.endpointAKey = endpointAKey;
        this.endpointBKey = endpointBKey;
    }

	public String getEndpointAKey() {
		return endpointAKey;
	}

	public String getEndpointBKey() {
		return endpointBKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Link that = (Link) o;

		if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
		if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
		if (getEndpointAKey() != null ? !getEndpointAKey().equals(that.getEndpointAKey()) : that.getEndpointAKey() != null) return false;
		if (getEndpointBKey() != null ? !getEndpointBKey().equals(that.getEndpointBKey()) : that.getEndpointBKey() != null) return false;
		return !(getAttributes() != null ? !getAttributes().equals(that.getAttributes()) : that.getAttributes() != null);

	}

	@Override
	public int hashCode() {
		int result = getKey() != null ? getKey().hashCode() : 0;
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + (getEndpointAKey() != null ? getType().hashCode() : 0);
		result = 31 * result + (getEndpointBKey() != null ? getType().hashCode() : 0);
		result = 31 * result + (getAttributes() != null ? getAttributes().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Link{" +
				"key='" + getKey() + '\'' +
				", type='" + getType() + '\'' +
				", endpointAKey='" + getEndpointAKey() + '\'' +
				", endpointBKey='" + getEndpointBKey() + '\'' +
				", attributes=" + getAttributes() +
				'}';
	}
}
