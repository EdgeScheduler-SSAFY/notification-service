package com.edgescheduler.notificationservice.controller;

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

    @GetMapping(path = "/notify/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<String>> sse(
        @PathVariable Integer userId,
        @RequestHeader(name = "Last-Event-ID", required = false, defaultValue = "0") Long lastEventId
    ) {
        return Flux.<ServerSentEvent<String>>create(sink -> {
                if (eventSinkManager.addEventSink(userId, sink, lastEventId)) {
                    sink.onDispose(() -> eventSinkManager.removeEventSink(userId));
                } else {
                    sink.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "한 번에 하나의 연결만 허용됩니다."));
                }
            }
        ).onBackpressureBuffer();
    }
}
