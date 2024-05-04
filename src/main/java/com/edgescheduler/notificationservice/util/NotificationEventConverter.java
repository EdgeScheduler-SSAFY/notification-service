package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.event.AttendeeProposalSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateNotTimeSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeSseEvent;

public class NotificationEventConverter {

    public static Notification convertToNotification(NotificationSseEvent notificationEvent) {
        return switch (notificationEvent.getType()) {
            case MEETING_CREATED -> convertToScheduleCreateNotification(
                (MeetingCreateSseEvent) notificationEvent);
            case MEETING_UPDATED -> convertToScheduleUpdateNotTimeNotification(
                (MeetingUpdateNotTimeSseEvent) notificationEvent);
            case MEETING_DELETED -> convertToScheduleDeleteNotification(
                (MeetingDeleteSseEvent) notificationEvent);
            case ATTENDEE_RESPONSE -> convertToAttendeeResponseNotification(
                (AttendeeResponseSseEvent) notificationEvent);
            case ATTENDEE_PROPOSAL -> convertToAttendeeProposalNotification(
                (AttendeeProposalSseEvent) notificationEvent);
        };
    }

    private static MeetingCreateNotification convertToScheduleCreateNotification(
        MeetingCreateSseEvent notificationMessage) {
        return MeetingCreateNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .occurredAt(notificationMessage.getOccurredAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .build();
    }

    private static MeetingUpdateNotTimeNotification convertToScheduleUpdateNotTimeNotification(
        MeetingUpdateNotTimeSseEvent notificationEvent) {
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
        MeetingUpdateTimeSseEvent notificationEvent) {
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
        MeetingDeleteSseEvent notificationEvent) {
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
        AttendeeResponseSseEvent notificationEvent) {
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
        AttendeeProposalSseEvent notificationEvent) {
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
