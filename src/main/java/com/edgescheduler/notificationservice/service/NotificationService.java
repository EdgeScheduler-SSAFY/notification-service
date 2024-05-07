package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

    Publisher<NotificationSseEvent> saveNotificationFromEventMessage(KafkaEventMessage eventMessage);

    Flux<NotificationSseEvent> getNotificationsByReceiverIdWithin2Day(Integer receiverId);

    Mono<Void> markAsRead(Integer notificationId);

    Mono<Void> markAllAsRead(Integer receiverId);
}
