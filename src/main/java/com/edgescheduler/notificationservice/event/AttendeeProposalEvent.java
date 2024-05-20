package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.util.TimeStringUtils;
import com.edgescheduler.notificationservice.util.TimeZoneConvertUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AttendeeProposalEvent extends NotificationEvent {

    private Integer attendeeId;
    private String attendeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long proposalId;
    private LocalDateTime proposedStartTime;
    private LocalDateTime proposedEndTime;
    private Integer runningTime;
    private String reason;

    public static AttendeeProposalEvent from(
        AttendeeProposalMessage message,
        AttendeeProposalNotification notification,
        ZoneId zoneId) {
        LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(notification.getOccurredAt(), zoneId);
        LocalDateTime zonedStartTime = TimeZoneConvertUtils.convertToZone(message.getStartTime(), zoneId);
        LocalDateTime zonedEndTime = TimeZoneConvertUtils.convertToZone(message.getEndTime(), zoneId);
        LocalDateTime zonedProposedStartTime = TimeZoneConvertUtils.convertToZone(message.getProposedStartTime(), zoneId);
        LocalDateTime zonedProposedEndTime = TimeZoneConvertUtils.convertToZone(message.getProposedEndTime(), zoneId);
        return AttendeeProposalEvent.builder()
            .id(notification.getId())
            .type(NotificationType.ATTENDEE_PROPOSAL)
            .receiverId(notification.getReceiverId())
            .occurredAt(zonedOccurredAt)
            .isRead(notification.getIsRead())
            .scheduleId(notification.getScheduleId())
            .scheduleName(message.getScheduleName())
            .startTime(zonedStartTime)
            .endTime(zonedEndTime)
            .attendeeId(notification.getAttendeeId())
            .attendeeName(message.getAttendeeName())
            .proposalId(notification.getProposalId())
            .proposedStartTime(zonedProposedStartTime)
            .proposedEndTime(zonedProposedEndTime)
            .runningTime(notification.getRunningTime())
            .reason(notification.getReason())
            .build();
    }

    public static AttendeeProposalEvent convertFrom(
        AttendeeProposalNotification attendeeProposalNotification,
        ScheduleInfo scheduleInfo,
        UserInfo attendeeInfo,
        LocalDateTime zonedProposedStartTime,
        LocalDateTime zonedProposedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return AttendeeProposalEvent.builder()
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
            .proposalId(attendeeProposalNotification.getProposalId())
            .proposedStartTime(zonedProposedStartTime)
            .proposedEndTime(zonedProposedEndTime)
            .runningTime(attendeeProposalNotification.getRunningTime())
            .build();
    }

    @Override
    public String getTemplateName() {
        return "attendee-proposal";
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
            context.setVariable("current", TimeStringUtils.formatPeriod(startTime, endTime));
            context.setVariable("suggested", TimeStringUtils.formatPeriod(proposedStartTime, proposedEndTime));
            return context;
        });
    }
}
