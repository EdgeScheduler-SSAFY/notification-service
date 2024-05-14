package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateNotTimeSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;
    private List<UpdatedField> updatedFields;

    public static MeetingUpdateNotTimeSseEvent from(
        MeetingUpdateMessage message,
        MeetingUpdateNotTimeNotification notification) {
        return MeetingUpdateNotTimeSseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_UPDATED_FIELDS)
            .occurredAt(notification.getOccurredAt())
            .isRead(notification.getIsRead())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .startTime(message.getUpdatedStartTime())
            .endTime(message.getUpdatedEndTime())
            .runningTime(message.getRunningTime())
            .updatedFields(notification.getUpdatedFields())
            .build();
    }

    public static MeetingUpdateNotTimeSseEvent convertFrom(
        MeetingUpdateNotTimeNotification meetingUpdateNotTimeNotification,
        ScheduleInfo scheduleInfo,
        LocalDateTime zonedStartTime,
        LocalDateTime zonedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingUpdateNotTimeSseEvent.builder()
            .id(meetingUpdateNotTimeNotification.getId())
            .type(NotificationType.MEETING_UPDATED_FIELDS)
            .receiverId(meetingUpdateNotTimeNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(meetingUpdateNotTimeNotification.getIsRead())
            .organizerId(scheduleInfo.getOrganizerId())
            .organizerName(scheduleInfo.getOrganizerName())
            .scheduleId(scheduleInfo.getScheduleId())
            .scheduleName(scheduleInfo.getName())
            .startTime(zonedStartTime)
            .endTime(zonedEndTime)
            .runningTime(scheduleInfo.getRunningTime())
            .updatedFields(meetingUpdateNotTimeNotification.getUpdatedFields())
            .build();
    }
}
