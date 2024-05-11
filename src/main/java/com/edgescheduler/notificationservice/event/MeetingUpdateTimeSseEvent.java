package com.edgescheduler.notificationservice.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateTimeSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
    private AttendeeStatus receiverStatus;
}
