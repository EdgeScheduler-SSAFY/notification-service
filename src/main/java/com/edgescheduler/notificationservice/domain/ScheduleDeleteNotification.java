package com.edgescheduler.notificationservice.domain;

import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@TypeAlias("scheduleDelete")
public class ScheduleDeleteNotification extends Notification {
    private String scheduleName;
    private Long organizerId;
}
