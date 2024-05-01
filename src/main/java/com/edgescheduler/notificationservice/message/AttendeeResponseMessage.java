package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.event.Response;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AttendeeResponseMessage extends EventMessage {

    private Long scheduleId;
    private String scheduleName;
    private Integer organizerId;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
