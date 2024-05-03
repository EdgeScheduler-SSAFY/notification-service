package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.service.KafkaTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final KafkaTestService kafkaService;

    @PostMapping("/meeting-create")
    public Mono<String> test(){
        return kafkaService.publishMeetingCreateEvent();
    }

    @PostMapping("/meeting-delete")
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
}
