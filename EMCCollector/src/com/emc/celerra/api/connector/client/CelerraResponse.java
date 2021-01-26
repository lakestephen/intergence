/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/

package com.emc.celerra.api.connector.client;


import java.io.PrintStream;

/**
 * Class CelerraResponse carries the actual response and status
 * of the reply to HTTP post request
 */
public class CelerraResponse
{
    private int              statusCode;
    private String           statusString;
    private byte[]           content;

    /**
     * Constructor
     * @param statusCode intger HTTP status code
     * @param statusString status as string (as taken from HTTP first line)
     * @param content
     */
    CelerraResponse (int statusCode, String statusString, byte[] content)
    {
        this.statusCode = statusCode;
        this.statusString = statusString;
        this.content = content;
    }
    /**
     * Get the response as byte array. If the status code is not 200,
     * the array can be null
     * @return content of the reply as an array of characters
     */
    public byte[] getContent()
    {
        return content;
    }
    /**
     * Returns HTTP reply status code as integer
     * @return  status code
     */
    public int getStatusCode()
    {
        return statusCode;
    }
    /**
     * Returns HTTP status code as string
     * @return statusCode status code as string from the HTTP first line
     */
    public String getStatusString()
    {
        return statusString;
    }

    /**
     * Print the celerra response status to the specified print stream.
     * Optionally print the content of the response
     * @param str print stream to which to print
     * @param printContent if true, print the content of the response
     */
    public void print(PrintStream str, boolean printContent)
    {

        str.println("========== Celerra Response ==========");
        str.println("statusCode="+statusCode+"  statusString="+statusString);
        if(printContent) {
            if(content == null) {
                str.println("Content == null");
            }
            else if(content.length>0) {
                try {
                    String s = new String(content);
                    str.print(s);
                    str.println();
                }
                catch(Exception ex){}
            }
        }

    }

}

