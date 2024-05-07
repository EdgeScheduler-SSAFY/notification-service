package com.edgescheduler.notificationservice.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.edgescheduler.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

    ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> markAsRead(Integer notificationId) {
        return mongoTemplate.updateFirst(query(where("id").is(notificationId)),
            new Update().set("isRead", true), Notification.class).then();
    }

    @Override
    public Mono<Void> markAllAsRead(Integer receiverId) {
        return mongoTemplate.updateMulti(query(where("receiverId").is(receiverId)),
            new Update().set("isRead", true), Notification.class).then();
    }
}
