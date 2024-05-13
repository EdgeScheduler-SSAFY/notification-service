package com.edgescheduler.notificationservice.client;

import com.edgescheduler.notificationservice.event.AttendeeStatus;
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

    public Mono<ScheduleInfo> getSchedule(Long scheduleId, Integer receiverId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/schedules/{scheduleId}/simple")
                .queryParam("receiverId", receiverId)
                .build(scheduleId))
            .retrieve().bodyToMono(ScheduleInfo.class)
            .onErrorResume(e -> Mono.just(      // for stubbing
                ScheduleInfo.builder()
                    .scheduleId(scheduleId)
                    .name("Unknown")
                    .organizerId(0)
                    .organizerName("Unknown")
                    .startDatetime(LocalDateTime.now())
                    .endDatetime(LocalDateTime.now().plusHours(2))
                    .runningTime(120)
                    .receiverStatus(AttendeeStatus.PENDING)
                    .build()
            ));
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
        private Integer runningTime;
        private AttendeeStatus receiverStatus;
    }
}
