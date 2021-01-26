/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.shared;


public interface ProtocolConstants
{
    /** The server invalidates auth. cookie every 8 hours
    * The client needs to renew the cookie a little bit before that
    */
    public static final long TICKET_RENEW_PERIOD = (2*60000L);

    public static final String PROTOCOL_SERVLET_NAME =  "/servlets/CelerraManagementServices";
    public static final String HTTP_VERSION = "1.1";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_PUT = "PUT";

    // HTTP headers
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_TRANSFER_ENCODING="Transfer-Encoding";
    public static final String HEADER_COOKIE="Cookie";
    public static final String HEADER_SET_COOKIE="Set-Cookie";

    // Key word for sequence number
    // Used as a header and within the body
    public static final String SEQUENCE_NUMBER="Sequence-Number";
    public static final String REQUEST_ID="Request-Id";

    // Header values and generic constants
    public static final String CONTENT_TYPE = "text/xml; charset=\"UTF-8\"";
    public static final String ENCODING = "UTF-8";
    public static final String CHUNKED="chunked";

    // CelerraConnector specific header strings
    public static final String HEADER_CONNECTOR_ID="CelerraConnector-ID";
    public static final String HEADER_CONNECTOR_RC="CelerraConnector-RC";  // Return code of the get
    public static final String HEADER_CONNECTOR_SESS="CelerraConnector-Sess";  // Session id header
    public static final String HEADER_CONNECTOR_CTL="CelerraConnector-Ctl";  // Session control

    public static final String CONNECTOR_REJECT = "REJECT";
    public static final String CONNECTOR_ACCEPT = "ACCEPT";
    public static final String CONNECTOR_DISCONNECT = "DISCONNECT";  // Comes with the header CTL
    public static final String CONNECTOR_ONE_TIME = "ONE-TIME-REQUEST";  // perform a request and disconnect
    public static final String CONNECTOR_REASON_TOO_MANY = "Reason-Too-Many";
    public static final String CONNECTOR_REASON_SESSION = "Reason-Session";
    public static final String CONNECTOR_REASON_LATER = "Reason-Retry-Later";
    public static final String CONNECTOR_REASON_SEQUENCE = "Reason-Sequence";

    // Various constants
    public static final int KEEP_HOT_INTERVAL = 60000;  // The interval with which server starts a keep-hot message (ms)
    public static final int CLIENT_RETRIES = 3;         // How may times client retries before issuing the IOException
    public static final int GENERIC_TIMEOUT = (CLIENT_RETRIES+1)*KEEP_HOT_INTERVAL; // Server inactive session timeout
    public static final int MAX_OUTGOING_SIZE = (1024*128); // MAX size of the icoming data before server stops sending
    public static final int MAX_CLIENTS = 16;           // Maximum remote clients of CelerraConnector

}