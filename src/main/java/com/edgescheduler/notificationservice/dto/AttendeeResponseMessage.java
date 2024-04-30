package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseMessage extends NotificationMessage {

    private Integer attendeeId;
    private String attendeeName;
    private Response response;

    public AttendeeResponseNotification toEntity() {
        return AttendeeResponseNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .attendeeId(attendeeId)
            .response(response)
            .build();
    }
}
