package com.edgescheduler.notificationservice.client;

import com.edgescheduler.notificationservice.event.AttendeeStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ScheduleServiceClient {

    private final WebClient webClient;

    @Value("${webclient.schedule-service.url}")
    private String scheduleServiceUrl;

    public ScheduleServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(scheduleServiceUrl).build();
    }

    public Mono<ScheduleInfo> getSchedule(Long scheduleId, Integer receiverId) {
        log.info("scheduleId: {}, receiverId: {}", scheduleId, receiverId);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/schedules/{scheduleId}/simple")
                .queryParam("receiverId", receiverId)
                .build(scheduleId))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> {
                    log.error("Failed to get schedule info");
                    return Mono.error(new RuntimeException("Failed to get schedule info"));
                })
            .bodyToMono(ScheduleInfo.class)
            .onErrorResume(e -> Mono.just(      // 에러 발생 시 기본값 반환
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
            ))
            .switchIfEmpty(Mono.just(      // for stubbing
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
            ))
            ;
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
