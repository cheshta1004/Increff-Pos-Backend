package com.increff.pos.util;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    public static String formatToIST(ZonedDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        return utcDateTime.withZoneSameInstant(IST_ZONE).format(formatter);
    }

    public static ZonedDateTime getCurrentUTCDateTime() {
        return ZonedDateTime.now(UTC_ZONE);
    }

    public static ZonedDateTime parseToUTC(String dateStr) {
        return ZonedDateTime.parse(dateStr + "T00:00:00Z");
    }

    public static ZonedDateTime parseToUTCEndOfDay(String dateStr) {
        return ZonedDateTime.parse(dateStr + "T23:59:59Z");
    }
} 