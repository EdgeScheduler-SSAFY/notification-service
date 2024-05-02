package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateNotTimeSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<UpdatedField> updatedFields;

    public MeetingUpdateNotTimeNotification toEntity() {
        return MeetingUpdateNotTimeNotification.builder()
            .receiverId(this.getReceiverId())
            .occurredAt(this.getOccurredAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .updatedName(this.getScheduleName())
            .updatedFields(this.getUpdatedFields())
            .build();
    }
}
