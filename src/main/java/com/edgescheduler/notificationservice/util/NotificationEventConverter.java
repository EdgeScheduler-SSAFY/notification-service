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
                return AttendeeProposalSseEvent.convertFrom(
                    attendeeProposalNotification,
                    scheduleInfo,
                    attendeeInfo,
                    zonedProposedStartTime,
                    zonedProposedEndTime,
                    zonedOccurredAt);
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
                return AttendeeResponseSseEvent.convertFrom(
                    attendeeResponseNotification,
                    scheduleInfo,
                    attendeeInfo,
                    zonedOccurredAt);
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
                return MeetingDeleteSseEvent.convertFrom(
                    meetingDeleteNotification,
                    organizerInfo,
                    zonedOccurredAt);
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
                    return MeetingUpdateNotTimeSseEvent.convertFrom(
                        meetingUpdateNotTimeNotification,
                        scheduleInfo,
                        zonedStartTime,
                        zonedEndTime,
                        zonedOccurredAt
                    );
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
                    return MeetingUpdateTimeSseEvent.convertFrom(
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
                return MeetingCreateSseEvent.convertFrom(
                    meetingCreateNotification,
                    scheduleInfo,
                    zonedStartTime,
                    zonedEndTime,
                    zonedOccurredAt);
            });
    }
}
