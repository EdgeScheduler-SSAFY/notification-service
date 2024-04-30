package com.edgescheduler.notificationservice.message;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AttendeeProposalMessage extends EventMessage {

    private Long meetingId;
    private String meetingName;
    private Integer organizerId;
    private Integer attendeeId;
    private String attendeeName;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private String reason;
}
