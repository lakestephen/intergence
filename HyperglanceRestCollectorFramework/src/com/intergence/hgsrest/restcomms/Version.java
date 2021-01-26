package com.intergence.hgsrest.restcomms;

import java.util.Arrays;

/**
 * Created by stephen on 19/02/2015.
 */
public class Version {

    private String id;
    private String status;
    private String path;
    private String[] documentation;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String[] getDocumentation() {
        return documentation;
    }

    @Override
    public String toString() {
        return "Version{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", path='" + path + '\'' +
                ", documentation=" + Arrays.toString(documentation) +
                '}';
    }
}
