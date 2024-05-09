package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import java.util.List;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

    Publisher<NotificationSseEvent> saveNotificationFromEventMessage(KafkaEventMessage eventMessage);

    Flux<NotificationSseEvent> getNotificationsByReceiverIdWithin2Day(Integer receiverId);

    Mono<Void> markAsRead(Long notificationId);

    Mono<Void> markAllAsRead(List<Long> notificationIds);
}
