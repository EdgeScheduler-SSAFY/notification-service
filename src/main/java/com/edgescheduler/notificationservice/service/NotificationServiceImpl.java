package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.Notification;
import com.edgescheduler.notificationservice.domain.ScheduleCreateNotification;
import com.edgescheduler.notificationservice.domain.ScheduleDeleteNotification;
import com.edgescheduler.notificationservice.domain.ScheduleUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.ScheduleUpdateTimeNotification;
import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.edgescheduler.notificationservice.dto.ScheduleCreateMessage;
import com.edgescheduler.notificationservice.dto.ScheduleUpdateNotTimeMessage;
import com.edgescheduler.notificationservice.repository.NotificationRepository;
import com.edgescheduler.notificationservice.util.NotificationMessageConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void saveNotification(NotificationMessage notificationMessage) {
        Notification notification = notificationMessage.toEntity();
        notificationRepository.save(notification)
            .doOnError(e -> {
                throw new RuntimeException("Failed to save notification");
            });
    }

    @Override
    public Flux<NotificationMessage> getNotificationsByReceiverId(Integer receiverId) {
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
