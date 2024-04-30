package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeProposalMessage extends NotificationMessage {

    private Integer attendeeId;
    private String attendeeName;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private String reason;

    public AttendeeProposalNotification toEntity() {
        return AttendeeProposalNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .attendeeId(attendeeId)
            .proposedStartTime(proposedStartTime)
            .proposedEndTime(proposedEndTime)
            .reason(reason)
            .build();
    }
}
