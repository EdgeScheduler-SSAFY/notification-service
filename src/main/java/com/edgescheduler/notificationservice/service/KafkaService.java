package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.message.EventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService implements ApplicationRunner {

    private final EventSinkManager eventSinkManager;
    private final ReactiveKafkaProducerTemplate<String, EventMessage> producerTemplate;
    private final ReactiveKafkaConsumerTemplate<String, EventMessage> consumerTemplate;
    private final NotificationService notificationService;

    public Mono<String> publish(String topic, String message) {
        MeetingCreateMessage meetingCreateMessage = MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(1)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .attendeeIds(IntStream.range(1, 100).boxed().toList())
            .build();
        return producerTemplate.send("meeting-created", meetingCreateMessage)
            .map(result -> result.recordMetadata().toString());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.consumerTemplate
            .receiveAutoAck()
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential(record -> {
                log.info("Consumed message: {} | From partition: {} | From offset: {}", record.value().getClass().getName(), record.partition(), record.offset());
                return notificationService.saveNotificationFromEventMessage(record.value());
            })
            .doOnNext(event -> {
                eventSinkManager.sendEvent(event.getReceiverId(), event);
            })
            .doOnError(error -> log.error("Error consuming message: {}", error.getMessage()))
            .subscribe();
    }
}
