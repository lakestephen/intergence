package com.intergence.hgsrest.restcomms;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerisonPathGetterTest {

    private Logger log = Logger.getLogger(this.getClass());

    @Test
    public void standardResponseCalculatesCorrectPath() throws IOException {
        final String responseJson =
                "{\n" +
                "\"versions\": [\n" +
                "{\n" +
                "\"id\": \"1.0\",\n" +
                "\"status\": \"current\",\n" +
                "\"path\": \"/hgapi/v1/1.0\",\n" +
                "\"documentation\": [\"http://support.real-status.com/hgapi.pdf\"]\n" +
                "}\n" +
                "]\n" +
                "}";

        String pathToRequiredVersion = doGetAbsolutePath(responseJson);

        assertEquals("https://localhost:8080/hgapi/v1/1.0", pathToRequiredVersion);
    }

    @Test
    public void standardResponseCalculatesCorrectPathWithEndSlash() throws IOException {
        final String responseJson =
                "{\n" +
                        "\"versions\": [\n" +
                        "{\n" +
                        "\"id\": \"1.0\",\n" +
                        "\"status\": \"current\",\n" +
                        "\"path\": \"/hgapi/v1/1.0/\",\n" +
                        "\"documentation\": [\"http://support.real-status.com/hgapi.pdf\"]\n" +
                        "}\n" +
                        "]\n" +
                        "}";

        String pathToRequiredVersion = doGetAbsolutePath(responseJson);

        assertEquals("https://localhost:8080/hgapi/v1/1.0", pathToRequiredVersion);
    }

    @Test(expected = IllegalStateException.class)
    public void noAppropriateVersionAvailableThrows() throws IOException {
        final String responseJson =
                "{\n" +
                        "\"versions\": [\n" +
                        "{\n" +
                        "\"id\": \"2.0\",\n" +
                        "\"status\": \"current\",\n" +
                        "\"path\": \"/hgapi/v1/1.0/\",\n" +
                        "\"documentation\": [\"http://support.real-status.com/hgapi.pdf\"]\n" +
                        "}\n" +
                        "]\n" +
                        "}";

        doGetAbsolutePath(responseJson);

    }


    @Test
    public void multipleVersionsGetsTheCorrectVersion() throws IOException {
        final String responseJson =
                "{\n" +
                        "\"versions\": [\n" +
                        "{\n" +
                        "\"id\": \"1.0\",\n" +
                        "\"status\": \"current\",\n" +
                        "\"path\": \"/hgapi/v1/1.0/\",\n" +
                        "\"documentation\": [\"http://support.real-status.com/hgapi.pdf\"]\n" +
                        "},\n" +
                        "{\n" +
                        "\"id\": \"2.0\",\n" +
                        "\"status\": \"current\",\n" +
                        "\"path\": \"/hgapi/v1/1.0/\",\n" +
                        "\"documentation\": [\"http://support.real-status.com/hgapi.pdf\"]\n" +
                        "}\n" +
                        "]\n" +
                        "}";

        String pathToRequiredVersion = doGetAbsolutePath(responseJson);

        assertEquals("https://localhost:8080/hgapi/v1/1.0", pathToRequiredVersion);


    }


    private String doGetAbsolutePath(String normalVersionResponse) throws IOException {
        VerisonPathGetter verisonPathGetter = new VerisonPathGetter();

        JsonComms mockComms = mock(JsonComms.class);
        when(mockComms.get(anyString(),anyString())).thenReturn(normalVersionResponse);

        verisonPathGetter.setJsonComms(mockComms);

        return verisonPathGetter.getAbsolutePathToRequiredVersion("https://localhost:8080", "/hgapi","", "1.0");
    }
}