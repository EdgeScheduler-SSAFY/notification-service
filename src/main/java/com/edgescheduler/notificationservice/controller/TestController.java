package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.feign.client.UserServiceClient;
import com.edgescheduler.notificationservice.service.EmailService;
import com.edgescheduler.notificationservice.service.KafkaTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final KafkaTestService kafkaService;
    private final EmailService emailService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/meeting-created")
    public Mono<String> test(){
        return kafkaService.publishMeetingCreateEvent();
    }

    @PostMapping("/meeting-deleted")
    public Mono<String> test2(){
        return kafkaService.publicMeetingDeleteEvent();
    }

    @PostMapping("/attendee-response")
    public Mono<String> test3(){
        return kafkaService.publishAttendeeResponseEvent();
    }

    @PostMapping("/attendee-proposal")
    public Mono<String> test4(){
        return kafkaService.publishAttendeeProposalEvent();
    }

    @PostMapping("/send-email")
    public Mono<String> test5(){
        return emailService.sendEmail("oh052679@naver.com", "해치웠나?", "제발...!")
            .flatMap(mimeMessage -> Mono.just("Email sent"));
    }

    @GetMapping("/user-service/uncheck")
    public Mono<String> test6(){
        return Mono.fromCallable(userServiceClient::uncheck).subscribeOn(Schedulers.boundedElastic());
    }
}
