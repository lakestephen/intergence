/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/

package com.emc.celerra.api.connector.client;

/**
 * This exception is thrown when the client can not be autenticated
 * by the remote server
 */
public class CelerraAuthenticationException extends Exception{

    /**
     * Constructor with a message
     * @param message
     */
    CelerraAuthenticationException(String message)
    {
        super(message);
    }
    /**
     * Default constructor
     */
    CelerraAuthenticationException()
    {
        super();
    }
}
