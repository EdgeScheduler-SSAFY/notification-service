package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.dto.NotificationPage;
import com.edgescheduler.notificationservice.event.AttendeeProposalSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeStatus;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateNotTimeSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEventConverter notificationEventConverter;

    public Publisher<NotificationSseEvent> saveNotificationFromEventMessage(
        NotificationMessage eventMessage) {

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
                        attendeeId -> MeetingCreateNotification.from(attendeeId, meetingCreateMessage)
                    ).toList();
                return notificationRepository.saveAll(notifications);
            })
            .map(notification -> MeetingCreateSseEvent.from(meetingCreateMessage, notification));
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
                            attendeeId -> MeetingCreateNotification.from(attendeeId, message)
                        ).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingCreateSseEvent.from(meetingUpdateMessage, notification));

                Flux<NotificationSseEvent> removedEvents = Mono.just(
                        removedAttendeeIds.stream().map(
                            attendeeId -> MeetingDeleteNotification.from(attendeeId, meetingUpdateMessage)
                        ).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingDeleteSseEvent.from(meetingUpdateMessage, notification));

                Flux<NotificationSseEvent> updatedEvents = Mono.just(
                        maintainedAttendeeIds.stream().map(
                            attendeeId -> MeetingUpdateNotTimeNotification.from(attendeeId, meetingUpdateMessage)
                        ).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .map(notification -> MeetingUpdateNotTimeSseEvent.from(meetingUpdateMessage, notification));

                if (meetingUpdateMessage.getUpdatedFields().contains(UpdatedField.TIME)) {
                    updatedEvents = Flux.mergeSequential(
                        updatedEvents,
                        Mono.just(
                                maintainedAttendeeIds.stream().map(
                                    attendeeId -> MeetingUpdateTimeNotification.from(attendeeId, meetingUpdateMessage)
                                ).toList()
                            ).flatMapMany(notificationRepository::saveAll)
                            .map(notification -> MeetingUpdateTimeSseEvent.from(meetingUpdateMessage, notification))
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
                        attendeeId -> MeetingDeleteNotification.from(attendeeId, meetingDeleteMessage)
                    ).toList();
                return notificationRepository.saveAll(notifications);
            }).map(notification -> MeetingDeleteSseEvent.from(meetingDeleteMessage, notification));
    }

    private Mono<NotificationSseEvent> saveAndGetAttendeeResponseEvent(AttendeeResponseMessage
        attendeeResponseMessage) {
        return Mono.just(attendeeResponseMessage)
            .flatMap(message -> {
                var notification = AttendeeResponseNotification.from(message);
                return notificationRepository.save(notification);
            }).map(notification -> AttendeeResponseSseEvent.from(attendeeResponseMessage,
                notification));
    }

    private Mono<NotificationSseEvent> saveAndGetAttendeeProposalEvent(AttendeeProposalMessage
        attendeeProposalMessage) {
        return Mono.just(attendeeProposalMessage)
            .flatMap(message -> {
                var notification = AttendeeProposalNotification.from(message);
                return notificationRepository.save(notification);
            }).map(notification -> AttendeeProposalSseEvent.from(attendeeProposalMessage,
                notification));
    }

    public Flux<NotificationSseEvent> getNotificationsByReceiverIdWithin2Weeks(Integer receiverId) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Flux<Notification> notifications = notificationRepository.findNotificationsAfter(
            receiverId, now.minusDays(14));
        return notifications.flatMapSequential(notificationEventConverter::convert);
    }

    public Mono<NotificationPage> getNotificationsByReceiverIdWithin2WeeksWithPaging(
        Integer receiverId, Integer page, Integer size) {
        return notificationRepository.findNotificationsAfterWithPaging(
                receiverId, LocalDateTime.now().minusDays(14), PageRequest.of(page, size))
            .flatMapSequential(notificationEventConverter::convert)
            .collectList()
            .zipWith(notificationRepository.countByReceiverIdAndOccurredAtGreaterThanEqual(
                receiverId, LocalDateTime.now().minusDays(14)))
            .map(tuple -> {
                List<NotificationSseEvent> data = tuple.getT1();
                Integer total = Math.toIntExact(tuple.getT2());
                return NotificationPage.builder()
                    .page(page)
                    .size(size)
                    .totalPages((total + size - 1) / size)
                    .totalElements(total)
                    .data(data)
                    .build();
            });
    }

    public Mono<Void> markAsRead(String notificationId) {
        return notificationRepository.markAsRead(notificationId);
    }

    public Mono<Void> markAllAsRead(List<String> notificationIds) {
        return notificationRepository.markAllAsRead(notificationIds);
    }
}
