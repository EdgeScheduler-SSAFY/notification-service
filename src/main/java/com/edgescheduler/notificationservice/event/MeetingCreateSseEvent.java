package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingCreateSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;
    private AttendeeStatus receiverStatus;

    public static MeetingCreateSseEvent from(
        MeetingCreateMessage message,
        MeetingCreateNotification notification) {
        return MeetingCreateSseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_CREATED)
            .occurredAt(notification.getOccurredAt())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .startTime(message.getStartTime())
            .endTime(message.getEndTime())
            .runningTime(message.getRunningTime())
            .receiverStatus(AttendeeStatus.PENDING)
            .isRead(notification.getIsRead())
            .build();
    }

    public static MeetingCreateSseEvent from(
        MeetingUpdateMessage message,
        MeetingCreateNotification notification) {
        return MeetingCreateSseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_CREATED)
            .occurredAt(notification.getOccurredAt())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .startTime(message.getUpdatedStartTime())
            .endTime(message.getUpdatedEndTime())
            .runningTime(message.getRunningTime())
            .receiverStatus(AttendeeStatus.PENDING)
            .isRead(notification.getIsRead())
            .build();
    }

    public static MeetingCreateSseEvent convertFrom(
        MeetingCreateNotification meetingCreateNotification,
        ScheduleInfo scheduleInfo,
        LocalDateTime zonedStartTime,
        LocalDateTime zonedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingCreateSseEvent.builder()
            .id(meetingCreateNotification.getId())
            .type(NotificationType.MEETING_CREATED)
            .receiverId(meetingCreateNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(meetingCreateNotification.getIsRead())
            .organizerId(scheduleInfo.getOrganizerId())
            .organizerName(scheduleInfo.getOrganizerName())
            .scheduleId(scheduleInfo.getScheduleId())
            .scheduleName(scheduleInfo.getName())
            .startTime(zonedStartTime)
            .endTime(zonedEndTime)
            .runningTime(scheduleInfo.getRunningTime())
            .receiverStatus(scheduleInfo.getReceiverStatus())
            .build();
    }
}
