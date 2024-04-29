package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("scheduleDelete")
public class ScheduleDeleteNotification extends Notification {
    private String scheduleName;
    private Long organizerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
