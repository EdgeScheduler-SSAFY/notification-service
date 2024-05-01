package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleCreateEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public MeetingCreateNotification toEntity() {
        return MeetingCreateNotification.builder()
            .receiverId(this.getReceiverId())
            .occurredAt(this.getOccurredAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .build();
    }
}
