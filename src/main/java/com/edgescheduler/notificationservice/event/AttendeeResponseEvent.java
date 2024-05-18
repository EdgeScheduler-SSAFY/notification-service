package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
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
public class AttendeeResponseEvent extends NotificationEvent {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer attendeeId;
    private String attendeeName;
    private Response response;

    public static AttendeeResponseEvent from(
        AttendeeResponseMessage message,
        AttendeeResponseNotification notification) {
        return AttendeeResponseEvent.builder()
            .id(notification.getId())
            .receiverId(notification.getReceiverId())
            .type(NotificationType.ATTENDEE_RESPONSE)
            .occurredAt(notification.getOccurredAt())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .startTime(message.getStartTime())
            .endTime(message.getEndTime())
            .attendeeId(notification.getAttendeeId())
            .attendeeName(message.getAttendeeName())
            .response(notification.getResponse())
            .isRead(notification.getIsRead())
            .build();
    }

    public static AttendeeResponseEvent convertFrom(
        AttendeeResponseNotification attendeeResponseNotification,
        ScheduleInfo scheduleInfo,
        UserInfo attendeeInfo,
        LocalDateTime zonedOccurredAt
    ) {
        return AttendeeResponseEvent.builder()
            .id(attendeeResponseNotification.getId())
            .type(NotificationType.ATTENDEE_RESPONSE)
            .receiverId(attendeeResponseNotification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(attendeeResponseNotification.getIsRead())
            .scheduleId(scheduleInfo.getScheduleId())
            .scheduleName(scheduleInfo.getName())
            .startTime(scheduleInfo.getStartDatetime())
            .endTime(scheduleInfo.getEndDatetime())
            .attendeeId(attendeeResponseNotification.getAttendeeId())
            .attendeeName(attendeeInfo.getName())
            .response(attendeeResponseNotification.getResponse())
            .build();
    }

    @Override
    public String getTemplateName() {
        return response == Response.ACCEPTED ? "attendee-response-accepted" : "attendee-response-declined";
    }

    @Override
    public Mono<Context> emailContext() {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("attendeeName", attendeeName);
            context.setVariable("title", super.getScheduleName());
            context.setVariable("month", TimeStringUtils.getShortMonthString(startTime));
            context.setVariable("dayOfMonth", startTime.getDayOfMonth());
            context.setVariable("dayOfWeek", TimeStringUtils.getDayOfWeekString(startTime));
            context.setVariable("date", TimeStringUtils.formatPeriod(startTime, endTime));
            return context;
        });
    }
}
