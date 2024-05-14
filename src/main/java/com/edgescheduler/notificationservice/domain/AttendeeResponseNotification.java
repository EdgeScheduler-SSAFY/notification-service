package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("attendeeResponse")
public class AttendeeResponseNotification extends Notification {
    private Long scheduleId;
    private Integer attendeeId;
    private Response response;

    public static AttendeeResponseNotification from(AttendeeResponseMessage message) {
        return AttendeeResponseNotification.builder()
            .receiverId(message.getOrganizerId())
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .attendeeId(message.getAttendeeId())
            .response(message.getResponse())
            .build();
    }
}
