package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.dto.NotificationHistory;
import com.edgescheduler.notificationservice.dto.NotificationPage;
import com.edgescheduler.notificationservice.dto.NotificationReadAllRequest;
import com.edgescheduler.notificationservice.dto.NotificationReadAllResponse;
import com.edgescheduler.notificationservice.dto.NotificationReadResponse;
import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.service.EventSinkManager;
import com.edgescheduler.notificationservice.service.NotificationService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class NotifyController {

    private static final Logger log = LoggerFactory.getLogger(NotifyController.class);
    private final EventSinkManager eventSinkManager;
    private final NotificationService notificationService;

    @GetMapping(path = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> sse(
        @RequestHeader(name = "Authorization", required = false) Integer userId,
        @RequestHeader(name = "Last-Event-ID", required = false, defaultValue = "0") Long lastEventId,
        @PathVariable Integer memberId) {
        return Flux.<ServerSentEvent<Object>>create(sink -> {
                if (eventSinkManager.addEventSink(memberId, sink, lastEventId)) {
                    sink.onDispose(() -> eventSinkManager.removeEventSink(memberId));
                } else {
                    sink.error(ErrorCode.DUPLICATE_CONNECTION.exception());
                }
            }
        ).onBackpressureBuffer();
    }

    @GetMapping("/notifications/history")
    public Mono<NotificationHistory> getNotifications(
        @RequestHeader(name = "Authorization") Integer userId) {
        return notificationService.getNotificationsByReceiverIdWithin2Weeks(userId)
            .collectList().map(data -> NotificationHistory.builder().data(data).build());
    }

    @GetMapping("/notifications/page")
    public Mono<NotificationPage> getNotificationsPage(
        @RequestHeader(name = "Authorization") Integer userId,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("getNotificationsPage: userId={}, page={}, size={}", userId, page, size);
        return notificationService.getNotificationsByReceiverIdWithin2WeeksWithPaging(userId, page,
            size);
    }

    @PatchMapping("/notifications/read/{id}")
    public Mono<NotificationReadResponse> readNotification(@PathVariable String id) {
        return notificationService.markAsRead(id)
            .thenReturn(
                NotificationReadResponse.builder().id(id).status("success").build()
            );
    }

    @PostMapping("/notifications/read-all")
    public Mono<NotificationReadAllResponse> readAllNotifications(@RequestBody
    NotificationReadAllRequest readAllRequest) {
        return Mono.justOrEmpty(readAllRequest.getIds())
            .defaultIfEmpty(List.of())
            .flatMap(ids -> ids.isEmpty() ?
                Mono.error(ErrorCode.REQUEST_VALIDATION.exception("ids")) :
                notificationService.markAllAsRead(ids))
            .thenReturn(
                NotificationReadAllResponse.builder().ids(readAllRequest.getIds()).status("success").build()
            );
    }
}
