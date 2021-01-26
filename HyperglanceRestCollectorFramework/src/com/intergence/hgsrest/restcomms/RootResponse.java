package com.intergence.hgsrest.restcomms;

import java.util.Arrays;

/**
 * Created by stephen on 19/02/2015.
 */
public class RootResponse {

    Version[] versions;


    @Override
    public String toString() {
        return "RootResponse{" +
                "versions=" + Arrays.toString(versions) +
                '}';
    }
}
