package com.edgescheduler.notificationservice.domain;

import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@TypeAlias("scheduleCreate")
public class ScheduleCreateNotification extends Notification {
    private Long scheduleId;
}
