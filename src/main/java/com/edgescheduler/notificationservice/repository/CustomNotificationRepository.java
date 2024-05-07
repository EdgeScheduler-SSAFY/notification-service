package com.edgescheduler.notificationservice.repository;

import reactor.core.publisher.Mono;

public interface CustomNotificationRepository {

    Mono<Void> markAsRead(Integer notificationId);

    Mono<Void> markAllAsRead(Integer receiverId);
}
