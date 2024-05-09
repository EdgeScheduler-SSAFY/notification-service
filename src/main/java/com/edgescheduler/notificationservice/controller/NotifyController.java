package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.service.EventSinkManager;
import com.edgescheduler.notificationservice.service.NotificationService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class NotifyController {

    private final EventSinkManager eventSinkManager;
    private final NotificationService notificationService;

    @GetMapping(path = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Object>> sse(
        @RequestHeader(name = "Authorization", required = false) Integer userId,
        @RequestHeader(name = "Last-Event-ID", required = false, defaultValue = "0") Long lastEventId,
        @PathVariable Integer memberId) {
        return Flux.<ServerSentEvent<Object>>create(sink -> {
                if (eventSinkManager.addEventSink(memberId, sink, lastEventId)) {
                    sink.onDispose(() -> eventSinkManager.removeEventSink(memberId));
                } else {
                    sink.error(ErrorCode.DUPLICATE_CONNECTION.build());
                }
            }
        ).onBackpressureBuffer();
    }

    @PatchMapping("/notifications/{id}/read")
    public Mono<Map<String, Object>> readNotification(@PathVariable Long id) {
        return notificationService.markAsRead(id)
            .then(Mono.defer(
                () -> Mono.just(Map.of("id", id, "isRead", Boolean.TRUE))));
    }

    @PostMapping("/notifications/read-all")
    public Mono<Map<String, Object>> readAllNotifications(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw ErrorCode.REQUEST_VALIDATION.build("ids");
        }
        return notificationService.markAllAsRead(ids)
            .then(Mono.just(Map.of("ids", ids, "isRead", Boolean.TRUE)));
    }
}
