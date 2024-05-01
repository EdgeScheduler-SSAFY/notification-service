package com.edgescheduler.notificationservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

    ReactiveMongoTemplate mongoTemplate;

    @Override
    public void markAsRead(Integer notificationId) {

    }

    @Override
    public void markAllAsRead(Integer receiverId) {

    }
}
