package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.dto.Response;
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
    private Integer attendeeId;
    private Response response;
}
