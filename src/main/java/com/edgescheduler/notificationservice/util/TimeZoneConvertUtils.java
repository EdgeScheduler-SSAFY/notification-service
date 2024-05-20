package com.edgescheduler.notificationservice.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeZoneConvertUtils {

    public static LocalDateTime convertToZone(LocalDateTime time, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = time.atZone(ZoneOffset.UTC);
        return zonedDateTime.withZoneSameInstant(zoneId).toLocalDateTime();
    }

    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("America/Santiago");
        System.out.println(convertToZone(time, zoneId));
    }
}
