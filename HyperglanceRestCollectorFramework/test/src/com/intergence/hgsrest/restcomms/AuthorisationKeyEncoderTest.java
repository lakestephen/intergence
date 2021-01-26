package com.intergence.hgsrest.restcomms;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AuthorisationKeyEncoderTest {


    @Test
    public void CalculatesAPIKetToSpec() throws IOException {

        AuthorisationKeyEncoder authorisationKeyEncoder = new AuthorisationKeyEncoder();

        String encodedKey = authorisationKeyEncoder.calculateAuthorisationKey("AcmeApp", "a15f3a32-77f3-4784-b10c-ff8b0587495b");

        assertEquals("Basic QWNtZUFwcDphMTVmM2EzMi03N2YzLTQ3ODQtYjEwYy1mZjhiMDU4NzQ5NWI=", encodedKey);
    }
}