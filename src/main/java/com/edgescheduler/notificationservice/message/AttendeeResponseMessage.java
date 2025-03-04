package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.event.Response;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeResponseMessage extends NotificationMessage {

    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer organizerId;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;
}
