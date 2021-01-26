/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.client;


/**
 * This class carries an indication and the transport level sequence numeber
 * that corresponds to this indication.
 */
public class CelerraIndication
{
    private int             transportSequenceNumber;
    private byte[]          content;
    CelerraIndication()
    {}
    /**
     * Get the arrived indication as an array of bytes.
     * @return indication
     */
    public byte[] getContent()
    {
        return content;
    }
    /**
     * Returns transport level (HTTP chunk) sequence number, that is used by the
     * underlying transport mechanizm for the purpose of recovery
     * of the lost connections. This method is normally used for debuging
     * and verification purpose only.
     * @return transport sequence number
     */
    public int getTransportSequenceNumber()
    {
        return transportSequenceNumber;
    }

    void setTransportSequenceNumber(int transportSequenceNumber)
    {
        this.transportSequenceNumber = transportSequenceNumber;
    }
    void setContent(byte[] content)
    {
        this.content = content;
    }
}
