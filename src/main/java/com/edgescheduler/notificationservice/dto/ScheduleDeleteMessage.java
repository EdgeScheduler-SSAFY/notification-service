package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.ScheduleDeleteNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleDeleteMessage extends NotificationMessage {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScheduleDeleteNotification toEntity() {
        return ScheduleDeleteNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .isRead(this.getIsRead())
            .scheduleName(this.getScheduleName())
            .organizerId(this.getOrganizerId())
            .startTime(this.getStartTime())
            .endTime(this.getEndTime())
            .build();
    }
}
