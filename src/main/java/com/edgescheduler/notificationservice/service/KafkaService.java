package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.MemberInfo;
import com.edgescheduler.notificationservice.message.ChangeTimeZoneMessage;
import com.edgescheduler.notificationservice.message.MemberEmailMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import java.time.ZoneId;
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
    private final ReactiveKafkaConsumerTemplate<String, NotificationMessage> notificationQueue;
    private final ReactiveKafkaConsumerTemplate<String, ChangeTimeZoneMessage> timeZoneQueue;
    private final ReactiveKafkaConsumerTemplate<String, MemberEmailMessage> emailQueue;
    private final MemberInfoRepository memberInfoRepository;
    private final MemberInfoService memberInfoService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.notificationQueue
            .receiveAutoAck()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(
                record -> log.info("Consumed message: {} | From partition: {} | From offset: {}",
                    record.value().getClass().getName(), record.partition(), record.offset()))
            .flatMapSequential(
                record -> notificationService.saveNotificationFromEventMessage(record.value()))
            .flatMap(event -> Mono.zip(
                    eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(),
                        event).subscribeOn(Schedulers.boundedElastic()),
                    emailService.sendEmail(event))
            )
            .doOnError(
                error -> log.error("Error consuming notification message: {}", error.getMessage()))
            .subscribe();

        this.timeZoneQueue
            .receiveAutoAck()
            .flatMap(record -> {
                ChangeTimeZoneMessage message = record.value();
                return memberInfoService.upsertZoneIdOfMember(
                    message.getMemberId(), ZoneId.of(message.getZoneId())
                );
            })
            .doOnError(
                error -> log.error("Error consuming timezone message: {}", error.getMessage()))
            .subscribe();

        this.emailQueue
            .receiveAutoAck()
            .flatMap(record -> {
                MemberEmailMessage message = record.value();
                return memberInfoService.upsertEmailOfMember(
                    message.getId(), message.getEmail()
                );
            })
            .doOnError(
                error -> log.error("Error consuming email message: {}", error.getMessage()))
            .subscribe();
    }
}
