package net.mattlabs.mauvelist.api.logging;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

// Adjusts log message format and sends to System.out
public class ConsoleOutputHandler extends StreamHandler {

    public ConsoleOutputHandler() {
        super(System.out, new ConsoleOutputFormatter());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public synchronized void close() {
        flush();
    }
}
