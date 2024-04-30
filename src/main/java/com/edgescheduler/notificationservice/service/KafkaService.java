package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.edgescheduler.notificationservice.dto.NotificationMessage.NotificationType;
import com.edgescheduler.notificationservice.dto.ScheduleCreateMessage;
import com.edgescheduler.notificationservice.message.EventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService implements ApplicationRunner {

    private final EventSinkManager eventSinkManager;
    private final ReactiveKafkaProducerTemplate<String, EventMessage> producerTemplate;
    private final ReactiveKafkaConsumerTemplate<String, EventMessage> consumerTemplate;

    public Mono<String> publish(String topic, String message) {
        MeetingCreateMessage meetingCreateMessage = MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .meetingId(1L)
            .meetingName("Meeting")
            .organizerId(1)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .build();
        return producerTemplate.send("meeting-created", meetingCreateMessage)
            .map(result -> result.recordMetadata().toString());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.consumerTemplate
            .receiveAutoAck()
            .doOnNext(record -> {
                log.info("Consumed message: {} | From partition: {} | From offset: {}", record.value().getClass().getName(), record.partition(), record.offset());
                eventSinkManager.sendEvent(1, record.value().getClass().getName());
            })
            .doOnError(error -> log.error("Error consuming message: {}", error.getMessage()))
            .subscribe();
    }
}
