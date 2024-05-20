package com.edgescheduler.notificationservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimeStringUtils {

    public static String getShortMonthString(LocalDateTime dateTime) {
        return dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    public static String getDayOfWeekString(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    public static String formatPeriod(LocalDateTime startTime, LocalDateTime endTime) {

        DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.ENGLISH);

        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            return startTime.format(dateTimeformatter) + " - " + endTime.format(timeFormatter);
        }

        return startTime.format(dateTimeformatter) + " - " + endTime.format(dateTimeformatter);
    }
}
