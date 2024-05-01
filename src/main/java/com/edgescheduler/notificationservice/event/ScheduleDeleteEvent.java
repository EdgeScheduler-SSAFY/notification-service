package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleDeleteEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public MeetingDeleteNotification toEntity() {
        return MeetingDeleteNotification.builder()
            .receiverId(this.getReceiverId())
            .occurredAt(this.getOccurredAt())
            .isRead(this.getIsRead())
            .scheduleName(this.getScheduleName())
            .organizerId(this.getOrganizerId())
            .startTime(this.getStartTime())
            .endTime(this.getEndTime())
            .build();
    }
}
