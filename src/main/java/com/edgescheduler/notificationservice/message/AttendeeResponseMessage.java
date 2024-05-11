package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.event.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseMessage extends NotificationMessage {

    private Long scheduleId;
    private String scheduleName;
    private Integer organizerId;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
