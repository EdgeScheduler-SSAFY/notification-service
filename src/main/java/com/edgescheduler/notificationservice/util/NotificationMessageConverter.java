package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.ScheduleCreateNotification;
import com.edgescheduler.notificationservice.domain.ScheduleDeleteNotification;
import com.edgescheduler.notificationservice.domain.ScheduleUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.ScheduleUpdateTimeNotification;
import com.edgescheduler.notificationservice.dto.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.dto.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.edgescheduler.notificationservice.dto.ScheduleCreateMessage;
import com.edgescheduler.notificationservice.dto.ScheduleDeleteMessage;
import com.edgescheduler.notificationservice.dto.ScheduleUpdateNotTimeMessage;
import com.edgescheduler.notificationservice.dto.ScheduleUpdateTimeMessage;

public class NotificationMessageConverter {

    public static Notification convertToNotification(NotificationMessage notificationMessage) {
        return switch (notificationMessage.getType()) {
            case SCHEDULE_CREATED -> convertToScheduleCreateNotification(
                (ScheduleCreateMessage) notificationMessage);
            case SCHEDULE_UPDATED_NOT_TIME -> convertToScheduleUpdateNotTimeNotification(
                (ScheduleUpdateNotTimeMessage) notificationMessage);
            case SCHEDULE_UPDATED_TIME -> convertToScheduleUpdateTimeNotification(
                (ScheduleUpdateTimeMessage) notificationMessage);
            case SCHEDULE_DELETED -> convertToScheduleDeleteNotification(
                (ScheduleDeleteMessage) notificationMessage);
            case ATTENDEE_RESPONSE -> convertToAttendeeResponseNotification(
                (AttendeeResponseMessage) notificationMessage);
            case ATTENDEE_SCHEDULE_PROPOSAL -> convertToAttendeeProposalNotification(
                (AttendeeProposalMessage) notificationMessage);
        };
    }

    private static ScheduleCreateNotification convertToScheduleCreateNotification(
        ScheduleCreateMessage notificationMessage) {
        return ScheduleCreateNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .build();
    }

    private static ScheduleUpdateNotTimeNotification convertToScheduleUpdateNotTimeNotification(
        ScheduleUpdateNotTimeMessage notificationMessage) {
        return ScheduleUpdateNotTimeNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .updatedName(notificationMessage.getScheduleName())
            .updatedFields(notificationMessage.getUpdatedFields())
            .build();
    }

    private static ScheduleUpdateTimeNotification convertToScheduleUpdateTimeNotification(
        ScheduleUpdateTimeMessage notificationMessage) {
        return ScheduleUpdateTimeNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .previousStartTime(notificationMessage.getPreviousStartTime())
            .previousEndTime(notificationMessage.getPreviousEndTime())
            .updatedStartTime(notificationMessage.getUpdatedStartTime())
            .updatedEndTime(notificationMessage.getUpdatedEndTime())
            .build();
    }

    private static ScheduleDeleteNotification convertToScheduleDeleteNotification(
        ScheduleDeleteMessage notificationMessage) {
        return ScheduleDeleteNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .isRead(notificationMessage.getIsRead())
            .scheduleName(notificationMessage.getScheduleName())
            .organizerId(notificationMessage.getOrganizerId())
            .startTime(notificationMessage.getStartTime())
            .endTime(notificationMessage.getEndTime())
            .build();
    }

    private static AttendeeResponseNotification convertToAttendeeResponseNotification(
        AttendeeResponseMessage notificationMessage) {
        return AttendeeResponseNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .attendeeId(notificationMessage.getAttendeeId())
            .response(notificationMessage.getResponse())
            .build();
    }

    private static AttendeeProposalNotification convertToAttendeeProposalNotification(
        AttendeeProposalMessage notificationMessage) {
        return AttendeeProposalNotification.builder()
            .receiverId(notificationMessage.getReceiverId())
            .notifiedAt(notificationMessage.getNotifiedAt())
            .scheduleId(notificationMessage.getScheduleId())
            .isRead(notificationMessage.getIsRead())
            .attendeeId(notificationMessage.getAttendeeId())
            .proposedStartTime(notificationMessage.getProposedStartTime())
            .proposedEndTime(notificationMessage.getProposedEndTime())
            .reason(notificationMessage.getReason())
            .build();
    }
}
