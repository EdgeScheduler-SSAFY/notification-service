package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public interface NotificationService {

    Publisher<NotificationSseEvent> saveNotificationFromEventMessage(KafkaEventMessage eventMessage);

    Flux<NotificationSseEvent> getNotificationsByReceiverId(Integer receiverId);

    void markAsRead(Integer notificationId);

    void markAllAsRead(Integer receiverId);
}
