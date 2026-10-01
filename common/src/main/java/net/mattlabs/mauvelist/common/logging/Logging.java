package net.mattlabs.mauvelist.common.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {

    public static Logger initializeLogging(String className) {
        // Logging with custom handler
        Logger rootLogger = Logger.getLogger("");

        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        rootLogger.addHandler(new ConsoleOutputHandler());
        rootLogger.setLevel(Level.INFO);

        return Logger.getLogger(className);
    }
}
