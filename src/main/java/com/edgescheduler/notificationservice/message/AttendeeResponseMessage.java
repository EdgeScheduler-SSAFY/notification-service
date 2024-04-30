package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.dto.Response;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AttendeeResponseMessage extends EventMessage {

    private Long meetingId;
    private String meetingName;
    private Integer organizerId;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
