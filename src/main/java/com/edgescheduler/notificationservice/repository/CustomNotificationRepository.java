package com.edgescheduler.notificationservice.repository;

import java.util.List;
import reactor.core.publisher.Mono;

public interface CustomNotificationRepository {

    Mono<Void> markAsRead(String notificationId);

    Mono<Void> markAllAsRead(List<String> notificationIds);
}
