package com.edgescheduler.notificationservice.client;

import lombok.Builder;
import lombok.Getter;
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
            .uri("/check")
            .retrieve().bodyToMono(String.class);
    }

    public Mono<UserInfo> getUserInfo(Integer id) {
        return webClient.get()
            .uri("/members/{id}", id)
            .retrieve().bodyToMono(UserInfo.class)
            .onErrorResume(e -> Mono.just(
                UserInfo.builder()
                    .id(id)
                    .name("Unknown")
                    .build())
            );
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Integer id;
        private String name;
    }
}
