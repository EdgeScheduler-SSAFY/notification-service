package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient;
import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.event.AttendeeProposalSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseSseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateNotTimeSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.client.UserServiceClient;
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.service.MemberInfoService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConverter {

    private final UserServiceClient userServiceClient;
    private final ScheduleServiceClient scheduleServiceClient;
    private final MemberInfoService memberInfoService;

    public Mono<NotificationSseEvent> convert(Notification notification) {
        if (notification instanceof MeetingCreateNotification meetingCreateNotification) {
            log.info("convertToMeetingCreateSseEvent");
            return convertToMeetingCreateSseEvent(meetingCreateNotification);
        }
        if (notification instanceof MeetingUpdateTimeNotification meetingUpdateTimeNotification) {
            log.info("convertToMeetingUpdateTimeSseEvent");
            return convertToMeetingUpdateTimeSseEvent(meetingUpdateTimeNotification);
        }
        if (notification instanceof MeetingUpdateNotTimeNotification meetingUpdateNotTimeNotification) {
            log.info("convertToMeetingUpdateNotTimeSseEvent");
            return convertToMeetingUpdateNotTimeSseEvent(meetingUpdateNotTimeNotification);
        }
        if (notification instanceof MeetingDeleteNotification meetingDeleteNotification) {
            log.info("convertToMeetingDeleteSseEvent");
            return convertToMeetingDeleteSseEvent(meetingDeleteNotification);
        }
        if (notification instanceof AttendeeResponseNotification attendeeResponseNotification) {
            log.info("convertToAttendeeResponseSseEvent");
            return convertToAttendeeResponseSseEvent(attendeeResponseNotification);
        }
        if (notification instanceof AttendeeProposalNotification attendeeProposalNotification) {
            log.info("convertToAttendeeProposalSseEvent");
            return convertToAttendeeProposalSseEvent(attendeeProposalNotification);
        }

        log.info("Unknown notification type: {}", notification.getClass().getName());
        return Mono.empty();
    }

    private Mono<NotificationSseEvent> convertToAttendeeProposalSseEvent(
        AttendeeProposalNotification attendeeProposalNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(attendeeProposalNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                scheduleServiceClient.getSchedule(
                    attendeeProposalNotification.getScheduleId(),
                    attendeeProposalNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                userServiceClient.getUserInfo(attendeeProposalNotification.getAttendeeId())
                    .subscribeOn(Schedulers.boundedElastic()))
            .map(tuple -> {
                ZoneId memberTimezone = tuple.getT1();
                var scheduleInfo = tuple.getT2();
                var attendeeInfo = tuple.getT3();
                LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                    attendeeProposalNotification.getOccurredAt(), memberTimezone);
                LocalDateTime zonedProposedStartTime = TimeZoneConvertUtils.convertToZone(
                    attendeeProposalNotification.getProposedStartTime(), memberTimezone);
                LocalDateTime zonedProposedEndTime = TimeZoneConvertUtils.convertToZone(
                    attendeeProposalNotification.getProposedEndTime(), memberTimezone);
                return AttendeeProposalSseEvent.builder()
                    .id(attendeeProposalNotification.getId())
                    .type(NotificationType.ATTENDEE_PROPOSAL)
                    .receiverId(attendeeProposalNotification.getReceiverId())
                    .occurredAt(zonedOccurredAt)
                    .isRead(attendeeProposalNotification.getIsRead())
                    .scheduleId(scheduleInfo.getScheduleId())
                    .scheduleName(scheduleInfo.getName())
                    .attendeeId(attendeeProposalNotification.getAttendeeId())
                    .attendeeName(attendeeInfo.getName())
                    .proposedStartTime(zonedProposedStartTime)
                    .proposedEndTime(zonedProposedEndTime)
                    .runningTime(attendeeProposalNotification.getRunningTime())
                    .build();
            });
    }

    private Mono<NotificationSseEvent> convertToAttendeeResponseSseEvent(
        AttendeeResponseNotification attendeeResponseNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(attendeeResponseNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                scheduleServiceClient.getSchedule(
                        attendeeResponseNotification.getScheduleId(),
                        attendeeResponseNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                userServiceClient.getUserInfo(attendeeResponseNotification.getAttendeeId())
                    .subscribeOn(Schedulers.boundedElastic()))
            .map(tuple -> {
                ZoneId memberTimezone = tuple.getT1();
                var scheduleInfo = tuple.getT2();
                var attendeeInfo = tuple.getT3();
                LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                    attendeeResponseNotification.getOccurredAt(), memberTimezone);
                return AttendeeResponseSseEvent.builder()
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
            });
    }

    private Mono<NotificationSseEvent> convertToMeetingDeleteSseEvent(
        MeetingDeleteNotification meetingDeleteNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(meetingDeleteNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                userServiceClient.getUserInfo(meetingDeleteNotification.getOrganizerId())
                    .subscribeOn(Schedulers.boundedElastic())
            )
            .map(tuple -> {
                ZoneId memberTimezone = tuple.getT1();
                var organizerInfo = tuple.getT2();
                LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                    meetingDeleteNotification.getOccurredAt(), memberTimezone);
                return MeetingDeleteSseEvent.builder()
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
            });
    }

    private Mono<NotificationSseEvent> convertToMeetingUpdateNotTimeSseEvent(
        MeetingUpdateNotTimeNotification meetingUpdateNotTimeNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(
                        meetingUpdateNotTimeNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                scheduleServiceClient.getSchedule(
                        meetingUpdateNotTimeNotification.getScheduleId(),
                        meetingUpdateNotTimeNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()))
            .map(tuple -> {
                    ZoneId memberTimezone = tuple.getT1();
                    var scheduleInfo = tuple.getT2();
                    LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateNotTimeNotification.getOccurredAt(), memberTimezone);
                    LocalDateTime zonedStartTime = TimeZoneConvertUtils.convertToZone(
                        scheduleInfo.getStartDatetime(), memberTimezone);
                    LocalDateTime zonedEndTime = TimeZoneConvertUtils.convertToZone(
                        scheduleInfo.getEndDatetime(), memberTimezone);
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
            );
    }

    private Mono<NotificationSseEvent> convertToMeetingUpdateTimeSseEvent(
        MeetingUpdateTimeNotification meetingUpdateTimeNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(meetingUpdateTimeNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                scheduleServiceClient.getSchedule(
                        meetingUpdateTimeNotification.getScheduleId(),
                        meetingUpdateTimeNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()))
            .map(tuple -> {
                    ZoneId memberTimezone = tuple.getT1();
                    var scheduleInfo = tuple.getT2();
                    LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateTimeNotification.getOccurredAt(), memberTimezone);
                    LocalDateTime zonedPreviousStartTime = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateTimeNotification.getPreviousStartTime(), memberTimezone);
                    LocalDateTime zonedPreviousEndTime = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateTimeNotification.getPreviousEndTime(), memberTimezone);
                    LocalDateTime zonedUpdatedStartTime = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateTimeNotification.getUpdatedStartTime(), memberTimezone);
                    LocalDateTime zonedUpdatedEndTime = TimeZoneConvertUtils.convertToZone(
                        meetingUpdateTimeNotification.getUpdatedEndTime(), memberTimezone);
                    return MeetingUpdateTimeSseEvent.builder()
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
            );
    }

    private Mono<NotificationSseEvent> convertToMeetingCreateSseEvent(
        MeetingCreateNotification meetingCreateNotification) {
        return Mono.zip(
                memberInfoService.getZoneIdOfMember(meetingCreateNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic()),
                scheduleServiceClient.getSchedule(
                        meetingCreateNotification.getScheduleId(),
                        meetingCreateNotification.getReceiverId())
                    .subscribeOn(Schedulers.boundedElastic())
            )
            .map(tuple -> {
                ZoneId memberTimezone = tuple.getT1();
                var scheduleInfo = tuple.getT2();
                LocalDateTime zonedOccurredAt = TimeZoneConvertUtils.convertToZone(
                    meetingCreateNotification.getOccurredAt(), memberTimezone);
                LocalDateTime zonedStartTime = TimeZoneConvertUtils.convertToZone(
                    scheduleInfo.getStartDatetime(), memberTimezone);
                LocalDateTime zonedEndTime = TimeZoneConvertUtils.convertToZone(
                    scheduleInfo.getEndDatetime(), memberTimezone);
                return MeetingCreateSseEvent.builder()
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
            });
    }


}
