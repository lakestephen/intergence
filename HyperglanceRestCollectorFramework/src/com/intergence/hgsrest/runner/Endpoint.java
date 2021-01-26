package com.intergence.hgsrest.runner;

import java.util.Map;

/**
 * Created by stephen on 10/02/2015.
 */
public class Endpoint extends SkeletalTopology {

    private String nodeKey;

    public Endpoint(String key, String type, String nodeKey, Map<String, String> attributes) {
	    super(key, type, attributes);
        this.nodeKey = nodeKey;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint that = (Endpoint) o;

        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        if (getNodeKey() != null ? !getNodeKey().equals(that.getNodeKey()) : that.getNodeKey() != null) return false;
        return !(getAttributes() != null ? !getAttributes().equals(that.getAttributes()) : that.getAttributes() != null);

    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getNodeKey() != null ? getType().hashCode() : 0);
        result = 31 * result + (getAttributes() != null ? getAttributes().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "key='" + getKey() + '\'' +
                ", type='" + getType() + '\'' +
                ", nodeKey='" + getNodeKey() + '\'' +
                ", attributes=" + getAttributes() +
                '}';
    }
}
