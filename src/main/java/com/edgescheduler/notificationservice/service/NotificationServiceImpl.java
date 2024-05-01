package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.event.ScheduleCreateEvent;
import com.edgescheduler.notificationservice.message.EventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.repository.MemberTimezoneRepository;
import com.edgescheduler.notificationservice.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Flux<NotificationEvent> saveNotificationFromEventMessage(EventMessage eventMessage) {

        if (eventMessage instanceof MeetingCreateMessage meetingCreateMessage) {
//            Flux.fromIterable(meetingCreateMessage.getAttendeeIds())
//                .filter(attendeeId -> !attendeeId.equals(meetingCreateMessage.getOrganizerId()))
//                .flatMapSequential(attendeeId -> {
//                    MeetingCreateNotification notification = MeetingCreateNotification.builder()
//                        .receiverId(attendeeId)
//                        .occurredAt(meetingCreateMessage.getOccurredAt())
//                        .isRead(false)
//                        .scheduleId(meetingCreateMessage.getScheduleId())
//                        .build();
//                    return notificationRepository.save(notification)
//                        .map();
//                });
            log.info("before saveAll");
            return Mono.just(meetingCreateMessage)
                .flatMapMany(message -> {
                    List<Integer> attendeeIds = message.getAttendeeIds();
                    log.info("attendeeIds: {}", attendeeIds);
                    attendeeIds.remove(message.getOrganizerId());
                    log.info("attendeeIds after remove: {}", attendeeIds);
                    List<MeetingCreateNotification> notifications = (List<MeetingCreateNotification>) attendeeIds.stream()
                        .map(attendeeId -> MeetingCreateNotification.builder()
                            .receiverId(attendeeId)
                            .occurredAt(message.getOccurredAt())
                            .isRead(false)
                            .scheduleId(message.getScheduleId())
                            .build())
                        .toList();
                    log.info("almost saveAll");
                    return notificationRepository.saveAll(notifications);
                }).map(notification -> ScheduleCreateEvent.builder()
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

        return Flux.empty();
    }

    @Override
    public Flux<NotificationEvent> getNotificationsByReceiverId(Integer receiverId) {
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
