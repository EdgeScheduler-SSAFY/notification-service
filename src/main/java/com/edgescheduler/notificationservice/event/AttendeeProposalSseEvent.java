package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private Integer runningTime;
    private String reason;

    public static AttendeeProposalSseEvent from(
        AttendeeProposalMessage message,
        AttendeeProposalNotification notification) {
        return AttendeeProposalSseEvent.builder()
            .id(notification.getId())
            .type(NotificationType.ATTENDEE_PROPOSAL)
            .receiverId(notification.getReceiverId())
            .occurredAt(notification.getOccurredAt())
            .isRead(notification.getIsRead())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .startTime(message.getStartTime())
            .endTime(message.getEndTime())
            .attendeeId(notification.getAttendeeId())
            .attendeeName(message.getAttendeeName())
            .proposedStartTime(notification.getProposedStartTime())
            .proposedEndTime(notification.getProposedEndTime())
            .runningTime(notification.getRunningTime())
            .reason(notification.getReason())
            .build();
    }

    public static AttendeeProposalSseEvent convertFrom(
        AttendeeProposalNotification attendeeProposalNotification,
        ScheduleInfo scheduleInfo,
        UserInfo attendeeInfo,
        LocalDateTime zonedProposedStartTime,
        LocalDateTime zonedProposedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return AttendeeProposalSseEvent.builder()
            .id(attendeeProposalNotification.getId())
            .type(NotificationType.ATTENDEE_PROPOSAL)
            .receiverId(attendeeProposalNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(attendeeProposalNotification.getIsRead())
            .scheduleId(scheduleInfo.getScheduleId())
            .scheduleName(scheduleInfo.getName())
            .startTime(scheduleInfo.getStartDatetime())
            .endTime(scheduleInfo.getEndDatetime())
            .attendeeId(attendeeProposalNotification.getAttendeeId())
            .attendeeName(attendeeInfo.getName())
            .proposedStartTime(zonedProposedStartTime)
            .proposedEndTime(zonedProposedEndTime)
            .runningTime(attendeeProposalNotification.getRunningTime())
            .build();
    }
}
