package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.dto.NotificationPage;
import com.edgescheduler.notificationservice.event.AttendeeProposalEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateFieldsEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeEvent;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
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
    private final MemberInfoService memberInfoService;

    public Publisher<NotificationEvent> saveNotificationFromEventMessage(
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

    private Flux<NotificationEvent> saveAndGetMeetingCreateEvent(
        MeetingCreateMessage meetingCreateMessage) {
        return Mono.just(meetingCreateMessage)
            .flatMapMany(message -> {
                List<Integer> attendeeIds = message.getAttendeeIds();
                attendeeIds.remove(message.getOrganizerId());
                List<MeetingCreateNotification> notifications = attendeeIds.stream()
                    .map(
                        attendeeId -> MeetingCreateNotification.from(attendeeId,
                            meetingCreateMessage)
                    ).toList();
                return notificationRepository.saveAll(notifications);
            }).flatMap(notification -> Mono.zip(
                Mono.just(notification),
                memberInfoService.getZoneIdOfMember(notification.getReceiverId())
            )).map(tuple2 -> {
                var notification = tuple2.getT1();
                var zoneId = tuple2.getT2();
                return MeetingCreateEvent.from(meetingCreateMessage, notification, zoneId);
            });
    }

    private Flux<NotificationEvent> saveAndGetMeetingUpdateEvent(
        MeetingUpdateMessage meetingUpdateMessage) {
        return Mono.just(meetingUpdateMessage)
            .flatMapMany(message -> {
                List<Integer> maintainedAttendeeIds = message.getMaintainedAttendeeIds();
                maintainedAttendeeIds.remove(message.getOrganizerId());
                List<Integer> addedAttendeeIds = message.getAddedAttendeeIds();
                addedAttendeeIds.remove(message.getOrganizerId());
                List<Integer> removedAttendeeIds = message.getRemovedAttendeeIds();
                removedAttendeeIds.remove(message.getOrganizerId());

                Flux<NotificationEvent> addedEvents = Mono.just(
                        addedAttendeeIds.stream().map(
                            attendeeId -> MeetingCreateNotification.from(attendeeId, message)
                        ).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .flatMap(notification -> Mono.zip(
                        Mono.just(notification),
                        memberInfoService.getZoneIdOfMember(notification.getReceiverId())
                    ))
                    .map(tuple2 -> {
                        var notification = tuple2.getT1();
                        var zoneId = tuple2.getT2();
                        return MeetingCreateEvent.from(meetingUpdateMessage, notification, zoneId);
                    });

                Flux<NotificationEvent> removedEvents =
                    Flux.fromIterable(removedAttendeeIds)
                        .flatMap(
                            attendeeId -> notificationRepository.deleteByReceiverIdAndScheduleId(
                                attendeeId, meetingUpdateMessage.getScheduleId())
                        ).then(Mono.just(
                            removedAttendeeIds.stream().map(
                                attendeeId -> MeetingDeleteNotification.from(attendeeId,
                                    meetingUpdateMessage)
                            ).toList()
                        ))
                        .flatMapMany(notificationRepository::saveAll)
                        .flatMap(notification -> Mono.zip(
                                Mono.just(notification),
                                memberInfoService.getZoneIdOfMember(notification.getReceiverId())
                        ))
                        .map(tuple2 -> {
                            var notification = tuple2.getT1();
                            var zoneId = tuple2.getT2();
                            return MeetingDeleteEvent.from(meetingUpdateMessage, notification, zoneId);
                        });

                Flux<NotificationEvent> updatedEvents = Mono.just(
                        maintainedAttendeeIds.stream().map(
                            attendeeId -> MeetingUpdateNotTimeNotification.from(attendeeId,
                                meetingUpdateMessage)
                        ).toList()
                    ).flatMapMany(notificationRepository::saveAll)
                    .flatMap(notification -> Mono.zip(
                        Mono.just(notification),
                        memberInfoService.getZoneIdOfMember(notification.getReceiverId())
                    ))
                    .map(tuple2 -> {
                        var notification = tuple2.getT1();
                        var zoneId = tuple2.getT2();
                        return MeetingUpdateFieldsEvent.from(meetingUpdateMessage,
                            notification, zoneId);
                    });

                if (meetingUpdateMessage.getUpdatedFields().contains(UpdatedField.TIME)) {
                    updatedEvents = Flux.mergeSequential(
                        updatedEvents,
                        Mono.just(
                                maintainedAttendeeIds.stream().map(
                                    attendeeId -> MeetingUpdateTimeNotification.from(attendeeId,
                                        meetingUpdateMessage)
                                ).toList()
                            ).flatMapMany(notificationRepository::saveAll)
                            .flatMap(notification -> Mono.zip(
                                Mono.just(notification),
                                memberInfoService.getZoneIdOfMember(notification.getReceiverId())
                            ))
                            .map(tuple2 -> {
                                var notification = tuple2.getT1();
                                var zoneId = tuple2.getT2();
                                return MeetingUpdateTimeEvent.from(meetingUpdateMessage,
                                    notification, zoneId);
                            })
                    );
                }

                return Flux.merge(addedEvents, removedEvents, updatedEvents);
            });
    }

    private Flux<NotificationEvent> saveAndGetMeetingDeleteEvent(MeetingDeleteMessage
        meetingDeleteMessage) {
        return Mono.just(meetingDeleteMessage)
            .flatMap(message -> notificationRepository.deleteByScheduleId(message.getScheduleId())
                .thenReturn(message))
            .flatMapMany(message -> {
                List<Integer> attendeeIds = message.getAttendeeIds();
                attendeeIds.remove(message.getOrganizerId());
                List<MeetingDeleteNotification> notifications = attendeeIds.stream()
                    .map(
                        attendeeId -> MeetingDeleteNotification.from(attendeeId,
                            meetingDeleteMessage)
                    ).toList();
                return notificationRepository.saveAll(notifications);
            }).flatMap(notification -> Mono.zip(
                Mono.just(notification),
                memberInfoService.getZoneIdOfMember(notification.getReceiverId())
            )).map(tuple2 -> {
                var notification = tuple2.getT1();
                var zoneId = tuple2.getT2();
                return MeetingDeleteEvent.from(meetingDeleteMessage, notification, zoneId);
            });
    }

    private Mono<NotificationEvent> saveAndGetAttendeeResponseEvent(AttendeeResponseMessage
        attendeeResponseMessage) {
        return Mono.just(attendeeResponseMessage)
            .flatMap(message -> {
                var notification = AttendeeResponseNotification.from(message);
                return notificationRepository.save(notification);
            }).zipWith(memberInfoService.getZoneIdOfMember(attendeeResponseMessage.getOrganizerId()))
            .map(tuple -> {
                var notification = tuple.getT1();
                var zoneId = tuple.getT2();
                return AttendeeResponseEvent.from(attendeeResponseMessage,
                    notification, zoneId);
            });
    }

    private Mono<NotificationEvent> saveAndGetAttendeeProposalEvent(AttendeeProposalMessage
        attendeeProposalMessage) {
        return Mono.just(attendeeProposalMessage)
            .flatMap(message -> {
                var notification = AttendeeProposalNotification.from(message);
                return notificationRepository.save(notification);
            }).zipWith(memberInfoService.getZoneIdOfMember(attendeeProposalMessage.getOrganizerId()))
            .map(tuple -> {
                var notification = tuple.getT1();
                var zoneId = tuple.getT2();
                return AttendeeProposalEvent.from(attendeeProposalMessage,
                    notification, zoneId);
            });
    }

    public Flux<NotificationEvent> getNotificationsByReceiverIdWithin2Weeks(Integer receiverId) {
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
                List<NotificationEvent> data = tuple.getT1();
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
