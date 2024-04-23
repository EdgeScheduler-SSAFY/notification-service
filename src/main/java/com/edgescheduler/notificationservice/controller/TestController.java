package com.edgescheduler.notificationservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    public Mono<String> test(){
        return Mono.just("test")
                .doOnNext(log::info);
    }
}
