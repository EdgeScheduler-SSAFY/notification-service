package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.dto.NotificationMessage;
import reactor.core.publisher.Flux;

public interface NotificationService {

    void saveNotification(NotificationMessage notificationMessage);

    Flux<NotificationMessage> getNotificationsByReceiverId(Integer receiverId);

    void markAsRead(Integer notificationId);

    void markAllAsRead(Integer receiverId);
}
