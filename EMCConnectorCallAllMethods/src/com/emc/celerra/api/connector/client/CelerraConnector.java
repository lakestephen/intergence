/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.client;

import com.emc.celerra.api.connector.util.Tool;
import com.emc.celerra.api.connector.shared.ProtocolConstants;
import com.emc.celerra.api.connector.util.Logger;
import com.emc.celerra.api.connector.util.LoggerParams;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Date;

/********************************************************************************
 * This class implements XML API V2 session protocol.
 * <br>
 * In essence, the protocol between the server uses two types of HTTP requests:
 * <ul>
 * <li>
 * HTTP POST is used to make RPC style calls to the server
 * <li>
 * HTTP GET is used to receive asynchronous messages (indications)
 * from the server. HTTP GET reply is a chunked stream of data that
 * never ends (except special cases). Each chunk in the stream
 * corresponds to one indication.
 * </li>
 * </ul>
 * For the purpose of session recovery each chunk is sequenced.
 * In addition, to keep the HTTP login ticket hot, there is a
 * thread that automatically renews the ticket.
 * <p>
 * A client can use connector in 2 ways:
 * <ol>
 * <li>
 * Recommended method:
 * <code>
 * <br>
 *    a) Create an instance of the connector
 * <br>
 *    b) Set an indication listener
 * <br>
 *    c) Work
 *       Use call() method on this instance of the connector
 *       to perform all calls to the server; listen and process
 *       incoming indications
 * <br>
 *    d) Upon work completion call terminate() method on the connector
 *       to free the underlying session on the server side.
 * </code>
 * <br>
 *    Using this method, once steps a) and b) are completed, the underlying
 *    session never times out.
 * <br>
 *    If step b) is not done, the underlying session times out in about
 *    90 seconds
 * <br>
 *    If step d) is not done, the application may experience a memory/thread leak
 *    when it creates multiple instances of the connector
 * <li>
 * Non - recommended method
 * <br>
 *    The application does not use instance of a connector but, instead,
 *    makes a call to the server using call() method defined on the
 *    connector class (static). This method has low performance. Also, without
 *    creating an instance, the client is not able to listen to indications.
 * </ol>
 *
 */
public class CelerraConnector implements ProtocolConstants
{
    private static String noOpPacket = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
                     "<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\"/>";

    private static class MyX509TrustManager implements X509TrustManager
    {

        // No certuficate verification

        MyX509TrustManager()
        {
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType)
                throws CertificateException
        {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType)
                throws CertificateException
        {
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
    }

    private static SSLContext ctx;

    static {

        TrustManager[] myTM = new TrustManager[]{new MyX509TrustManager()};
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, myTM, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (Exception ex) {
            Logger.logErr(ex);
        }
    }
    /**
     * A thread that listens for indications and sends them to the indication listener
     */
    class IndThread extends Thread
    {
        boolean terminate;

        IndThread()
        {
            setDaemon(true);
        }
        public void run()
        {
            try {
                while (true) {
                    if (terminate) {
                        break;
                    }
                    getOneIndication();
                }
            } catch (Exception ex) {
                if(!terminate) {
                    terminate();
                    listener.connectionTerminated(ex.getMessage());
                    Logger.log(LoggerParams.LOG_INDICATION, "Indicator Thread: " + this.getName() + " - exiting with error: "+ex.getMessage());
                    return;
                }
            }
            finally {
                Logger.log(LoggerParams.LOG_INDICATION, "Indicator Thread: " + this.getName() + " - exiting");
                listener.connectionTerminated("Normal termination");
            }
        }

        public synchronized void terminateThread()
        {
            terminate = true;
            this.interrupt();
        }
        public synchronized boolean isTerminationStarted()
        {
            return terminate;
        }
    };
    /**
     * A thread that periodically renews the authentication ticket
     */
    class TicketRenewer extends Thread
    {
        TicketRenewer()
        {
            setDaemon(true);
        }
        public void run()
        {

            while(true) {
                try {
                    Thread.sleep(60000);
                    long time = login(); // It is NOOP if the ticket did not expire
                    if(time - lastRequestTime >= TICKET_RENEW_PERIOD) {
                        long time0 = System.currentTimeMillis();
                        CelerraResponse resp = post(noOpPacket, false);
                        long time1 = System.currentTimeMillis();
                        if(resp.getContent() != null) {
                            String s = new String(resp.getContent());
                            setCelerraTimeFromResponse(s, time1- time0);
                        }
                        else {
                            Logger.logErr("No response to ticket renewer");
                        }
                    }
                }
                catch(InterruptedException ie) {
                    return;
                }
                catch(Exception ex) {
                }
            }
        }
        public void terminateThread()
        {
            this.interrupt();
        }
    }

