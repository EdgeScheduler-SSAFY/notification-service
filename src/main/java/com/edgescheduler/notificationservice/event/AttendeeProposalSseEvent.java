package com.edgescheduler.notificationservice.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeProposalSseEvent extends NotificationSseEvent {

    private Integer attendeeId;
    private String attendeeName;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private Integer runningTime;
    private String reason;
}
