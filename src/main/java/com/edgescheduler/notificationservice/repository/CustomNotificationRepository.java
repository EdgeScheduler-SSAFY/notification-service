package com.edgescheduler.notificationservice.repository;

import java.util.List;
import reactor.core.publisher.Mono;

public interface CustomNotificationRepository {

    Mono<Void> markAsRead(Long notificationId);

    Mono<Void> markAllAsRead(List<Long> notificationIds);
}
