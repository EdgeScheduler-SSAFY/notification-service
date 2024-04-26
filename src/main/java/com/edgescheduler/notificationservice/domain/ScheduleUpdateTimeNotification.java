package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("scheduleUpdateTime")
public class ScheduleUpdateTimeNotification extends Notification {
    private Long scheduleId;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
}
