package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.edgescheduler.notificationservice.dto.NotificationMessage.NotificationType;
import com.edgescheduler.notificationservice.dto.ScheduleCreateMessage;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService implements ApplicationRunner {

    private final EventSinkManager eventSinkManager;
    private final ReactiveKafkaProducerTemplate<String, NotificationMessage> producerTemplate;
    private final ReactiveKafkaConsumerTemplate<String, NotificationMessage> consumerTemplate;

    public Mono<String> publish(String topic, String message) {
        ScheduleCreateMessage notificationMessage = ScheduleCreateMessage.builder()
            .type(NotificationType.SCHEDULE_CREATED)
            .receiverId(1)
            .notifiedTime(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Test schedule")
            .isRead(false)
            .organizerId(2)
            .organizerName("Test organizer")
            .startTime(LocalDateTime.now().plusHours(1))
            .endTime(LocalDateTime.now().plusHours(2))
            .build();
        return producerTemplate.send(topic, notificationMessage)
            .map(result -> result.recordMetadata().toString());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.consumerTemplate
            .receiveAutoAck()
            .doOnNext(record -> {
                log.info("Consumed message: {} | From partition: {} | From offset: {}", record.value().getScheduleName(), record.partition(), record.offset());
                eventSinkManager.sendEvent(1, record.value().getScheduleName());
            })
            .doOnError(error -> log.error("Error consuming message: {}", error.getMessage()))
            .subscribe();
    }
}
