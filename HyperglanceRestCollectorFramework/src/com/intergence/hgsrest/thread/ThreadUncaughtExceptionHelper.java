package com.intergence.hgsrest.thread;

import org.apache.log4j.Logger;

/**
 * Created by stephen on 11/04/2015.
 */
public class ThreadUncaughtExceptionHelper {

    private static Logger log = Logger.getLogger(ThreadUncaughtExceptionHelper.class);

    public static void setLoggingDefaultUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
    }

    private static class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable e) {
            try {
                log.error("UNCAUGHT EXCEPTION in thread [" + thread + "]", e);
            } catch (Throwable t) {
                // Swallow!! If any exception escapes here could get infinite loop.
            }
        }
    }
}
