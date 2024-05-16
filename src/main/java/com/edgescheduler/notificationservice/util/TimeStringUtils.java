package com.edgescheduler.notificationservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeStringUtils {

    public static String formatPeriod(LocalDateTime startTime, LocalDateTime endTime) {

        DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.ENGLISH);

        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            return startTime.format(dateTimeformatter) + " - " + endTime.format(timeFormatter);
        }

        return startTime.format(dateTimeformatter) + " - " + endTime.format(dateTimeformatter);
    }
}
