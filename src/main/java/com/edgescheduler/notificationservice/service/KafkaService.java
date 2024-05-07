package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService implements ApplicationRunner {

    private final EventSinkManager eventSinkManager;
    private final ReactiveKafkaConsumerTemplate<String, KafkaEventMessage> consumerTemplate;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.consumerTemplate
            .receiveAutoAck()
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential(record -> {
                log.info("Consumed message: {} | From partition: {} | From offset: {}",
                    record.value().getClass().getName(), record.partition(), record.offset());
                return notificationService.saveNotificationFromEventMessage(record.value());
            })
            .flatMap(event -> {
                log.info("Sending event to receiver: {}", event.getReceiverId());
                return Mono.zip(
                    eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(), event).subscribeOn(Schedulers.boundedElastic()),
                    emailService.sendEmail("oh052679@naver.com", event.getReceiverId() + "번 유저에게.", event.getType().toString()).subscribeOn(Schedulers.boundedElastic())
                );
            })
            .doOnError(error -> log.error("Error consuming message: {}", error.getMessage()))
            .subscribe();
    }
}
