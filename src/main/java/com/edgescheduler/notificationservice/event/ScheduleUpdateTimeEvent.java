package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleUpdateTimeEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;

    public MeetingUpdateTimeNotification toEntity() {
        return MeetingUpdateTimeNotification.builder()
            .receiverId(this.getReceiverId())
            .occurredAt(this.getOccurredAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .previousStartTime(this.getPreviousStartTime())
            .previousEndTime(this.getPreviousEndTime())
            .updatedStartTime(this.getUpdatedStartTime())
            .updatedEndTime(this.getUpdatedEndTime())
            .build();
    }
}