    private String  host;
    private int     port;
    private String  proxyHost;
    private int     proxyPort;
    private String  userName;
    private String  password;
    private String  authCookie;
    private long    authCookieTime;
    private long    lastRequestTime;
    private String  jsessionCookie;
    private String  connectorSession;
    private Object  indicationsSyncObject = new Object();
    private Object  loginSyncObject = new Object();
    private boolean indicationsIntialized = false;
    private TicketRenewer ticketRenewer;
    private IndThread indThread;
    private Socket currentIndicationsSocket;
    private DataInputStream currentIndicationsReader;
    private int lastInboundSequenceNumber = -1;
    private CelerraIndicationListener listener;
    private long    celerraTime = -1;
    /**
     * Constructor
     * @param host host name or IP address of the Control Station host
     * @param port port number (Normally 443, unless Celerra Control
     * station reconfigured to use another port)
     * @param proxyHost proxy host name or IP address if proxy tunneling
     *  is desired, otherwise null
     * @param proxyPort proxy host port number
     * @param userName  user name with which to login into control station
     * @param password  user password
     * @throws IOException  if there are some communications problems
     * @throws CelerraAuthenticationException if Control Station could
     * not authenticate the user
     */
    public CelerraConnector(String host, int port,
                            String proxyHost, int proxyPort,
                            String userName, String password)
            throws IOException, CelerraAuthenticationException
    {
        this.host = host;
        this.port = port;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.userName = userName;
        this.password = password;
        long time0 = System.currentTimeMillis();
        CelerraResponse resp = post(noOpPacket, false);
        long time1 = System.currentTimeMillis();
        if(resp.getContent() != null) {
            String s = new String(resp.getContent());
            setCelerraTimeFromResponse(s, time1- time0);
        }
        else {
            resp.print(System.err, false);
            throw new IOException(resp.getStatusString());
        }
        ticketRenewer = new TicketRenewer();
        ticketRenewer.start();
    }
    private CelerraConnector()
    {}

    /**
     * Terminate connector internal threads
     * Call server to deallocated the session.
     */
    public void terminate()
    {

        if(ticketRenewer != null) {
            ticketRenewer.terminateThread();
            ticketRenewer = null;
        }
        if (indThread != null) {
            indThread.terminateThread();
            indThread = null;
        }
        try {
            // Signal the servlet theat we are termination this session
            call(null);
        }
        catch(Exception ex) {
            Logger.log(LoggerParams.LOG_HTTP_POST, "Exception thrown during disconnect:\n"+Tool.stackTraceToString(ex));
        }
    }
    /**
     * Make one time call. On server, the session is created and immediately
     * deallocated after the call is performed. This is not preffered method
     * of using the connector, as it is slow (due to the fact that each time
     * a login needs to be performed).
     * @param request properly xml-formatted user request
     * @param host host name or IP address of the Control Station host
     * @param port port number (Normally 443, unless Celerra Control
     * station reconfigured to use another port)
     * @param proxyHost proxy host name or IP address if proxy tunneling
     *  is desired, otherwise null
     * @param proxyPort proxy host port number
     * @param userName  user name with which to login into control station
     * @param password  user password
     * @return CelerraResponse that contains the reply
     * @throws IOException  if there are some communications problems
     * @throws CelerraAuthenticationException if Control Station could
     * not authenticate the user
     */
    public static CelerraResponse call( String request,
                                        String host, int port,
                                        String proxyHost, int proxyPort,
                                        String userName, String password)
            throws IOException, CelerraAuthenticationException
    {
        CelerraConnector c = new CelerraConnector();
        c.host = host;
        c.port = port;
        c.proxyHost = proxyHost;
        c.proxyPort = proxyPort;
        c.userName = userName;
        c.password = password;
        return c.quickCall(request);
    }
    /**
     * Send request and receive a reponse
     * @param request user request as xml-formatted string
     * @return CelerraResponse that contains the reply
     * @throws IOException if any communications exception occurred during the processing
     * @throws CelerraAuthenticationException is the user can not be authenticated
     */
    public CelerraResponse call(String request)
            throws IOException, CelerraAuthenticationException
    {
        CelerraResponse repl = null;
        for (int i = 0; i < 3; i++) {
            repl = post(request, false);
            if (repl.getContent() != null && repl.getContent().length > 0) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ex) { }
        }
        return repl;
    }

