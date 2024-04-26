package com.edgescheduler.notificationservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("attendeeResponse")
public class AttendeeResponseNotification extends Notification {
    private Long scheduleId;
    private Long attendeeId;
    private String response;
}
