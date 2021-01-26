package com.intergence.hgsrest.logging;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 11/04/2015.
 */
public class LoggingOutputStream extends OutputStream {

    public static final int DEFAULT_BUFFER_LENGTH = 2048;

    public enum Priority {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }

    private boolean hasBeenClosed = false;
    private byte[] buf;
    private int count;
    private int bufLength;
    private final Logger log;
    private final Priority priority;

    public LoggingOutputStream(Logger log, Priority priority) throws IllegalArgumentException {
        this.log = checkNotNull(log, "log must not be null");
        this.priority = checkNotNull(priority, "priority must not be null");

        bufLength = DEFAULT_BUFFER_LENGTH;
        buf = new byte[DEFAULT_BUFFER_LENGTH];
        count = 0;
    }

    public void close() {
        flush();
        hasBeenClosed = true;
    }

    public void write(final int b) throws IOException {
        if (hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        }

        // don't log nulls
        if (b == 0) {
            return;
        }

        // would this be writing past the buffer?
        if (count == bufLength) {
            // grow the buffer
            final int newBufLength = bufLength+DEFAULT_BUFFER_LENGTH;
            final byte[] newBuf = new byte[newBufLength];

            System.arraycopy(buf, 0, newBuf, 0, bufLength);

            buf = newBuf;
            bufLength = newBufLength;
        }

        buf[count] = (byte)b;
        count++;
    }

    public void flush() {
        if (count == 0) {
            return;
        }

        // don't print out blank lines; flushing from PrintStream puts out these
        if (count == System.getProperty("line.separator").length()) {
            if ( ((char)buf[0]) == System.getProperty("line.separator").charAt(0)  &&
                    ( ( count == 1 ) ||  // <- Unix & Mac, -> Windows
                            ( (count == 2) && ((char)buf[1]) == System.getProperty("line.separator").charAt(1) ) ) ) {
                reset();
                return;
            }
        }

        switch (priority) {
            case TRACE:
                if (log.isTraceEnabled()) log.trace(getMessageString());
                break;
            case DEBUG:
                if (log.isDebugEnabled()) log.debug(getMessageString());
                break;
            case INFO:
                if (log.isInfoEnabled()) log.info(getMessageString());
                break;
            case WARN:
                log.warn(getMessageString());
                break;
            case ERROR:
                log.error(getMessageString());
                break;
        }

        reset();
    }

    private String getMessageString() {
        final byte[] theBytes = new byte[count];
        System.arraycopy(buf, 0, theBytes, 0, count);
        return new String(theBytes);
    }


    private void reset() {
        // not resetting the buffer -- assuming that if it grew that it
        //   will likely grow similarly again
        count = 0;
    }

}
