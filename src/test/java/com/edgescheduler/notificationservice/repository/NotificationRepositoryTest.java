package com.edgescheduler.notificationservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.ScheduleCreateNotification;
import com.edgescheduler.notificationservice.domain.ScheduleUpdateTimeNotification;
import java.time.LocalDateTime;

import com.edgescheduler.notificationservice.domain.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

@Slf4j
@DataMongoTest
@TestPropertySource(properties = {
    "de.flapdoodle.mongodb.embedded.version=6.0.8"
})
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void entityInheritanceTest() {

        var not1 = ScheduleCreateNotification.builder()
            .receiverId(1)
            .notifiedAt(LocalDateTime.now())
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .notifiedAt(LocalDateTime.now())
            .scheduleId(2L)
            .attendeeId(2L)
            .response("ACCEPTED")
            .build();

        var prevStartTime = LocalDateTime.now();
        var updatedStartTime = LocalDateTime.now().plusHours(1);

        var not3 = ScheduleUpdateTimeNotification.builder()
            .receiverId(1)
            .notifiedAt(LocalDateTime.now())
            .scheduleId(3L)
            .previousStartTime(prevStartTime)
            .previousEndTime(prevStartTime.plusMinutes(30))
            .updatedStartTime(updatedStartTime)
            .updatedEndTime(updatedStartTime.plusMinutes(30))
            .build();

        var saved = notificationRepository.saveAll(Flux.just(not1, not2, not3));

        List<Notification> find = saved.flatMapSequential(notification -> notificationRepository.findById(notification.getId())
        ).collectList().block();

        assertNotNull(find);
        assertEquals(3, find.size());

        assertInstanceOf(ScheduleCreateNotification.class, find.get(0));
        assertInstanceOf(AttendeeResponseNotification.class, find.get(1));
        assertInstanceOf(ScheduleUpdateTimeNotification.class, find.get(2));

        assertEquals(not1.getScheduleId(), ((ScheduleCreateNotification) find.get(0)).getScheduleId());
        assertEquals(not2.getResponse(), ((AttendeeResponseNotification) find.get(1)).getResponse());
        assertAll(
            () -> assertEquals(not3.getPreviousStartTime().getHour(), ((ScheduleUpdateTimeNotification) find.get(2)).getPreviousStartTime().getHour()),
            () -> assertEquals(not3.getPreviousStartTime().getMinute(), ((ScheduleUpdateTimeNotification) find.get(2)).getPreviousStartTime().getMinute()),
            () -> assertEquals(not3.getUpdatedStartTime().getHour(), ((ScheduleUpdateTimeNotification) find.get(2)).getUpdatedStartTime().getHour()),
            () -> assertEquals(not3.getUpdatedStartTime().getMinute(), ((ScheduleUpdateTimeNotification) find.get(2)).getUpdatedStartTime().getMinute())
        );

    }
}