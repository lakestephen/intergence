package com.intergence.hgsrest.restcomms;

import com.intergence.hgsrest.util.StreamReader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class DefaultJsonComms implements JsonComms {

	private Logger log = Logger.getLogger(this.getClass());

    private int requestTimeoutSeconds = 10;

    @Override
	public String put(String fullCallUrl, String authorisationKey, String json) throws IOException {

        log.info("Making PUT call to [" + fullCallUrl + "]");
        log.debug("Request: " + json);
        HttpURLConnection conn = getHttpURLConnection(fullCallUrl);

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Authorization", authorisationKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setReadTimeout(requestTimeoutSeconds * 1000);

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();

        String response = handleResponse(conn);

	    return response;
    }

    @Override
    public String get(String fullCallUrl, String authorisationKey) throws IOException {

        log.info("Making GET call to [" + fullCallUrl + "]");

        HttpURLConnection conn = getHttpURLConnection(fullCallUrl);

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authorisationKey);
        conn.setRequestProperty("Accept", "application/json");

        conn.setReadTimeout(requestTimeoutSeconds * 1000);

        String response = handleResponse(conn);

        return response;
    }

    private HttpURLConnection getHttpURLConnection(String fullCallUrl) throws IOException {
        URL url = new URL(fullCallUrl);
        return (HttpURLConnection) url.openConnection();
    }

    private String handleResponse(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String errorResponse = new StreamReader().readStream(conn.getErrorStream());
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode() + " : " + conn.getResponseMessage() + "\r\n" + errorResponse);
        }

        String response = new StreamReader().readStream(conn.getInputStream());

        conn.disconnect();

	    log.debug("Response: " + response);

	    return response;
    }

    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }
}
