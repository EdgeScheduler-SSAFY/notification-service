package com.edgescheduler.notificationservice.dto;

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

    public enum Response {
        ACCEPTED,
        DECLINED
    }
}
