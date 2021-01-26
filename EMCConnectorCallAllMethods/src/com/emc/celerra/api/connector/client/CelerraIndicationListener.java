/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.client;

/**
 * The client application needs to implement this interface if the
 * it is required to listen to the incoming indications.
 */
public interface CelerraIndicationListener
{
    /**
     * Do required processing for the newly arrived celerra indication.
     * <br>
     * NOTE: all indications are received by one thread of the CelerraConnector.
     * Therefore, if the application does not process the indications rapidly,
     * the new indications can not be received and the remote server buffers may
     * overflow and start dropping messages.
     * @param ind Received indication
     */
    public abstract void processIndication(CelerraIndication ind);
    /**
     * Notifies the application that the connection has been terminated
     * @param message a short message explains the reson behind this termination.
     */
    public abstract void connectionTerminated(String message);
}
