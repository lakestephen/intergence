/**
*
* This source file is provided as a sample to show how to write a client application that communicates with the Celerra Management API.
* It should not be used as or considered as a full-fledged application.
**/

package com.emc.celerra.api.connector.util;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logger {
    private static PrintStream ps;
    private static PrintStream es;

    public static void init()
    {
        if(ps != null) return;
        String loggerFile = System.getProperty("com.emc.celerra.util.logger.infolog");
        if(loggerFile == null) {
            ps = System.out;
        }
        else {
            try {
                ps = new PrintStream(new FileOutputStream(loggerFile, true), true);
            }
            catch (Exception ex) {
                ps = System.out;
            }
        }
        loggerFile = System.getProperty("com.emc.celerra.util.logger.errorlog");
        if(loggerFile == null) {
            es = System.err;
        }
        else {
            try {
                es = new PrintStream(new FileOutputStream(loggerFile, true), true);
            }
            catch (Exception ex) {
                es = System.err;
            }
        }
    }
    public static PrintStream getInfoStream()
    {
        return ps;
    }
    public static PrintStream getErrorStream()
    {
        return es;
    }
    public static synchronized void log(long flags, String message)
    {

        if (LoggerParams.isSet(flags)) {
            if (ps != null) {
                try {
                    String tname = Thread.currentThread().getName();
                    tname = tname+": ";
                    ps.print(com.emc.celerra.api.connector.util.Tool.formatShortCurrentLocalDate()+": "+tname);
                    ps.println(message);
                    ps.flush();
                }
                catch (Exception ex) {
                }
            }
        }
    }
    public static synchronized void log(String message)
    {
        if (ps != null) {
            try {
                String tname = Thread.currentThread().getName();
                tname = tname+": ";
                ps.print(com.emc.celerra.api.connector.util.Tool.formatShortCurrentLocalDate()+": "+tname);
                ps.println(message);
                ps.flush();
            }
            catch (Exception ex) {
            }
        }
    }
    public static synchronized void print(long flags, String message)
    {

        if (LoggerParams.isSet(flags)) {
            if (ps != null) {
                try {
                    ps.println(message);
                    ps.flush();
                }
                catch (Exception ex) {
                }
            }
        }
    }
    public static synchronized void print(String message)
    {

            if (ps != null) {
                try {
                    ps.println(message);
                    ps.flush();
                }
                catch (Exception ex) {
                }
            }
    }

    public static synchronized void log(long flags, Throwable th)
    {
        String message = com.emc.celerra.api.connector.util.Tool.stackTraceToString(th);
        log(flags, message);
    }
    public static synchronized void logErr(String message)
    {
        if (es != null) {
            try {
                String tname = Thread.currentThread().getName();
                tname = tname+": ";
                es.print(Tool.formatShortCurrentLocalDate()+": "+tname);
                es.println(message);
                es.flush();
            }
            catch (Exception ex) {
            }
        }
    }
    public static synchronized void logErr(Throwable th)
    {
        String message = com.emc.celerra.api.connector.util.Tool.stackTraceToString(th);
        logErr(message);
    }
}
