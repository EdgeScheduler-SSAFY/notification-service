package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.event.AttendeeProposalEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseEvent;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.event.ScheduleCreateEvent;
import com.edgescheduler.notificationservice.event.ScheduleDeleteEvent;
import com.edgescheduler.notificationservice.event.ScheduleUpdateNotTimeEvent;
import com.edgescheduler.notificationservice.event.ScheduleUpdateTimeEvent;

public class NotificationEventConverter {

    public static Notification convertToNotification(NotificationEvent notificationEvent) {
        return switch (notificationEvent.getType()) {
            case SCHEDULE_CREATED -> convertToScheduleCreateNotification(
                (ScheduleCreateEvent) notificationEvent);
            case SCHEDULE_UPDATED -> convertToScheduleUpdateNotTimeNotification(
                (ScheduleUpdateNotTimeEvent) notificationEvent);
            case SCHEDULE_DELETED -> convertToScheduleDeleteNotification(
                (ScheduleDeleteEvent) notificationEvent);
            case ATTENDEE_RESPONSE -> convertToAttendeeResponseNotification(
                (AttendeeResponseEvent) notificationEvent);
            case ATTENDEE_SCHEDULE_PROPOSAL -> convertToAttendeeProposalNotification(
                (AttendeeProposalEvent) notificationEvent);
        };
    }

    private static MeetingCreateNotification convertToScheduleCreateNotification(
        ScheduleCreateEvent notificationMessage) {
        return MeetingCreateNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .occurredAt(notificationMessage.getOccurredAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .build();
    }

    private static MeetingUpdateNotTimeNotification convertToScheduleUpdateNotTimeNotification(
        ScheduleUpdateNotTimeEvent notificationEvent) {
        return MeetingUpdateNotTimeNotification.builder()
            .receiverId(notificationEvent.getReceiverId())
            .occurredAt(notificationEvent.getOccurredAt())
            .scheduleId(notificationEvent.getScheduleId())
            .isRead(notificationEvent.getIsRead())
            .updatedName(notificationEvent.getScheduleName())
            .updatedFields(notificationEvent.getUpdatedFields())
            .build();
    }

    private static MeetingUpdateTimeNotification convertToScheduleUpdateTimeNotification(
        ScheduleUpdateTimeEvent notificationEvent) {
        return MeetingUpdateTimeNotification.builder()
            .receiverId(notificationEvent.getReceiverId())
            .occurredAt(notificationEvent.getOccurredAt())
            .scheduleId(notificationEvent.getScheduleId())
            .isRead(notificationEvent.getIsRead())
            .previousStartTime(notificationEvent.getPreviousStartTime())
            .previousEndTime(notificationEvent.getPreviousEndTime())
            .updatedStartTime(notificationEvent.getUpdatedStartTime())
            .updatedEndTime(notificationEvent.getUpdatedEndTime())
            .build();
    }

    private static MeetingDeleteNotification convertToScheduleDeleteNotification(
        ScheduleDeleteEvent notificationEvent) {
        return MeetingDeleteNotification.builder()
            .receiverId(notificationEvent.getReceiverId())
            .occurredAt(notificationEvent.getOccurredAt())
            .isRead(notificationEvent.getIsRead())
            .scheduleName(notificationEvent.getScheduleName())
            .organizerId(notificationEvent.getOrganizerId())
            .startTime(notificationEvent.getStartTime())
            .endTime(notificationEvent.getEndTime())
            .build();
    }

    private static AttendeeResponseNotification convertToAttendeeResponseNotification(
        AttendeeResponseEvent notificationEvent) {
        return AttendeeResponseNotification.builder()
            .receiverId(notificationEvent.getReceiverId())
            .occurredAt(notificationEvent.getOccurredAt())
            .scheduleId(notificationEvent.getScheduleId())
            .isRead(notificationEvent.getIsRead())
            .attendeeId(notificationEvent.getAttendeeId())
            .response(notificationEvent.getResponse())
            .build();
    }

    private static AttendeeProposalNotification convertToAttendeeProposalNotification(
        AttendeeProposalEvent notificationEvent) {
        return AttendeeProposalNotification.builder()
            .receiverId(notificationEvent.getReceiverId())
            .occurredAt(notificationEvent.getOccurredAt())
            .scheduleId(notificationEvent.getScheduleId())
            .isRead(notificationEvent.getIsRead())
            .attendeeId(notificationEvent.getAttendeeId())
            .proposedStartTime(notificationEvent.getProposedStartTime())
            .proposedEndTime(notificationEvent.getProposedEndTime())
            .reason(notificationEvent.getReason())
            .build();
    }
}
