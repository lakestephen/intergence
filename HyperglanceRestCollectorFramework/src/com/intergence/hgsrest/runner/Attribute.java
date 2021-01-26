package com.intergence.hgsrest.runner;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 10/02/2015.
 */
public class Attribute {

    private final String name;
    private final String value;

    public Attribute(String name, String value) {
        this.name = checkNotNull(name);
        this.value = checkNotNull(value);
    }

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        if (getName() != null ? !getName().equals(attribute.getName()) : attribute.getName() != null) return false;
        return !(getValue() != null ? !getValue().equals(attribute.getValue()) : attribute.getValue() != null);

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                 name + "=" + value +
                "}";
    }
}
