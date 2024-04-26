package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@TypeAlias("attendeeProposal")
public class AttendeeProposalNotification extends Notification {
    private Long scheduleId;
    private Integer attendeeId;
    private LocalDateTime proposalTime;
    private String proposal;
}
