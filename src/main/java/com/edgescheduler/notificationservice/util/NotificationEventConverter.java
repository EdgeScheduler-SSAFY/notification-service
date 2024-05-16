package com.edgescheduler.notificationservice.util;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient;
import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.event.AttendeeProposalEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateFieldsEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeEvent;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.client.UserServiceClient;
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

    public Mono<NotificationEvent> convert(Notification notification) {
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

        log.info("Unknown notification type: {}", notification.getClass().getName());
        return Mono.empty();
    }

    private Mono<NotificationEvent> convertToAttendeeProposalSseEvent(
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
                return AttendeeProposalEvent.convertFrom(
                    attendeeProposalNotification,
                    scheduleInfo,
                    attendeeInfo,
                    zonedProposedStartTime,
                    zonedProposedEndTime,
                    zonedOccurredAt);
            });
    }

    private Mono<NotificationEvent> convertToAttendeeResponseSseEvent(
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
                return AttendeeResponseEvent.convertFrom(
                    attendeeResponseNotification,
                    scheduleInfo,
                    attendeeInfo,
                    zonedOccurredAt);
            });
    }

    private Mono<NotificationEvent> convertToMeetingDeleteSseEvent(
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
                return MeetingDeleteEvent.convertFrom(
                    meetingDeleteNotification,
                    organizerInfo,
                    zonedOccurredAt);
            });
    }

    private Mono<NotificationEvent> convertToMeetingUpdateNotTimeSseEvent(
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
                    return MeetingUpdateFieldsEvent.convertFrom(
                        meetingUpdateNotTimeNotification,
                        scheduleInfo,
                        zonedStartTime,
                        zonedEndTime,
                        zonedOccurredAt
                    );
                }
            );
    }

    private Mono<NotificationEvent> convertToMeetingUpdateTimeSseEvent(
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
                    return MeetingUpdateTimeEvent.convertFrom(
                        meetingUpdateTimeNotification,
                        scheduleInfo,
                        zonedPreviousStartTime,
                        zonedPreviousEndTime,
                        zonedUpdatedStartTime,
                        zonedUpdatedEndTime,
                        zonedOccurredAt
                    );
                }
            );
    }

    private Mono<NotificationEvent> convertToMeetingCreateSseEvent(
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
                return MeetingCreateEvent.convertFrom(
                    meetingCreateNotification,
                    scheduleInfo,
                    zonedStartTime,
                    zonedEndTime,
                    zonedOccurredAt);
            });
    }
}
