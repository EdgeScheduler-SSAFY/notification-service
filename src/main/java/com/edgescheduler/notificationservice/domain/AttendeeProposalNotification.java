package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("attendeeProposal")
public class AttendeeProposalNotification extends Notification {
    private Long scheduleId;
    private Integer attendeeId;
    private LocalDateTime proposalTime;
    private String proposal;
}
