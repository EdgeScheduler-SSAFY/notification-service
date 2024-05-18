package com.edgescheduler.notificationservice.client;

import com.edgescheduler.notificationservice.event.AttendeeStatus;
import com.edgescheduler.notificationservice.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ScheduleServiceClient {

    private final WebClient webClient;

    public ScheduleServiceClient(@Qualifier("scheduleServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
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
                    log.error("Failed to get schedule info: {}", response.statusCode());
                    log.error("failed response body: {}", response.bodyToMono(String.class).block());
                    return Mono.error(ErrorCode.SCHEDULE_NOT_FOUND.exception(scheduleId));
                })
            .bodyToMono(ScheduleInfo.class)
            .filter(scheduleInfo -> {
                log.info("scheduleInfo: {}", scheduleInfo);
                return !scheduleInfo.getIsDeleted();
            })
//            .onErrorResume(e -> Mono.just(      // 에러 발생 시 기본값 반환
//                ScheduleInfo.builder()
//                    .scheduleId(scheduleId)
//                    .name("Unknown")
//                    .organizerId(0)
//                    .organizerName("Unknown")
//                    .startDatetime(LocalDateTime.now())
//                    .endDatetime(LocalDateTime.now().plusHours(2))
//                    .runningTime(120)
//                    .receiverStatus(AttendeeStatus.PENDING)
//                    .build()
//            ))
//            .switchIfEmpty(Mono.just(      // for stubbing
//                ScheduleInfo.builder()
//                    .scheduleId(scheduleId)
//                    .name("Unknown")
//                    .organizerId(0)
//                    .organizerName("Unknown")
//                    .startDatetime(LocalDateTime.now())
//                    .endDatetime(LocalDateTime.now().plusHours(2))
//                    .runningTime(120)
//                    .receiverStatus(AttendeeStatus.PENDING)
//                    .build()
//            ))
            ;
    }

    @Getter
    @Builder
    @ToString
    public static class ScheduleInfo {
        private Long scheduleId;
        private String name;
        private Integer organizerId;
        private String organizerName;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private Integer runningTime;
        private AttendeeStatus receiverStatus;
        private Boolean isDeleted;
    }
}
