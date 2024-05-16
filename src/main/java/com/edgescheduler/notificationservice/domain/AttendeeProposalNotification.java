package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
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
    private Long proposalId;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private Integer runningTime;
    private String reason;

    public static AttendeeProposalNotification from(AttendeeProposalMessage message) {
        return AttendeeProposalNotification.builder()
            .receiverId(message.getOrganizerId())
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .attendeeId(message.getAttendeeId())
            .proposalId(message.getProposalId())
            .proposedStartTime(message.getProposedStartTime())
            .proposedEndTime(message.getProposedEndTime())
            .runningTime(message.getRunningTime())
            .reason(message.getReason())
            .build();
    }
}
