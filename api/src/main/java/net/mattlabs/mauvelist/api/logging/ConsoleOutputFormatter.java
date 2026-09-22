package net.mattlabs.mauvelist.api.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// Formats log messages for easy readability
public class ConsoleOutputFormatter extends Formatter {

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public String format(LogRecord record) {
        return String.format(
                "[%s %s]: %s%n",
                timeFormat.format(Instant.ofEpochMilli(record.getMillis())),
                record.getLevel().getName(),
                formatMessage(record)
        );
    }
}
