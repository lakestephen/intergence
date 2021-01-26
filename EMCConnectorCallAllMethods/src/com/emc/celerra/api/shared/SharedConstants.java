/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/


package com.emc.celerra.api.shared;

public interface SharedConstants
{
    public static final String CMD_FROM_SERVLET = "SERVLET";
    public static final String CMD_FROM_JSERVER = "JSERVER";
    public static final String SESS_DEL_ALL = "session.delete.all";
    public static final String SESS_DEL_ONE = "session.delete.one";
    public static final String SESS_REM_ONE_SUBS = "session.remove.one.subscription";
    public static final String SESS_REM_ALL_SUBS = "session.remove.all.subscriptions";

    public static final String CMD_JSERVER_REREAD_MOVERS = "REREAD_MOVERS";
    public static final String CMD_JSERVER_REREAD_STORAGE = "REREAD_STORAGE";
    public static final String CMD_JSERVER_REREAD_POOLS = "REREAD_POOLS";
    public static final String CMD_JSERVER_REREAD_NETWORK = "REREAD_NETWORK";
}
