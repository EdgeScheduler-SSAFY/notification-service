package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.domain.MemberTimezone;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.feign.client.UserServiceClient;
import com.edgescheduler.notificationservice.service.MemberTimezoneService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationEventConverter {

    private final UserServiceClient userServiceClient;
    private final MemberTimezoneService memberTimezoneService;

    public Mono<NotificationSseEvent> convert(Notification notification) {
        if (notification instanceof MeetingCreateNotification meetingCreateNotification) {
            return convertToMeetingCreateSseEvent(meetingCreateNotification);
        }
        if (notification instanceof MeetingUpdateTimeNotification meetingUpdateTimeNotification) {
            return convertToMeetingUpdateTimeSseEvent(meetingUpdateTimeNotification);
        }
        if (notification instanceof MeetingUpdateNotTimeNotification meetingUpdateNotTimeNotification) {
            return convertToMeetingUpdateNotTimeSseEvent(meetingUpdateNotTimeNotification);
        }
        if (notification instanceof MeetingDeleteNotification meetingDeleteNotification) {
            return convertToMeetingDeleteSseEvent(meetingDeleteNotification);
        }
        if (notification instanceof AttendeeResponseNotification attendeeResponseNotification) {
            return convertToAttendeeResponseSseEvent(attendeeResponseNotification);
        }
        if (notification instanceof AttendeeProposalNotification attendeeProposalNotification) {
            return convertToAttendeeProposalSseEvent(attendeeProposalNotification);
        }

        return Mono.empty();
    }

    private Mono<NotificationSseEvent> convertToAttendeeProposalSseEvent(
        AttendeeProposalNotification attendeeProposalNotification) {
        return null;
    }

    private Mono<NotificationSseEvent> convertToAttendeeResponseSseEvent(
        AttendeeResponseNotification attendeeResponseNotification) {
        return null;
    }

    private Mono<NotificationSseEvent> convertToMeetingDeleteSseEvent(
        MeetingDeleteNotification meetingDeleteNotification) {
        return null;
    }

    private Mono<NotificationSseEvent> convertToMeetingUpdateNotTimeSseEvent(
        MeetingUpdateNotTimeNotification meetingUpdateNotTimeNotification) {
        return null;
    }

    private Mono<NotificationSseEvent> convertToMeetingUpdateTimeSseEvent(
        MeetingUpdateTimeNotification meetingUpdateTimeNotification) {
        return null;
    }

    private Mono<NotificationSseEvent> convertToMeetingCreateSseEvent(MeetingCreateNotification meetingCreateNotification) {

        return memberTimezoneService.getZoneIdOfMember(meetingCreateNotification.getReceiverId())
            .map(memberTimezone -> {
                LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                    meetingCreateNotification.getOccurredAt(), memberTimezone);
                MeetingCreateSseEvent build = MeetingCreateSseEvent.builder()
                    .id(meetingCreateNotification.getId())
                    .receiverId(meetingCreateNotification.getReceiverId())
                    .occurredAt(zonedOccurredAt)
                    .isRead(meetingCreateNotification.getIsRead())
                    .build();
                return build;
            });
    }


}
