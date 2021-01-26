/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.connector.util;

public class LoggerParams
{
    public static final long    LOG_HTTP_POST = 1;
    public static final long    LOG_HTTP_GET = 2;
    public static final long    LOG_SESSION = 4;
    public static final long    LOG_CACHE_INIT = 8;
    public static final long    LOG_CACHE_REQUEST = 16;
    public static final long    LOG_INDICATION = 32;
    public static final long    LOG_USER_REQUEST = 64;
    public static final long    LOG_INDICATION_DETAIL = 128;

    private static long         mask = 255;

    public static long getMask()
    {
        return mask;
    }

    public static void setMask(long mask)
    {
        LoggerParams.mask = mask;
    }
    public static boolean isSet(long flags)
    {
        return ((flags & mask) != 0);
    }
}
