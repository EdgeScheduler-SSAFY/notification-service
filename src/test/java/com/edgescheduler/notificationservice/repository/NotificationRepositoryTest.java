package com.edgescheduler.notificationservice.repository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@DataMongoTest
@TestPropertySource(properties = {
    "de.flapdoodle.mongodb.embedded.version=6.0.8"
})
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void test() {

        AttendeeResponseNotification notification = AttendeeResponseNotification.builder()
            .userId(1)
            .createdAt(LocalDateTime.now())
            .scheduleId(1L)
            .attendeeId(1L)
            .response("response")
            .build();

        var saved = notificationRepository.save(notification);

        AttendeeResponseNotification block = saved.block();
        log.info("{}", block);
        assertInstanceOf(AttendeeResponseNotification.class, block);
    }
}