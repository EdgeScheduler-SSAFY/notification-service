package com.edgescheduler.notificationservice.client;

import com.edgescheduler.notificationservice.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final WebClient webClient;

    @Value("${webclient.user-service.url}")
    private String userServiceUrl;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    public Mono<UserInfo> getUserInfo(Integer id) {
        log.info("userServiceUrl: {}", userServiceUrl);
        return webClient.get()
            .uri("/members/{id}", id)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> {
                    log.error("Failed to get user info: {}", response.statusCode());
                    return Mono.error(ErrorCode.MEMBER_NOT_FOUND.exception());
                })
            .bodyToMono(UserInfo.class)
            .onErrorResume(e -> Mono.just(  // 에러 발생 시 기본값 반환
                UserInfo.builder()
                    .id(id)
                    .name("Unknown")
                    .build())
            )
            .switchIfEmpty(Mono.just(
                UserInfo.builder()
                    .id(id)
                    .name("Unknown")
                    .build())
            )
            ;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Integer id;
        private String name;
    }
}
