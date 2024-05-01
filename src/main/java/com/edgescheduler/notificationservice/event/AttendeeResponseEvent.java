package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseEvent extends NotificationEvent {

    private Integer attendeeId;
    private String attendeeName;
    private Response response;

    public AttendeeResponseNotification toEntity() {
        return AttendeeResponseNotification.builder()
            .receiverId(this.getReceiverId())
            .occurredAt(this.getOccurredAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .attendeeId(attendeeId)
            .response(response)
            .build();
    }
}
