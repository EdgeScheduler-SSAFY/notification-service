package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.message.ChangeTimeZoneMessage;
import com.edgescheduler.notificationservice.message.MemberCreateMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService implements ApplicationRunner {

    private final EventSinkManager eventSinkManager;
    private final ReactiveKafkaConsumerTemplate<String, NotificationMessage> notificationQueue;
    private final ReactiveKafkaConsumerTemplate<String, ChangeTimeZoneMessage> timeZoneQueue;
    private final ReactiveKafkaConsumerTemplate<String, MemberCreateMessage> signupQueue;
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
                record -> Flux.from(
                        notificationService.saveNotificationFromEventMessage(record.value()))
                    .onErrorResume(error -> {
                        log.error("Error saving notification: {}", error.getMessage());
                        return Mono.empty();
                    })
            )
            .flatMap(event ->
                Mono.when(
                    eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(),
                        event).subscribeOn(Schedulers.boundedElastic()),
                    emailService.sendEmail(event).subscribeOn(Schedulers.boundedElastic()))
//                    eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(),
//                            event).subscribeOn(Schedulers.boundedElastic())
                        .onErrorResume(error -> {
                            log.error("Error sending event: {}", error.getMessage());
                            return Mono.empty();
                        })
            )
            .subscribe();

        this.timeZoneQueue
            .receiveAutoAck()
            .flatMap(record -> {
                ChangeTimeZoneMessage message = record.value();
                log.info("Consumed timezone message: {}", message.toString());
                return memberInfoService.upsertZoneIdOfMember(
                    message.getMemberId(), ZoneId.of(message.getZoneId())
                );
            })
            .doOnError(
                error -> log.error("Error consuming timezone message: {}", error.getMessage()))
            .subscribe();

        this.signupQueue
            .receiveAutoAck()
            .flatMap(record -> {
                MemberCreateMessage message = record.value();
                return memberInfoService.upsertMemberInfo(
                    message.getMemberId(), message.getEmail(), message.getZoneId()
                );
            })
            .doOnError(
                error -> log.error("Error consuming email message: {}", error.getMessage()))
            .subscribe();
    }
}
