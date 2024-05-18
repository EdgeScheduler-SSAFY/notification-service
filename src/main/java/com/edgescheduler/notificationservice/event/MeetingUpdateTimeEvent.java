package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
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
public class MeetingUpdateTimeEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
    private Integer runningTime;
    private AttendeeStatus receiverStatus;

    public static MeetingUpdateTimeEvent from(
        MeetingUpdateMessage message,
        MeetingUpdateTimeNotification notification) {
        return MeetingUpdateTimeEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.MEETING_UPDATED_TIME)
            .occurredAt(notification.getOccurredAt())
            .isRead(notification.getIsRead())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .organizerName(message.getOrganizerName())
            .previousStartTime(notification.getPreviousStartTime())
            .previousEndTime(notification.getPreviousEndTime())
            .updatedStartTime(notification.getUpdatedStartTime())
            .updatedEndTime(notification.getUpdatedEndTime())
            .runningTime(notification.getRunningTime())
            .receiverStatus(AttendeeStatus.PENDING)
            .build();
    }

    public static MeetingUpdateTimeEvent convertFrom(
        MeetingUpdateTimeNotification meetingUpdateTimeNotification,
        ScheduleInfo scheduleInfo,
        LocalDateTime zonedPreviousStartTime,
        LocalDateTime zonedPreviousEndTime,
        LocalDateTime zonedUpdatedStartTime,
        LocalDateTime zonedUpdatedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingUpdateTimeEvent.builder()
            .id(meetingUpdateTimeNotification.getId())
            .type(NotificationType.MEETING_UPDATED_TIME)
            .receiverId(meetingUpdateTimeNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(meetingUpdateTimeNotification.getIsRead())
            .organizerId(scheduleInfo.getOrganizerId())
            .organizerName(scheduleInfo.getOrganizerName())
            .scheduleId(scheduleInfo.getScheduleId())
            .scheduleName(scheduleInfo.getName())
            .previousStartTime(zonedPreviousStartTime)
            .previousEndTime(zonedPreviousEndTime)
            .updatedStartTime(zonedUpdatedStartTime)
            .updatedEndTime(zonedUpdatedEndTime)
            .runningTime(scheduleInfo.getRunningTime())
            .receiverStatus(scheduleInfo.getReceiverStatus())
            .build();
    }

    @Override
    public String getTemplateName() {
        return "meeting-update-time";
    }

    @Override
    public Mono<Context> emailContext() {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("organizerName", organizerName);
            context.setVariable("title", super.getScheduleName());
            context.setVariable("month", TimeStringUtils.getShortMonthString(updatedStartTime));
            context.setVariable("dayOfMonth", updatedStartTime.getDayOfMonth());
            context.setVariable("dayOfWeek", TimeStringUtils.getDayOfWeekString(updatedStartTime));
            context.setVariable("before", TimeStringUtils.formatPeriod(previousStartTime, previousEndTime));
            context.setVariable("updated", TimeStringUtils.formatPeriod(updatedStartTime, updatedEndTime));
            return context;
        });
    }
}
