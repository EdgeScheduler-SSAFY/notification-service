package com.edgescheduler.notificationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.when;

import com.edgescheduler.notificationservice.domain.MeetingCreateNotification;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import com.edgescheduler.notificationservice.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.FirstStep;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberInfoRepository memberInfoRepository;

    @InjectMocks
    private NotificationService notificationService;

    @DisplayName("미팅 생성 메시지로 SSE 이벤트 생성")
    @Test
    void saveNotificationFromMeetingCreateMessageTest() {
        var eventMessage = MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(1)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .attendeeIds(IntStream.range(1, 100).boxed().collect(Collectors.toList()))
            .build();

        when(notificationRepository.saveAll(anyIterable())).thenReturn(
            Flux.create(sink -> {
                for (Integer attendeeId : eventMessage.getAttendeeIds()) {
                    sink.next(MeetingCreateNotification.builder()
                        .receiverId(attendeeId)
                        .scheduleId(eventMessage.getScheduleId())
                        .build());
                }
                sink.complete();
            }));

        // when
        var result = notificationService.saveNotificationFromEventMessage(eventMessage);

        // then
        FirstStep<NotificationEvent> step = StepVerifier.create(result);
        for (int i = 2; i < 100; i++) {
            int finalI = i;
            step.assertNext(event -> {
                assertEquals(1, event.getScheduleId());
                assertEquals("Meeting", event.getScheduleName());
                assertEquals(finalI, event.getReceiverId());
            });
        }
        step.expectComplete()
            .verify(StepVerifier.DEFAULT_VERIFY_TIMEOUT);
    }
}