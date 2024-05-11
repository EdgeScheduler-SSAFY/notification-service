package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.MemberInfo;
import com.edgescheduler.notificationservice.message.ChangeTimeZoneMessage;
import com.edgescheduler.notificationservice.message.MemberEmailMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
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
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.notificationQueue
            .receiveAutoAck()
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential(record -> {
                log.info("Notification message received");
                log.info("Consumed message: {} | From partition: {} | From offset: {}",
                    record.value().getClass().getName(), record.partition(), record.offset());
                return notificationService.saveNotificationFromEventMessage(record.value());
            })
            .flatMap(event -> {
                log.info("Sending event to receiver: {}", event.getReceiverId());
//                return Mono.zip(
//                    eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(), event).subscribeOn(Schedulers.boundedElastic()),
//                    emailService.sendEmail("oh052679@naver.com", event.getReceiverId() + "번 유저에게.", event.getType().toString()).subscribeOn(Schedulers.boundedElastic())
//                );
                return eventSinkManager.sendEvent(event.getReceiverId(), event.getType().toString(),
                    event).subscribeOn(Schedulers.boundedElastic());
            })
            .doOnError(
                error -> log.error("Error consuming notification message: {}", error.getMessage()))
            .subscribe();

        this.timeZoneQueue
            .receiveAutoAck()
            .flatMap(record -> {
                log.info("Timezone message received");
                ChangeTimeZoneMessage message = record.value();
                log.info("memberId: {} | zoneId: {}", message.getMemberId(), message.getZoneId());
                return memberInfoRepository.findById(message.getMemberId())
                    .flatMap(memberInfo -> {
                        memberInfo.changeZoneId(message.getZoneId());
                        return memberInfoRepository.save(memberInfo);
                    }).switchIfEmpty(Mono.defer(() -> memberInfoRepository.save(
                        MemberInfo.builder()
                            .memberId(message.getMemberId())
                            .zoneId(message.getZoneId()).build())
                    ));
            })
            .doOnError(
                error -> log.error("Error consuming timezone message: {}", error.getMessage()))
            .subscribe();

        this.emailQueue
            .receiveAutoAck()
            .flatMap(record -> {
                log.info("Email message received");
                MemberEmailMessage message = record.value();
                log.info("id: {} | email: {}", message.getId(), message.getEmail());
                return memberInfoRepository.findById(message.getId())
                    .flatMap(memberInfo -> {
                        memberInfo.changeEmail(message.getEmail());
                        return memberInfoRepository.save(memberInfo);
                    }).switchIfEmpty(Mono.defer(() -> memberInfoRepository.save(
                        MemberInfo.builder()
                            .memberId(message.getId())
                            .email(message.getEmail()).build())
                    ));
            })
            .doOnError(
                error -> log.error("Error consuming email message: {}", error.getMessage()))
            .subscribe();
    }
}