    /**
     * Set an indication listener. Each new listener replaces the new one.
     * @param list inidctaion listener
     */
    public void listen(CelerraIndicationListener list)
    {
        if (indThread != null) {
            indThread.terminateThread();
        }
        listener = list;
        indThread = new IndThread();
        indThread.start();
        // Wait until inidcation threads establieshed the connection
        // In case it does it faster that we come over here, just time out
        // on wait, but this is most unlikely
//        synchronized(indicationsSyncObject) {
//            if(!indicationsIntialized) {
//                try {
//                    indicationsSyncObject.wait(60000);
//                }
//                catch(InterruptedException ie) {}
//            }
//        }
    }
    /**
     * Get UTF time in MS of the control station
     * @return UTF time in MS of the control station or -1, if not yet determined
     */
    public synchronized long getCelerraTime()
    {
        return celerraTime;
    }
    /**
     * Returns connector's session id, which can be used for filtering
     * tasks that belong to the session or for user recovery
     * @return session id as a string
     */
    public String getConnectorSession()
    {
        return connectorSession;
    }

    /**
     * Send a quick (with subsequent disconnect) request and recive a response
     * @param request user request properly formated
     * @return CelerraResponse that contains the reply
     * @throws IOException
     * @throws CelerraAuthenticationException
     */
    private  CelerraResponse quickCall(String request)
            throws IOException, CelerraAuthenticationException
    {
        CelerraResponse repl = null;
        for (int i = 0; i < 3; i++) {
            repl = post(request, true);
            if (repl.getContent() != null && repl.getContent().length > 0) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ex) { }
        }
        return repl;
    }
    /**
     * Perform login to Celerra control station server
     * @return login time in milliseconds
     * @throws IOException
     * @throws CelerraAuthenticationException
     */
    private long login()
            throws IOException, CelerraAuthenticationException
    {
        synchronized(loginSyncObject) {
            long time = System.currentTimeMillis();
            if(time - authCookieTime > TICKET_RENEW_PERIOD-60000) {
                authCookie = null;
            }
            if (authCookie != null) {
                return time;
            }

            Socket sock = getSSLSocket(host, port, proxyHost, proxyPort);
            //Socket sock  = getSocket(host, 80);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out.println(HTTP_POST + " " + "http://"+host+"/Login" + " HTTP/" + HTTP_VERSION);
            out.println(HEADER_CONTENT_TYPE + ": " + "application/x-www-form-urlencoded");
            out.println(HEADER_HOST + ": " + host);
            StringBuffer msg = new StringBuffer(128);
            msg.append("user=");
            msg.append(userName);
            msg.append("&password=");
            msg.append(password);
            msg.append("&Login=Login");
            out.println(HEADER_CONTENT_LENGTH + ": " + msg.length());
            out.println();
            out.println(msg.toString());
            out.flush();
            if(LoggerParams.isSet(LoggerParams.LOG_HTTP_POST)) {
                Logger.print(HTTP_POST + " " + "http://"+host+"/Login" + " HTTP/" + HTTP_VERSION);
                Logger.print(HEADER_CONTENT_TYPE + ": " + "application/x-www-form-urlencoded");
                Logger.print(HEADER_HOST + ": " + host);
                Logger.print(HEADER_CONTENT_LENGTH + ": " + msg.length());
                Logger.print("");
                Logger.print(msg.toString());
            }
            String cookie = null;
            while (true) {
                try {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    Logger.print(LoggerParams.LOG_HTTP_POST,line);
                    int ix = line.indexOf(":");
                    String cname;
                    if (ix > 0 && ix < (line.length() - 1)) {
                        cname = line.substring(0, ix).trim();
                        if (cname.equalsIgnoreCase(HEADER_SET_COOKIE)) {
                            cookie = line.substring(ix + 2).trim();
                            break;
                        }
                    }
                }
                catch (IOException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new com.emc.celerra.api.connector.client.CelerraAuthenticationException(Tool.stackTraceToString(e));
                }
            }
            returnSocket(sock);
            if (cookie != null) {
                authCookie = cookieWork(cookie);
                authCookieTime = System.currentTimeMillis();
                return time;
            }

            throw new com.emc.celerra.api.connector.client.CelerraAuthenticationException("Unable to login, cookie not found");
        }
    }
    /**
     * Extract real cookie from the Set-Cookie header value returned by the server
     * @param hdr Set-Cookie header value
     * @return hdr
     */
    private String cookieWork(String hdr)
    {
        // The hdr looks like: Ticket=ip&10.240.12.182&idle&0&persists&&last&1087848954&expires&480&hash&23810b590a9c23219ee8df7ea5fa1b8c&user&nasadmin&type&User&time&1087848954; path=/
        // Delete /path from the hdr
        int ix = hdr.indexOf(";");
        if(ix>0) {
            hdr = hdr.substring(0, ix)+"; $Path=/";
        }
        return hdr;
    }
    /**
     * Compute and set time of the celerra control station
     * based on the roundtrip time of an empty packet and the reply
     * from the celerra
     * @param resp response to and empty packet
     * @param roundtripTime time of the roundtrip of the packet
     */
    private synchronized void setCelerraTimeFromResponse(String resp, long roundtripTime)
    {
        final String prfx = "time=";
        int ix = resp.indexOf(prfx);
        if(ix>0) {
            ix += prfx.length();
            char quot = resp.charAt(ix);
            ix++;
            int ix1 = resp.indexOf(quot, ix);
            if(ix1>0) {
                String value = resp.substring(ix, ix1);
                celerraTime = Long.parseLong(value) + roundtripTime/2;
                return;
            }
        }
        Logger.logErr("Unable to find time attribute in response: \n"+resp);
    }
    /**
     * Send request and receive reply.
     * @param content request string; if null, this is a connector request to deallocate the server session
     * @param disconnectOnExit if true, make request, send back the response and deallocate session
     * @return server response
     * @throws IOException
     * @throws CelerraAuthenticationException
     */
    private CelerraResponse post(String content, boolean disconnectOnExit)
            throws IOException, CelerraAuthenticationException
    {
        PrintWriter out = null;
        DataInputStream in = null;
        int retries = 2;
        // Do some retries rapidly
        for (int r = 0; r < retries; r++) {
            try {
                lastRequestTime = login();
                Socket sock = getSSLSocket(host, port, proxyHost, proxyPort);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
                in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
                out.println(HTTP_POST + " " + PROTOCOL_SERVLET_NAME + " HTTP/" + HTTP_VERSION);
                out.println(HEADER_HOST + ": " + host + ':' + port);
                out.println(HEADER_CONTENT_TYPE + ": " + "text/xml");
                out.println(HEADER_COOKIE + ": " + authCookie);

//                if(jsessionCookie != null) {
//                    out.println(HEADER_COOKIE + ": " + jsessionCookie);
//                }
                if(connectorSession != null) {
                    out.println(HEADER_CONNECTOR_SESS+": "+connectorSession);
                }
                if(content == null) {
                    out.println(HEADER_CONNECTOR_CTL+": "+CONNECTOR_DISCONNECT);
                    out.println();
                }
                else {
                    if(disconnectOnExit) {
                        out.println(HEADER_CONNECTOR_CTL + ": " + CONNECTOR_ONE_TIME);
                    }
                    out.println(HEADER_CONTENT_LENGTH + ": " + content.length());
                    out.println();
                    out.println(content);
                }
                out.println();
                out.flush();
                if(LoggerParams.isSet(LoggerParams.LOG_HTTP_POST)){
                    Logger.print(HTTP_POST + " " + PROTOCOL_SERVLET_NAME + " HTTP/" + HTTP_VERSION);
                    Logger.print(HEADER_HOST + ": " + host + ':' + port);
                    Logger.print(HEADER_CONTENT_TYPE + ": " + "text/xml");
                    Logger.print(HEADER_COOKIE + ": " + authCookie);
                    if(jsessionCookie != null) {
                        Logger.print(HEADER_COOKIE + ": " + jsessionCookie);
                    }
                    if(connectorSession != null) {
                        Logger.print(HEADER_CONNECTOR_SESS+": "+connectorSession);
                    }
                    if(content == null) {
                        Logger.print(HEADER_CONNECTOR_CTL+": "+CONNECTOR_DISCONNECT);
                        Logger.print("");
                    }
                    else {
                        if(disconnectOnExit) {
                            Logger.print(HEADER_CONNECTOR_CTL + ": " + CONNECTOR_ONE_TIME);
                        }
                        Logger.print(HEADER_CONTENT_LENGTH + ": " + content.length());
                        Logger.print("");
                        Logger.print(content);
                    }

                }
                Logger.log(LoggerParams.LOG_HTTP_POST, "===== Post: request sent.   Thread: " + Thread.currentThread());


                /* read the status line */
                int statusCode = 0;
                String statusString = null;
                Logger.log(LoggerParams.LOG_HTTP_POST, "===== Post: reply.");

                try {
                    String line = in.readLine();
                    if (line == null) {
                        continue;
                    }
                    Logger.print(LoggerParams.LOG_HTTP_POST, line);
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken(); // ignore version part
                    statusCode = Integer.parseInt(st.nextToken());
                    StringBuffer sb = new StringBuffer();
                    while (st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        if (st.hasMoreTokens()) {
                            sb.append(" ");
                        }
                    }
                    statusString = sb.toString();
                } catch (IOException ex) {
                    throw ex;
                } catch (Exception e) {
                    Logger.logErr(e);
                    throw new IllegalArgumentException("error parsing HTTP status line: " +
                            e.getMessage());
                }
                if(statusCode==401 || statusCode == 403) {
                    // Unauthorized, relogin
                    synchronized(loginSyncObject) {
                        authCookie = null;
                    }
                    continue;
                }
                int respContentLength = -1;
                try {
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        Logger.print(LoggerParams.LOG_HTTP_POST, line);
                        if (line.length() == 0) {
                            break;
                        }
                        int colonIndex = line.indexOf(':');
                        String fieldName = line.substring(0, colonIndex);
                        String fieldValue = line.substring(colonIndex + 1).trim();
                        if (fieldName.equalsIgnoreCase(HEADER_CONTENT_LENGTH)) {
                            respContentLength = Integer.parseInt(fieldValue);
                        }
                        else if (fieldName.equalsIgnoreCase(HEADER_CONTENT_TYPE)) {
                            //respContentType = fieldValue;
                        }
                        else if(fieldName.equalsIgnoreCase(HEADER_CONNECTOR_SESS)) {
                            if(connectorSession == null) {
                                connectorSession = fieldValue;
                            }
                        }
                        else if (fieldName.equalsIgnoreCase((HEADER_SET_COOKIE))) {
                            String cookieName = fieldValue.substring(0,fieldValue.indexOf("="));
                            if(cookieName.equalsIgnoreCase("JSESSIONID")) {
                                jsessionCookie = cookieWork(fieldValue)+"; $Secure";
                            }
                            else if(cookieName.equalsIgnoreCase("Ticket")) {
                                authCookie =  cookieWork(fieldValue);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("error reading HTTP headers: " +
                            e.getMessage());
                }

                if (respContentLength > 0) {
                    /* all done */
                    byte[] buf = new byte[respContentLength];
                    int offset = 0;
                    try {
                        while (offset < respContentLength) {
                            int numBytes = in.read(buf, offset, respContentLength - offset);
                            if (numBytes <= 0) {
                                break;
                            }
                            offset += numBytes;
                        }


                    }
                    catch (IOException ex) {
                        Logger.logErr(ex);
                    }
                    Logger.print(LoggerParams.LOG_HTTP_POST, new String(buf));
                    returnSocket(sock);

                    return new CelerraResponse(statusCode, statusString, buf);
                }
                else {
                    // forget socket
                    sock.close();
                    return new CelerraResponse(statusCode, statusString, null);
                }
            } catch (IOException ex) {
                if (r == retries - 1) {
                    throw ex;
                }
            }

        }
        throw new IOException("No more retries");
    }
    /**
     * Dispose of a socket
     * @param sock socket
     */
    private void returnSocket(Socket sock)
    {
        try {
            sock.close();
        } catch (IOException ex) {
        }
    }

    // Return either:
    // Indication structure - new data buffer
    // IOException - caller should start a new session
    // In the header of the get the client sends the last sequence number it got.
    // If this is first connection, it sends -1
    // If the server thinks differently, it should abort the connection
    // With each packet the servers sends its sequence number
    private final String REJECT = "Establishing indication connection has been rejected. Server reason code is: ";
    private void getOneIndication()
            throws IOException
    {
        long startTime = System.currentTimeMillis();
        while(true) {
            try {
                // This may throw an IOException, after which the socket is closed
                startIndicationsConnection();
                synchronized(indicationsSyncObject) {
                    indicationsIntialized = true;
                    indicationsSyncObject.notifyAll();
                }

                String line = null;

                if ((line = currentIndicationsReader.readLine()) != null) {
                    // Regular message with data or a keep-hot message with 0 length data
                    Logger.print(LoggerParams.LOG_HTTP_GET, line);
                    processRegularMessage(line);
                }
                else {
                    //Logger.logErr("Indications: null line - closing stream");
                    closeCurrentIndicationsSocket();
                }
                return;
            } catch (IOException e) {
                String msg = e.getMessage();
                if(msg != null && msg.startsWith(REJECT)) {
                    throw e;
                }
                if(indThread.isTerminationStarted()) {
                    throw e;
                }
                closeCurrentIndicationsSocket();
                long tmDiff = System.currentTimeMillis() - startTime;
                if(tmDiff<GENERIC_TIMEOUT) {
                    Tool.threadSleep(5000);
                }
                else {
                    Logger.logErr(e);
                    throw e;
                }
            } catch (Exception e) {
                closeCurrentIndicationsSocket();
                Logger.logErr(e);
                throw new IOException("Unexpected problems: " + e.getMessage());
            }
        }
    }
    private boolean processRegularMessage(String line)
            throws IOException
    {
        int respContentLength = -1;
        int sequenceNumber = -1;
        String[] tkns = Tool.tokenize(line, " \t;=");
        if (tkns.length == 3 && tkns[1].equalsIgnoreCase(SEQUENCE_NUMBER)) {
            respContentLength = Integer.parseInt(tkns[0], 16);
            sequenceNumber = Integer.parseInt(tkns[2], 10);
        }
        else if (tkns.length == 1) {
            // Must be keep hot token
            return true;
        }
        else {
            throw new IOException("Invalid control line: "+line);
        }
        boolean outOfSequence = false;
        if ((lastInboundSequenceNumber + 1) != sequenceNumber) {
            if (sequenceNumber <= lastInboundSequenceNumber) {
                Logger.log(LoggerParams.LOG_HTTP_GET,
                        "CelerraConnector: ignoring packet: lastSeq=" + lastInboundSequenceNumber +
                        "  newSeq=" + sequenceNumber);
                outOfSequence = true;
            }
            else {
                closeCurrentIndicationsSocket();
                throw new IOException("CelerraConnector: out of sequence: lastSeq=" + lastInboundSequenceNumber +
                        "  newSeq=" + sequenceNumber);
            }
        }
        if (respContentLength > 0) {
            byte[] buf = new byte[respContentLength];
            int offset = 0;
            while (offset < respContentLength) {
                int numBytes = currentIndicationsReader.read(buf, offset, respContentLength - offset);
                if (numBytes <= 0) {
                    break;
                }
                offset += numBytes;
            }

            if (outOfSequence) {
                Logger.log(LoggerParams.LOG_HTTP_GET, "==== Indications out of sequence exception");
                throw new IOException("Indications out of sequence");
            }
            // Convert everything back to bytes, because jaxb seem to have problem with InputSource
            // created from ready characters
            if(LoggerParams.isSet(LoggerParams.LOG_HTTP_GET)) {
                Logger.print(new String(buf));
                Logger.log("Indications normal return");
            }
            // Normal return
            CelerraIndication ind = new CelerraIndication();
            lastInboundSequenceNumber = sequenceNumber;
            ind.setContent( buf );
            ind.setTransportSequenceNumber(sequenceNumber);
            listener.processIndication(ind);

            return true;
        }
        else {
            final String msg = "==== Indications: protocol error non-positive response length";
            Logger.log(LoggerParams.LOG_HTTP_GET, msg);
            closeCurrentIndicationsSocket();
            throw new IOException(msg);
        }

    }

    // send the last sequence number I received
    private void startIndicationsConnection()
            throws IOException, com.emc.celerra.api.connector.client.CelerraAuthenticationException
    {
        synchronized(loginSyncObject) {
            if(authCookie == null) {
                throw new CelerraAuthenticationException("Not authenticated");
            }
        }
        if(jsessionCookie == null){
            throw new CelerraAuthenticationException("No session");
        }
        if(connectorSession == null){
            throw new CelerraAuthenticationException("No connector session");
        }

        // See if we have opened a socket already, and if so, ignore the call
        if (currentIndicationsSocket != null) {
            return;
        }
        Exception err;
        // In principle we should try only once, but just in case something unexpected happen, let do it more than once
        for (int retries = 0; retries < CLIENT_RETRIES; retries++) {
            err = null;
            currentIndicationsSocket = getSSLSocket(host, port, proxyHost, proxyPort);
            currentIndicationsReader = new DataInputStream(new BufferedInputStream(currentIndicationsSocket.getInputStream()));
            PrintWriter currentIndicationsWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(currentIndicationsSocket.getOutputStream())));
            // Start new chunked request
            currentIndicationsWriter.println(HTTP_GET + " " + PROTOCOL_SERVLET_NAME + " HTTP/" + HTTP_VERSION);
            currentIndicationsWriter.println(HEADER_HOST + ": " + host + ':' + port);
            currentIndicationsWriter.println(HEADER_COOKIE+": "+authCookie);
            currentIndicationsWriter.println(HEADER_COOKIE + ": " + jsessionCookie);
            currentIndicationsWriter.println(SEQUENCE_NUMBER + ": " + Integer.toString(lastInboundSequenceNumber));
            currentIndicationsWriter.println(HEADER_CONNECTOR_SESS + ": " + connectorSession);
            currentIndicationsWriter.println(); //Empty delimiter line
            if(LoggerParams.isSet(LoggerParams.LOG_HTTP_GET)){
                Logger.print(HTTP_GET + " " + PROTOCOL_SERVLET_NAME + " HTTP/" + HTTP_VERSION);
                Logger.print(HEADER_HOST + ": " + host + ':' + port);
                Logger.print(HEADER_COOKIE+": "+authCookie);
                Logger.print(HEADER_COOKIE + ": " + jsessionCookie);
                Logger.print(SEQUENCE_NUMBER + ": " + Integer.toString(lastInboundSequenceNumber));
                Logger.print(HEADER_CONNECTOR_SESS + ": " + connectorSession);
                Logger.print(""); //Empty delimiter line
            }
            if (currentIndicationsWriter.checkError()) { // This one does the flush
                closeCurrentIndicationsSocket();
                throw new IOException("Indications Socket opening error");
            }
            Logger.log(LoggerParams.LOG_HTTP_GET, "==== Started indications socket");
            /* read the status line */
            int statusCode = 0;
            String statusString = null;
            Logger.log(LoggerParams.LOG_HTTP_GET, "==== startIndicationConnection(): reading reply:");

            try {
                String line = currentIndicationsReader.readLine();
                if (line == null) {
                    throw new IOException("Null first line");
                }
                Logger.print(LoggerParams.LOG_HTTP_GET, line);

                String[] tkns = Tool.tokenize(line, " ");
                if (tkns.length < 2) {
                    throw new IOException("Bad status line: " + line);
                }
                statusCode = Integer.parseInt(tkns[1]);
                statusString = line;
            } catch (IOException ex) {
                closeCurrentIndicationsSocket();
                throw ex;
            } catch (Exception e) {
                Logger.logErr(e);
                closeCurrentIndicationsSocket();
                throw new IllegalArgumentException("error parsing HTTP status line: " +
                        e.getMessage());
            }

            if (statusCode >= 300 || statusCode < 200) {
                Logger.log(LoggerParams.LOG_HTTP_GET, "==== Bad status code, consuming lines:");

                String line;
                while ((line = currentIndicationsReader.readLine()) != null) {
                    Logger.log(LoggerParams.LOG_HTTP_GET, line);
                }
                ;
                throw new IOException("Invalid status: " + statusString);
            }
            /* get the headers */
            boolean OK = false;
            boolean invalidProtocol = false;
            String reason = "";
            String reasonMessage = "";
            try {
                String line = null;
                while ((line = currentIndicationsReader.readLine()) != null) {
                    Logger.print(LoggerParams.LOG_HTTP_GET,line);
                    if (line.length() == 0) {
                        break;
                    }
                    int ix = line.indexOf(':');
                    String fieldName = line.substring(0, ix).trim();
                    String fieldValue = line.substring(ix + 1).trim();
                    if (fieldName.equalsIgnoreCase(HEADER_CONNECTOR_RC)) {
                        if (fieldValue.equals(CONNECTOR_ACCEPT)) {
                            OK = true;
                        }
                        else {
                            ix = fieldValue.indexOf(";");
                            if (ix > 0) {
                                reason = fieldValue.substring(ix + 1).trim();
                                ix = reason.indexOf("=");
                                if (ix > 0) {
                                    reasonMessage = reason.substring(ix + 1);
                                    reason = reason.substring(0, ix);
                                }
                            }
                            else {
                                invalidProtocol = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                OK = false;
                err = e;
            }

            // Normal return
            if (OK) {
                return;
            }

            if (invalidProtocol) {
                throw new IOException("Server does not support valid CelerraConnector version");
            }
            if (err == null && reason.length() > 0) {
                // Normal reject reply
                Logger.logErr("Reject, reasone code message: \"" + reasonMessage + "\"");
                throw new IOException(REJECT+reason+"; message is: "+reasonMessage);
            }
            else if (err != null && !(err instanceof IOException)) {
                Logger.logErr(err);
            }
            // Sleep a little between retries
            try {
                Thread.sleep(200);
                Logger.logErr("Retrying, retry#=" + (retries + 1));
            } catch (InterruptedException ex) {
            }

        }

        throw new IOException("Unable to connect");

    }

    private void closeCurrentIndicationsSocket()
    {
        try {
            if (currentIndicationsSocket != null) {
                currentIndicationsSocket.close();
            }
        } catch (Exception ex) {
        }
        currentIndicationsSocket = null;
        synchronized(indicationsSyncObject) {
            indicationsIntialized = false;
        }
    }
    /**
     * Create a secure socket
     * @param host destination host
     * @param port destination port
     * @param proxyHost proxy host; null if tunneling is not desired
     * @param proxyPort proxy port
     * @return
     * @throws IOException on any connection or socket creation problem
     */
    private static synchronized Socket getSSLSocket(String host, int port, String proxyHost, int proxyPort)
            throws IOException
    {
        for(int i=0; i<3; i++) {
            try {
                SSLSocketFactory factory = ctx.getSocketFactory();
                SSLSocket sock;
                int tunnelPort = (proxyPort <= 0) ? 443 : proxyPort;
                if (proxyHost != null) {
                    /*
                    * Set up a socket to do tunneling through the proxy.
                    * Start it off as a regular socket, then layer SSL
                    * over the top of it.
                    */
                    Socket tunnel = new Socket(proxyHost, tunnelPort);
                    tunnel.setSoLinger(false, 0);
                    tunnel.setSoTimeout(0);
                    tunnel.setKeepAlive(true);
                    doTunnelHandshake(tunnel, host, port);

                    /*
                    * Ok, let's overlay the tunnel socket with SSL.
                    */
                    sock = (SSLSocket) factory.createSocket(tunnel, host, port, true);
                }
                else {
                    sock = (SSLSocket) factory.createSocket(host, port);
                }
                sock.setSoLinger(false, 0);
                sock.setSoTimeout(0);
                sock.setKeepAlive(true);

                sock.startHandshake();
                return sock;
            } catch (Exception e) {
                Logger.logErr(e);
                if(i>=2) {
                    if (e instanceof IOException) {
                        throw (IOException) e;
                    }
                    throw new IOException("Exception opening socket: " + e.toString());
                }
                else {
                    try {
                        Thread.sleep(10000);
                    }
                    catch(InterruptedException ie){}
                }
            }
        }
        return null;
    }

    /*
     * Tell our tunnel where we want to CONNECT, and look for the
     * right reply.  Throw IOException if anything goes wrong.
     */
    private static void doTunnelHandshake(Socket tunnel, String host, int port)
            throws IOException
    {
        OutputStream out = tunnel.getOutputStream();
        String msg = "CONNECT " + host + ":" + port + " HTTP/1.1\r\n"
                + "User-Agent: "
                + sun.net.www.protocol.http.HttpURLConnection.userAgent+"\r\n"
                + "Proxy-Connection: keep-alive\r\n"
                + "Host: "+host + ":" + port+"\r\n"
                + "\r\n";
        byte b[];
        try {
            /*
            * We really do want ASCII7 -- the http protocol doesn't change
            * with locale.
            */
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            /*
            * If ASCII7 isn't there, something serious is wrong, but
            * Paranoia Is Good (tm)
            */
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();

        /*
        * We need to store the reply so we can create a detailed
        * error message to the user.
        */
        byte reply[] = new byte[200];
        int replyLen = 0;
        int newlinesSeen = 0;
        boolean headerDone = false;	/* Done on first newline */

        InputStream in = tunnel.getInputStream();

        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            }
            else if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }

        /*
        * Converting the byte array to a string is slightly wasteful
        * in the case where the connection was successful, but it's
        * insignificant compared to the network overhead.
        */
        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }

        /* We asked for HTTP/1.0, so we should get that back */
        if (replyStr.startsWith("HTTP/1.0") || replyStr.startsWith("HTTP/1.1")) {
            if (replyStr.indexOf(" 200") > 0) {
                return;
            }
        }
        String message = "Unable to tunnel through "
                + tunnel.getInetAddress().getHostAddress() + ":" + tunnel.getPort()
                + ".\nProxy returns:\n \"" + replyStr + "\"";
        Logger.logErr(message);
        throw new IOException(message);

    }

}
