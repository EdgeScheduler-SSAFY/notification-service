package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.event.Response;
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

    // temporary
    private String scheduleName;
    private String attendeeName;
}
