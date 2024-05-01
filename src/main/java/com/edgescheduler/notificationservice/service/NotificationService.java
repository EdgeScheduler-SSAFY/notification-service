package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.message.EventMessage;
import reactor.core.publisher.Flux;

public interface NotificationService {

    Flux<NotificationEvent> saveNotificationFromEventMessage(EventMessage eventMessage);

    Flux<NotificationEvent> getNotificationsByReceiverId(Integer receiverId);

    void markAsRead(Integer notificationId);

    void markAllAsRead(Integer receiverId);
}
