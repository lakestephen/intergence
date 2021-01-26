package com.intergence.hgsrest.restcomms;

import java.io.IOException;

/**
 * Created by stephen on 19/02/2015.
 */
public interface JsonComms {

    String put(String fullCallUrl, String authorisationKey, String json) throws IOException;

    String get(String fullCallUrl, String authorisationKey) throws IOException;

}
