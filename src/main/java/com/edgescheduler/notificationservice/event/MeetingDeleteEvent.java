package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.edgescheduler.notificationservice.util.TimeStringUtils;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingDeleteEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;

    public static MeetingDeleteEvent from(
        MeetingDeleteMessage message,
        MeetingDeleteNotification notification) {
        return MeetingDeleteEvent.builder()
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

    public static MeetingDeleteEvent from(
        MeetingUpdateMessage message,
        MeetingDeleteNotification notification) {
        return MeetingDeleteEvent.builder()
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

    public static MeetingDeleteEvent convertFrom(
        MeetingDeleteNotification meetingDeleteNotification,
        UserInfo organizerInfo,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingDeleteEvent.builder()
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

    @Override
    public String getTemplateName() {
        return "meeting-delete";
    }

    @Override
    public Mono<Context> emailContext() {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("organizerName", organizerName);
            context.setVariable("title", super.getScheduleName());
            context.setVariable("date", TimeStringUtils.formatPeriod(startTime, endTime));
            return context;
        });
    }
}
