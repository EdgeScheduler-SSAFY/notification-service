package com.edgescheduler.notificationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service").build();
    }

    public Mono<String> getUncheck() {
        return webClient.get()
            .uri("/uncheck")
            .retrieve().bodyToMono(String.class);
    }
}
