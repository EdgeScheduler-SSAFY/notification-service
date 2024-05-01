package com.edgescheduler.notificationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

@Component
public class EventSinkManager {

    private final Map<Integer, FluxSink<ServerSentEvent<Object>>> userEventSinks = new ConcurrentHashMap<>();
    private final Map<Integer, List<ServerSentEvent<Object>>> eventBuffer = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    public boolean addEventSink(Integer userId, FluxSink<ServerSentEvent<Object>> sink, long lastEventId) {
        var existingSink = userEventSinks.putIfAbsent(userId, sink);
        if (existingSink == null) {
            sink.next(ServerSentEvent.builder()
                .id(String.valueOf(lastEventId))
                .event("connected")
                .data("Connected Successfully").build());
            resendMissedEvents(userId, lastEventId, sink);
            return true;
        }
        return false;
    }

    public void removeEventSink(Integer userId) {
        userEventSinks.remove(userId);
    }

    public boolean sendEvent(Integer userId, Object event) {
        var sink = userEventSinks.get(userId);
        var sseEvent = ServerSentEvent.builder()
            .id(String.valueOf(counter.incrementAndGet()))
            .event("notification")
            .data(event)
            .build();
        if (sink == null) {
            addToBuffer(userId, sseEvent);
            return false;
        }
        sink.next(sseEvent);
        return true;
    }

    public void addToBuffer(Integer userId, ServerSentEvent<Object> message) {
        eventBuffer.computeIfAbsent(userId, key -> new ArrayList<>()).add(message);
    }

    private void resendMissedEvents(Integer userId, long lastEventId, FluxSink<ServerSentEvent<Object>> sink) {
        eventBuffer.getOrDefault(userId, List.of())
            .stream()
            .filter(event -> convertEventId(event.id()) > lastEventId)
            .forEach(sink::next);
        eventBuffer.remove(userId);
    }

    private long convertEventId(String eventId) {
        return Long.parseLong(eventId);
    }
}
