package com.edgescheduler.notificationservice.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleUpdateTimeMessage extends NotificationMessage {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
}
