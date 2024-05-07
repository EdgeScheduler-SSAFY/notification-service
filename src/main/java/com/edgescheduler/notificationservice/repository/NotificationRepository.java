package com.edgescheduler.notificationservice.repository;

import com.edgescheduler.notificationservice.domain.Notification;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String>, CustomNotificationRepository {

    Flux<Notification> findByReceiverIdAndOccurredAtGreaterThanEqual(Integer receiverId, LocalDateTime occurredAt);
}
