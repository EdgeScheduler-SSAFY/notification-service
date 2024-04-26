package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaService kafkaService;

    @GetMapping("/test")
    public Mono<String> test(){
        return Mono.just("test Kafka messaging")
                .doOnNext(message -> kafkaService.publish("test", message));
    }
}
