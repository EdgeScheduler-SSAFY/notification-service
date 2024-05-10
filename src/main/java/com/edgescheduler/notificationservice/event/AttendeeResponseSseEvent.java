package com.edgescheduler.notificationservice.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseSseEvent extends NotificationSseEvent {

    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
