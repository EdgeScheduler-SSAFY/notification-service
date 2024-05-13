package com.edgescheduler.notificationservice.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseSseEvent extends NotificationSseEvent {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
