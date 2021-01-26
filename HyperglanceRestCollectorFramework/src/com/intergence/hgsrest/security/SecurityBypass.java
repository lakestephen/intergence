package com.intergence.hgsrest.security;

import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This is a method of bypassing HTTPS security. Requested by Rupert
 */
public class SecurityBypass {

    private static Logger log = Logger.getLogger(SecurityBypass.class);

    public void bypassSecurity() {

        installTrustAllTrustManager();
        removeHostnameVerification();
    }

    private void installTrustAllTrustManager() {

        log.warn("BYPASSING SSL SECURITY! NOTE: This will open you up to man-in-the-middle type attacks.");

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void removeHostnameVerification() {

        HostnameVerifier hv = new HostnameVerifier(){
            public boolean verify(String urlHostName, SSLSession session){
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

}
