package nl.juraji.imagemanager.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

/**
 * Created by Juraji on 26-1-2019.
 * Image Manager 2
 */
public final class DateUtils {
    private DateUtils() {
    }

    public static String formatDefault(TemporalAccessor instant) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }
}
