package com.edgescheduler.notificationservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("scheduleCreate")
public class ScheduleCreateNotification extends Notification {
    private Long scheduleId;
}
