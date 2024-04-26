package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@TypeAlias("scheduleUpdateTime")
public class ScheduleUpdateTimeNotification extends Notification {
    private Long scheduleId;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
}
