package com.edgescheduler.notificationservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("scheduleUpdateNotTime")
public class ScheduleUpdateNotTimeNotification extends Notification {
    private Long scheduleId;
    private String updatedName;
    private String updatedDescription;
}
