package com.edgescheduler.notificationservice.repository;

import com.edgescheduler.notificationservice.domain.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationRepository extends
    ReactiveMongoRepository<Notification, String> {

}
