package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.event.AttendeeProposalSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteSseEvent;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.edgescheduler.notificationservice.repository.MemberTimezoneRepository;
import com.edgescheduler.notificationservice.repository.NotificationRepository;
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
    private final MemberTimezoneRepository memberTimezoneRepository;

    @Override
    public Publisher<NotificationSseEvent> saveNotificationFromEventMessage(KafkaEventMessage eventMessage) {

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

    private Flux<NotificationSseEvent> saveAndGetMeetingCreateEvent(MeetingCreateMessage meetingCreateMessage) {
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
                log.info("almost saveAll");
                return notificationRepository.saveAll(notifications);
            }).map(notification -> MeetingCreateSseEvent.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .type(NotificationType.SCHEDULE_CREATED)
                .occurredAt(notification.getOccurredAt())
                .scheduleId(notification.getScheduleId())
                .scheduleName(meetingCreateMessage.getScheduleName())
                .organizerId(meetingCreateMessage.getOrganizerId())
                .organizerName(meetingCreateMessage.getOrganizerName())
                .startTime(meetingCreateMessage.getStartTime())
                .endTime(meetingCreateMessage.getEndTime())
                .isRead(notification.getIsRead())
                .build());
    }

    private Flux<NotificationSseEvent> saveAndGetMeetingUpdateEvent(MeetingUpdateMessage meetingUpdateMessage) {
        return Flux.empty();
    }

    private Flux<NotificationSseEvent> saveAndGetMeetingDeleteEvent(MeetingDeleteMessage meetingDeleteMessage) {
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
                .type(NotificationType.SCHEDULE_DELETED)
                .occurredAt(notification.getOccurredAt())
                .scheduleName(notification.getScheduleName())
                .organizerId(notification.getOrganizerId())
                .organizerName(meetingDeleteMessage.getOrganizerName())
                .startTime(notification.getStartTime())
                .endTime(notification.getEndTime())
                .isRead(notification.getIsRead())
                .build());
    }

    private Mono<NotificationSseEvent> saveAndGetAttendeeResponseEvent(AttendeeResponseMessage attendeeResponseMessage) {
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

    private Mono<NotificationSseEvent> saveAndGetAttendeeProposalEvent(AttendeeProposalMessage attendeeProposalMessage) {
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
                .type(NotificationType.ATTENDEE_SCHEDULE_PROPOSAL)
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
    public Flux<NotificationSseEvent> getNotificationsByReceiverId(Integer receiverId) {
        Flux<Notification> notifications = notificationRepository.findByReceiverId(receiverId);
        return null;
    }

    @Override
    public void markAsRead(Integer notificationId) {

    }

    @Override
    public void markAllAsRead(Integer receiverId) {

    }




}
