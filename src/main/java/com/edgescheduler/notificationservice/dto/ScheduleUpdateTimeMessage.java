package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.ScheduleUpdateTimeNotification;
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

    public ScheduleUpdateTimeNotification toEntity() {
        return ScheduleUpdateTimeNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .previousStartTime(this.getPreviousStartTime())
            .previousEndTime(this.getPreviousEndTime())
            .updatedStartTime(this.getUpdatedStartTime())
            .updatedEndTime(this.getUpdatedEndTime())
            .build();
    }
}
