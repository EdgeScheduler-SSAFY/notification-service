package com.edgescheduler.notificationservice.client;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScheduleServiceClient {

    private final WebClient webClient;

    public ScheduleServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://schedule-service").build();
    }

    public Mono<ScheduleInfo> getSchedule(Long id) {
        return webClient.get()
            .uri("/schedules/{id}/simple", id)
            .retrieve().bodyToMono(ScheduleInfo.class);
    }

    @Getter
    @Builder
    public static class ScheduleInfo {
        private Long scheduleId;
        private String name;
        private Integer organizerId;
        private String organizerName;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
    }
}
