package com.edgescheduler.notificationservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.edgescheduler.notificationservice.domain.AttendeeProposalNotification;
import com.edgescheduler.notificationservice.domain.AttendeeResponseNotification;
import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.domain.MeetingDeleteNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateNotTimeNotification;
import com.edgescheduler.notificationservice.domain.MeetingUpdateTimeNotification;
import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.event.UpdatedField;
import java.time.LocalDateTime;

import com.edgescheduler.notificationservice.domain.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
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

        var not1 = MeetingCreateNotification.builder()
            .receiverId(1)
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .occurredAt(LocalDateTime.now())
            .scheduleId(2L)
            .attendeeId(2)
            .response(Response.ACCEPTED)
            .build();

        var prevStartTime = LocalDateTime.now();
        var updatedStartTime = LocalDateTime.now().plusHours(1);

        var not3 = MeetingUpdateTimeNotification.builder()
            .receiverId(1)
            .occurredAt(LocalDateTime.now())
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

        assertInstanceOf(MeetingCreateNotification.class, find.get(0));
        assertInstanceOf(AttendeeResponseNotification.class, find.get(1));
        assertInstanceOf(MeetingUpdateTimeNotification.class, find.get(2));

        assertEquals(not1.getScheduleId(), ((MeetingCreateNotification) find.get(0)).getScheduleId());
        assertEquals(not2.getResponse(), ((AttendeeResponseNotification) find.get(1)).getResponse());
        assertAll(
            () -> assertEquals(not3.getPreviousStartTime().getHour(), ((MeetingUpdateTimeNotification) find.get(2)).getPreviousStartTime().getHour()),
            () -> assertEquals(not3.getPreviousStartTime().getMinute(), ((MeetingUpdateTimeNotification) find.get(2)).getPreviousStartTime().getMinute()),
            () -> assertEquals(not3.getUpdatedStartTime().getHour(), ((MeetingUpdateTimeNotification) find.get(2)).getUpdatedStartTime().getHour()),
            () -> assertEquals(not3.getUpdatedStartTime().getMinute(), ((MeetingUpdateTimeNotification) find.get(2)).getUpdatedStartTime().getMinute())
        );

    }

    @Test
    void findNotificationsAfterTest() {

        LocalDateTime now = LocalDateTime.now();
        var not1 = MeetingCreateNotification.builder()
            .receiverId(1)
            .occurredAt(now)
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(1))
            .scheduleId(2L)
            .attendeeId(2)
            .response(Response.ACCEPTED)
            .build();

        var not3 = MeetingUpdateTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(2))
            .scheduleId(3L)
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusMinutes(30))
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
            .build();

        Flux<Notification> notificationFlux = notificationRepository.saveAll(
            Flux.just(not1, not2, not3));
        List<Notification> block = notificationFlux.collectList().block();

        var find = notificationRepository.findNotificationsAfter(1, now.minusMinutes(100)).collectList().block();

        assertNotNull(find);
        assertEquals(3, find.size());

        assertAll(
            () -> assertInstanceOf(MeetingCreateNotification.class, find.get(0)),
            () -> assertInstanceOf(AttendeeResponseNotification.class, find.get(1)),
            () -> assertInstanceOf(MeetingUpdateTimeNotification.class, find.get(2))
        );
    }

    @Test
    void findNotificationsAfterWithPagingTest() {

        LocalDateTime now = LocalDateTime.now();
        var not1 = MeetingCreateNotification.builder()
            .receiverId(1)
            .occurredAt(now)
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(1))
            .scheduleId(2L)
            .attendeeId(2)
            .response(Response.ACCEPTED)
            .build();

        var not3 = MeetingUpdateTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(2))
            .scheduleId(3L)
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusMinutes(30))
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
            .build();

        var not4 = MeetingUpdateNotTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(3))
            .scheduleId(4L)
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE))
            .build();

        var not5 = MeetingDeleteNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(4))
            .scheduleName("Meeting 5")
            .build();

        var not6 = AttendeeProposalNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(5))
            .scheduleId(6L)
            .attendeeId(2)
            .build();


        Flux<Notification> notificationFlux = notificationRepository.saveAll(
            Flux.just(not1, not2, not3, not4, not5, not6));
        List<Notification> block = notificationFlux.collectList().block();

        var page1 = notificationRepository.findNotificationsAfterWithPaging(1, now.minusMinutes(6),
            PageRequest.of(0, 4)).collectList().block();
        var page2 = notificationRepository.findNotificationsAfterWithPaging(1, now.minusMinutes(6),
            PageRequest.of(1, 4)).collectList().block();

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(4, page1.size());
        assertEquals(2, page2.size());

        assertAll(
            () -> assertInstanceOf(MeetingCreateNotification.class, page1.get(0)),
            () -> assertInstanceOf(AttendeeResponseNotification.class, page1.get(1)),
            () -> assertInstanceOf(MeetingUpdateTimeNotification.class, page1.get(2)),
            () -> assertInstanceOf(MeetingUpdateNotTimeNotification.class, page1.get(3)),
            () -> assertInstanceOf(MeetingDeleteNotification.class, page2.get(0)),
            () -> assertInstanceOf(AttendeeProposalNotification.class, page2.get(1))
        );
    }

    @Test
    void countByReceiverIdAndOccurredAtGreaterThanEqualTest() {

        LocalDateTime now = LocalDateTime.now();
        var not1 = MeetingCreateNotification.builder()
            .receiverId(1)
            .occurredAt(now)
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(1))
            .scheduleId(1L)
            .attendeeId(2)
            .response(Response.ACCEPTED)
            .build();

        var not3 = MeetingUpdateTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(2))
            .scheduleId(1L)
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusMinutes(30))
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
            .build();

        var not4 = MeetingUpdateNotTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(3))
            .scheduleId(1L)
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE))
            .build();

        var not5 = MeetingDeleteNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(4))
            .scheduleId(1L)
            .scheduleName("Meeting 5")
            .build();

        var not6 = AttendeeProposalNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(5))
            .scheduleId(6L)
            .attendeeId(2)
            .build();


        Flux<Notification> notificationFlux = notificationRepository.saveAll(
            Flux.just(not1, not2, not3, not4, not5, not6));
        List<Notification> block = notificationFlux.collectList().block();

        var count = notificationRepository.countByReceiverIdAndOccurredAtGreaterThanEqual(1, now.minusMinutes(6)).block();

        assertNotNull(count);
        assertEquals(6, count);
    }

    @Test
    void deleteByScheduleIdTest() {
        LocalDateTime now = LocalDateTime.now();
        var not1 = MeetingCreateNotification.builder()
            .receiverId(1)
            .occurredAt(now)
            .scheduleId(1L)
            .build();

        var not2 = AttendeeResponseNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(1))
            .scheduleId(1L)
            .attendeeId(2)
            .response(Response.ACCEPTED)
            .build();

        var not3 = MeetingUpdateTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(2))
            .scheduleId(1L)
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusMinutes(30))
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
            .build();

        var not4 = MeetingUpdateNotTimeNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(3))
            .scheduleId(1L)
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE))
            .build();

        var not5 = MeetingDeleteNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(4))
            .scheduleId(1L)
            .scheduleName("Meeting 5")
            .build();

        var not6 = AttendeeProposalNotification.builder()
            .receiverId(1)
            .occurredAt(now.minusMinutes(5))
            .scheduleId(6L)
            .attendeeId(2)
            .build();


        Flux<Notification> notificationFlux = notificationRepository.saveAll(
            Flux.just(not1, not2, not3, not4, not5, not6));
        List<Notification> block = notificationFlux.collectList().block();

        notificationRepository.deleteByScheduleId(1L).block();

        var find = notificationRepository.findNotificationsAfter(1, now.minusMinutes(6)).collectList().block();

        assertNotNull(find);
        assertEquals(1, find.size());
        assertInstanceOf(AttendeeProposalNotification.class, find.get(0));
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll().block();
    }
}