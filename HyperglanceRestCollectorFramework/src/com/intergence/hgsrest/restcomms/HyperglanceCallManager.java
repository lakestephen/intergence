package com.intergence.hgsrest.restcomms;

import com.intergence.hgsrest.runner.Topology;

import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface HyperglanceCallManager {

    void getTopology() throws IOException;
    void replaceTopology(Topology topology) throws IOException;
}
