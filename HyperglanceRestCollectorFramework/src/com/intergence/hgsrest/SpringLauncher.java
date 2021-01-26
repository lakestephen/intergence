package com.intergence.hgsrest;

import com.google.common.base.Strings;
import com.intergence.hgsrest.logging.LoggingOutputStream;
import com.intergence.hgsrest.thread.ThreadUncaughtExceptionHelper;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintStream;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by stephen on 27/01/2015.
 */
public class SpringLauncher {

    private static Logger log = Logger.getLogger(SpringLauncher.class);

    static {
        ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();

        System.setErr(new PrintStream(new LoggingOutputStream(Logger.getLogger("stderr"), LoggingOutputStream.Priority.WARN), true));
        System.setOut(new PrintStream(new LoggingOutputStream(Logger.getLogger("stdout"), LoggingOutputStream.Priority.INFO), true));
    }

    public static void main(String[] args) {

        try {
            log.info("Starting Spring ...");
            start(args);
            log.info("Finished Spring ...");
        }
        catch (Exception e) {
            log.error("Exception launching Spring", e);
            System.out.print(usage());
        }

    }

    private static void start(String[] args) {
        checkState(args.length == 1 || args.length == 2, "Wrong number of command line arguments [%s].", Arrays.toString(args));
        String contextFile = args[0];
        checkState(!Strings.isNullOrEmpty(contextFile), "Command line arg null or empty");

        log.info("Running using spring file [" + contextFile + "]");

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextFile);

        log.info("Spring Wiring finished...");

        if (args.length == 2) {
            String beanName = args[1];
            log.info("Running Runnable bean [" + beanName + "]");
            Runnable bean = (Runnable)applicationContext.getBean(beanName);
            bean.run();
        }
    }

    private static Object usage() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Usage:").append(System.getProperty("line.separator"));
        buffer.append("    SpringLauncher applicationContextFileName [Runnable beanName to run]").append(System.getProperty("line.separator"));
        return buffer;
    }
}
