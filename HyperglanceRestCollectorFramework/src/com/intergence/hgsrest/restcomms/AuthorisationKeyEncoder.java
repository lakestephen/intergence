package com.intergence.hgsrest.restcomms;

import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 19/02/2015.
 */
public class AuthorisationKeyEncoder {

    private Logger log = Logger.getLogger(this.getClass());

    String calculateAuthorisationKey(String hyperglanceDatasourceName, String hyperglanceApiKey) {

        checkNotNull(hyperglanceApiKey, "Set hyperglanceApiKey before calling init()");
        checkNotNull(hyperglanceDatasourceName, "Set hyperglanceDatasourceName before calling init()");

        String keyForEncoding = hyperglanceDatasourceName + ":" + hyperglanceApiKey;

        BASE64Encoder encoder = new BASE64Encoder();

        String encodedKey = "Basic " + encoder.encode(keyForEncoding.getBytes());

        log.info("Calculated authorisation key [" + encodedKey + "]");

        return encodedKey;
    }
}
