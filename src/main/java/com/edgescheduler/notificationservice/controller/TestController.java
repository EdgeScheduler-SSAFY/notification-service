package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaService kafkaService;

    @GetMapping("/test")
    public Mono<String> test(){
        return kafkaService.publish("notification", "test message");
//            .doOnNext(result -> log.info("Published message: {}", result))
//            .doOnError(error -> log.error("Error publishing message: {}", error.getMessage()));
    }
}
