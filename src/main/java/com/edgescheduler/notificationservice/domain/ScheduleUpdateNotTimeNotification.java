package com.edgescheduler.notificationservice.domain;

import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@TypeAlias("scheduleUpdateNotTime")
public class ScheduleUpdateNotTimeNotification extends Notification {
    private Long scheduleId;
    private String updatedName;
    private String updatedDescription;
}
