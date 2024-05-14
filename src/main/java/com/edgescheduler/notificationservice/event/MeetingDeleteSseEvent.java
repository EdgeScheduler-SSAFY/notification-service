package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingDeleteSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;

    public static MeetingDeleteSseEvent from(
        MeetingDeleteMessage message,
        MeetingDeleteNotification notification) {
        return MeetingDeleteSseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_DELETED)
            .occurredAt(notification.getOccurredAt())
            .scheduleName(notification.getScheduleName())
            .organizerId(notification.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .startTime(notification.getStartTime())
            .endTime(notification.getEndTime())
            .runningTime(notification.getRunningTime())
            .isRead(notification.getIsRead())
            .build();
    }

    public static MeetingDeleteSseEvent from(
        MeetingUpdateMessage message,
        MeetingDeleteNotification notification) {
        return MeetingDeleteSseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_DELETED)
            .occurredAt(notification.getOccurredAt())
            .scheduleName(notification.getScheduleName())
            .organizerId(notification.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .startTime(notification.getStartTime())
            .endTime(notification.getEndTime())
            .runningTime(notification.getRunningTime())
            .isRead(notification.getIsRead())
            .build();
    }

    public static MeetingDeleteSseEvent convertFrom(
        MeetingDeleteNotification meetingDeleteNotification,
        UserInfo organizerInfo,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingDeleteSseEvent.builder()
            .id(meetingDeleteNotification.getId())
            .type(NotificationType.MEETING_DELETED)
            .receiverId(meetingDeleteNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(meetingDeleteNotification.getIsRead())
            .organizerId(meetingDeleteNotification.getOrganizerId())
            .organizerName(organizerInfo.getName())
            .scheduleId(null)
            .scheduleName(meetingDeleteNotification.getScheduleName())
            .startTime(meetingDeleteNotification.getStartTime())
            .endTime(meetingDeleteNotification.getEndTime())
            .runningTime(meetingDeleteNotification.getRunningTime())
            .build();
    }
}
