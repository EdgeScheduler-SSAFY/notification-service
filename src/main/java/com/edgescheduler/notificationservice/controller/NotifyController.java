package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.service.EventSinkManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class NotifyController {

    private final EventSinkManager eventSinkManager;

    @GetMapping(path = "/notify/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
}
