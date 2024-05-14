package com.edgescheduler.notificationservice.repository;

import com.edgescheduler.notificationservice.domain.Notification;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String>, CustomNotificationRepository {

    @Query(value = "{ 'receiverId' : ?0, 'occurredAt' : { $gte : ?1 } }", sort = "{ 'occurredAt' : -1 }")
    Flux<Notification> findNotificationsAfter(Integer receiverId, LocalDateTime occurredAt);

    @Query(value = "{ 'receiverId' : ?0, 'occurredAt' : { $gte : ?1 } }", sort = "{ 'occurredAt' : -1 }")
    Flux<Notification> findNotificationsAfterWithPaging(Integer receiverId, LocalDateTime occurredAt, Pageable pageable);

    Mono<Long> countByReceiverIdAndOccurredAtGreaterThanEqual(Integer receiverId, LocalDateTime occurredAt);
}
