package com.edgescheduler.notificationservice.service;

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
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.edgescheduler.notificationservice.repository.NotificationRepository;
import com.edgescheduler.notificationservice.util.NotificationEventConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEventConverter notificationEventConverter;

    @Override
    public Publisher<NotificationSseEvent> saveNotificationFromEventMessage(
        KafkaEventMessage eventMessage) {

        log.info("Received event message: {}", eventMessage.getOccurredAt());
        if (eventMessage instanceof MeetingCreateMessage meetingCreateMessage) {
            return saveAndGetMeetingCreateEvent(meetingCreateMessage);
        }
        if (eventMessage instanceof MeetingDeleteMessage meetingDeleteMessage) {
            return saveAndGetMeetingDeleteEvent(meetingDeleteMessage);
        }
        if (eventMessage instanceof MeetingUpdateMessage meetingUpdateMessage) {
            return saveAndGetMeetingUpdateEvent(meetingUpdateMessage);
        }
        if (eventMessage instanceof AttendeeResponseMessage attendeeResponseMessage) {
            return saveAndGetAttendeeResponseEvent(attendeeResponseMessage);
        }
        if (eventMessage instanceof AttendeeProposalMessage attendeeProposalMessage) {
            return saveAndGetAttendeeProposalEvent(attendeeProposalMessage);
        }

        return Flux.empty();
    }

    private Flux<NotificationSseEvent> saveAndGetMeetingCreateEvent(
        MeetingCreateMessage meetingCreateMessage) {
        return Mono.just(meetingCreateMessage)
            .flatMapMany(message -> {
                List<Integer> attendeeIds = message.getAttendeeIds();
                attendeeIds.remove(message.getOrganizerId());
                List<MeetingCreateNotification> notifications = attendeeIds.stream()
                    .map(
                        attendeeId -> (MeetingCreateNotification) MeetingCreateNotification.builder()
                            .receiverId(attendeeId)
                            .occurredAt(message.getOccurredAt())
                            .isRead(false)
                            .scheduleId(message.getScheduleId())
                            .build())
                    .toList();
                return notificationRepository.saveAll(notifications);
            }).map(notification -> {
                log.info("Notification saved: {}", notification.getId());
                return MeetingCreateSseEvent.builder()
                    .id(notification.getId())
                    .receiverId(notification.getReceiverId())
                    .type(NotificationType.MEETING_CREATED)
                    .occurredAt(notification.getOccurredAt())
                    .scheduleId(notification.getScheduleId())
                    .scheduleName(meetingCreateMessage.getScheduleName())
                    .organizerId(meetingCreateMessage.getOrganizerId())
                    .organizerName(meetingCreateMessage.getOrganizerName())
                    .startTime(meetingCreateMessage.getStartTime())
                    .endTime(meetingCreateMessage.getEndTime())
                    .isRead(notification.getIsRead())
                    .build();
            });
    }

    private Flux<NotificationSseEvent> saveAndGetMeetingUpdateEvent(
        MeetingUpdateMessage meetingUpdateMessage) {
        return Mono.just(meetingUpdateMessage)
            .flatMapMany(message -> {
                List<Integer> maintainedAttendeeIds = message.getMaintainedAttendeeIds();
                maintainedAttendeeIds.remove(message.getOrganizerId());
                List<Integer> addedAttendeeIds = message.getAddedAttendeeIds();
                addedAttendeeIds.remove(message.getOrganizerId());
                List<Integer> removedAttendeeIds = message.getRemovedAttendeeIds();
                removedAttendeeIds.remove(message.getOrganizerId());

                Flux<NotificationSseEvent> addedEvents = Mono.just(
                        addedAttendeeIds.stream().map(
                            attendeeId -> (MeetingCreateNotification) MeetingCreateNotification.builder()
                                .receiverId(attendeeId)
                                .occurredAt(message.getOccurredAt())
                                .isRead(false)
                                .scheduleId(message.getScheduleId())
                                .build()).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingCreateSseEvent.builder()
                        .id(notification.getId())
                        .receiverId(notification.getReceiverId())
                        .type(NotificationType.MEETING_CREATED)
                        .occurredAt(notification.getOccurredAt())
                        .scheduleId(notification.getScheduleId())
                        .scheduleName(meetingUpdateMessage.getScheduleName())
                        .organizerId(meetingUpdateMessage.getOrganizerId())
                        .organizerName(meetingUpdateMessage.getOrganizerName())
                        .startTime(meetingUpdateMessage.getUpdatedStartTime())
                        .endTime(meetingUpdateMessage.getUpdatedEndTime())
                        .isRead(notification.getIsRead())
                        .build());

                Flux<NotificationSseEvent> removedEvents = Mono.just(
                        removedAttendeeIds.stream().map(
                            attendeeId -> (MeetingDeleteNotification) MeetingDeleteNotification.builder()
                                .receiverId(attendeeId)
                                .occurredAt(message.getOccurredAt())
                                .isRead(false)
                                .scheduleName(message.getScheduleName())
                                .organizerId(message.getOrganizerId())
                                .startTime(message.getUpdatedStartTime())
                                .endTime(message.getUpdatedEndTime())
                                .build()).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingDeleteSseEvent.builder()
                        .id(notification.getId())
                        .receiverId(notification.getReceiverId())
                        .type(NotificationType.MEETING_DELETED)
                        .occurredAt(notification.getOccurredAt())
                        .scheduleName(notification.getScheduleName())
                        .organizerId(notification.getOrganizerId())
                        .organizerName(meetingUpdateMessage.getOrganizerName())
                        .startTime(notification.getStartTime())
                        .endTime(notification.getEndTime())
                        .isRead(notification.getIsRead())
                        .build());

                Flux<NotificationSseEvent> updatedEvents = Mono.just(
                        maintainedAttendeeIds.stream().map(
                            attendeeId -> (MeetingUpdateNotTimeNotification) MeetingUpdateNotTimeNotification.builder()
                                .receiverId(attendeeId)
                                .occurredAt(message.getOccurredAt())
                                .isRead(false)
                                .scheduleId(message.getScheduleId())
                                .updatedFields(message.getUpdatedFields())
                                .build()).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingUpdateNotTimeSseEvent.builder()
                        .id(notification.getId())
                        .receiverId(notification.getReceiverId())
                        .type(NotificationType.MEETING_UPDATED_FIELDS)
                        .occurredAt(notification.getOccurredAt())
                        .isRead(notification.getIsRead())
                        .scheduleId(notification.getScheduleId())
                        .scheduleName(meetingUpdateMessage.getScheduleName())
                        .organizerId(meetingUpdateMessage.getOrganizerId())
                        .organizerName(meetingUpdateMessage.getOrganizerName())
                        .startTime(meetingUpdateMessage.getUpdatedStartTime())
                        .endTime(meetingUpdateMessage.getUpdatedEndTime())
                        .updatedFields(notification.getUpdatedFields())
                        .build());

                if (meetingUpdateMessage.getUpdatedFields().contains(UpdatedField.TIME)) {
                    updatedEvents = Flux.mergeSequential(updatedEvents,
                        Mono.just(
                                maintainedAttendeeIds.stream().map(
                                    attendeeId -> (MeetingUpdateTimeNotification) MeetingUpdateTimeNotification.builder()
                                        .receiverId(attendeeId)
                                        .occurredAt(message.getOccurredAt())
                                        .isRead(false)
                                        .scheduleId(message.getScheduleId())
                                        .previousStartTime(message.getPreviousStartTime())
                                        .previousEndTime(message.getPreviousEndTime())
                                        .updatedStartTime(message.getUpdatedStartTime())
                                        .updatedEndTime(message.getUpdatedEndTime())
                                        .build()).toList()
                            ).flatMapMany(notificationRepository::saveAll)
                            .map(notification -> MeetingUpdateTimeSseEvent.builder()
                                .id(notification.getId())
                                .receiverId(notification.getReceiverId())
                                .type(NotificationType.MEETING_UPDATED_TIME)
                                .occurredAt(notification.getOccurredAt())
                                .isRead(notification.getIsRead())
                                .scheduleId(notification.getScheduleId())
                                .scheduleName(meetingUpdateMessage.getScheduleName())
                                .organizerId(meetingUpdateMessage.getOrganizerId())
                                .organizerName(meetingUpdateMessage.getOrganizerName())
                                .previousStartTime(notification.getPreviousStartTime())
                                .previousEndTime(notification.getPreviousEndTime())
                                .updatedStartTime(notification.getUpdatedStartTime())
                                .updatedEndTime(notification.getUpdatedEndTime())
                                .build())
                    );
                }

                return Flux.merge(addedEvents, removedEvents, updatedEvents);
            });
    }

    private Flux<NotificationSseEvent> saveAndGetMeetingDeleteEvent(MeetingDeleteMessage
        meetingDeleteMessage) {
        return Mono.just(meetingDeleteMessage)
            .flatMapMany(message -> {
                List<Integer> attendeeIds = message.getAttendeeIds();
                attendeeIds.remove(message.getOrganizerId());
                List<MeetingDeleteNotification> notifications = attendeeIds.stream()
                    .map(
                        attendeeId -> (MeetingDeleteNotification) MeetingDeleteNotification.builder()
                            .receiverId(attendeeId)
                            .occurredAt(message.getOccurredAt())
                            .isRead(false)
                            .scheduleName(message.getScheduleName())
                            .organizerId(message.getOrganizerId())
                            .startTime(message.getStartTime())
                            .endTime(message.getEndTime())
                            .build()).toList();
                return notificationRepository.saveAll(notifications);
            }).map(notification -> MeetingDeleteSseEvent.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .type(NotificationType.MEETING_DELETED)
                .occurredAt(notification.getOccurredAt())
                .scheduleName(notification.getScheduleName())
                .organizerId(notification.getOrganizerId())
                .organizerName(meetingDeleteMessage.getOrganizerName())
                .startTime(notification.getStartTime())
                .endTime(notification.getEndTime())
                .isRead(notification.getIsRead())
                .build());
    }

    private Mono<NotificationSseEvent> saveAndGetAttendeeResponseEvent(AttendeeResponseMessage
        attendeeResponseMessage) {
        return Mono.just(attendeeResponseMessage)
            .flatMap(message -> {
                AttendeeResponseNotification notification = AttendeeResponseNotification.builder()
                    .receiverId(message.getOrganizerId())
                    .occurredAt(message.getOccurredAt())
                    .isRead(false)
                    .scheduleId(message.getScheduleId())
                    .attendeeId(message.getAttendeeId())
                    .response(message.getResponse())
                    .build();
                return notificationRepository.save(notification);
            }).map(notification -> AttendeeResponseSseEvent.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .type(NotificationType.ATTENDEE_RESPONSE)
                .occurredAt(notification.getOccurredAt())
                .scheduleId(notification.getScheduleId())
                .scheduleName(attendeeResponseMessage.getScheduleName())
                .attendeeId(notification.getAttendeeId())
                .attendeeName(attendeeResponseMessage.getAttendeeName())
                .response(notification.getResponse())
                .isRead(notification.getIsRead())
                .build());
    }

    private Mono<NotificationSseEvent> saveAndGetAttendeeProposalEvent(AttendeeProposalMessage
        attendeeProposalMessage) {
        return Mono.just(attendeeProposalMessage)
            .flatMap(message -> {
                AttendeeProposalNotification notification = AttendeeProposalNotification.builder()
                    .receiverId(message.getOrganizerId())
                    .occurredAt(message.getOccurredAt())
                    .isRead(false)
                    .scheduleId(message.getScheduleId())
                    .attendeeId(message.getAttendeeId())
                    .proposedStartTime(message.getProposedStartTime())
                    .proposedEndTime(message.getProposedEndTime())
                    .reason(message.getReason())
                    .build();
                return notificationRepository.save(notification);
            }).map(notification -> AttendeeProposalSseEvent.builder()
                .id(notification.getId())
                .type(NotificationType.ATTENDEE_PROPOSAL)
                .receiverId(notification.getReceiverId())
                .occurredAt(notification.getOccurredAt())
                .isRead(notification.getIsRead())
                .scheduleId(notification.getScheduleId())
                .scheduleName(attendeeProposalMessage.getScheduleName())
                .attendeeId(notification.getAttendeeId())
                .attendeeName(attendeeProposalMessage.getAttendeeName())
                .proposedStartTime(notification.getProposedStartTime())
                .proposedEndTime(notification.getProposedEndTime())
                .reason(notification.getReason())
                .build());
    }

    @Override
    public Flux<NotificationSseEvent> getNotificationsByReceiverIdWithin2Day(Integer receiverId) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Flux<Notification> notifications = notificationRepository.findByReceiverIdAndOccurredAtGreaterThanEqual(
            receiverId, now.minusDays(2));
        return notifications.flatMapSequential(notificationEventConverter::convert);
    }

    @Override
    public Mono<Void> markAsRead(Long notificationId) {
        return notificationRepository.markAsRead(notificationId);
    }

    @Override
    public Mono<Void> markAllAsRead(List<Long> notificationIds) {
        return notificationRepository.markAllAsRead(notificationIds);
    }


}
