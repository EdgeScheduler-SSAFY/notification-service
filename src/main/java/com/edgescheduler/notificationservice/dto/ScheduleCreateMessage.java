package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.ScheduleCreateNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleCreateMessage extends NotificationMessage {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScheduleCreateNotification toEntity() {
        return ScheduleCreateNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .build();
    }
}
