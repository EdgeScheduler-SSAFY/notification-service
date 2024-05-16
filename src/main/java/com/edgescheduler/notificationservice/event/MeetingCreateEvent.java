package com.edgescheduler.notificationservice.event;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingCreateEvent extends NotificationEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;
    private AttendeeStatus receiverStatus;

    public static MeetingCreateEvent from(
        MeetingCreateMessage message,
        MeetingCreateNotification notification) {
        return MeetingCreateEvent.builder()
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

    public static MeetingCreateEvent from(
        MeetingUpdateMessage message,
        MeetingCreateNotification notification) {
        return MeetingCreateEvent.builder()
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

    public static MeetingCreateEvent convertFrom(
        MeetingCreateNotification meetingCreateNotification,
        ScheduleInfo scheduleInfo,
        LocalDateTime zonedStartTime,
        LocalDateTime zonedEndTime,
        LocalDateTime zonedOccurredAt
    ) {
        return MeetingCreateEvent.builder()
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

    @Override
    public String getTemplateName() {
        return "meeting-create";
    }

    @Override
    public Mono<Context> emailContext() {
        return Mono.fromCallable(() -> {
            Context context = new Context();
            context.setVariable("organizerName", organizerName);
            context.setVariable("scheduleName", super.getScheduleName());
            context.setVariable("startTime", startTime);
            context.setVariable("endTime", endTime);
            return context;
        });
    }
}
