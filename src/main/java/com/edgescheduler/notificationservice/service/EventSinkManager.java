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

    private final Map<Integer, FluxSink<ServerSentEvent<String>>> userEventSinks = new ConcurrentHashMap<>();
    private final Map<Integer, List<ServerSentEvent<String>>> eventBuffer = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    public boolean addEventSink(Integer userId, FluxSink<ServerSentEvent<String>> sink, long lastEventId) {
        var existingSink = userEventSinks.putIfAbsent(userId, sink);
        if (existingSink == null) {
            sink.next(ServerSentEvent.builder("연결이 성공적으로 설정되었습니다.")
                .id(String.valueOf(lastEventId)).build());
            resendMissedEvents(userId, lastEventId, sink);
            return true;
        }
        return false;
    }

    public void removeEventSink(Integer userId) {
        userEventSinks.remove(userId);
    }

    public boolean sendEvent(Integer userId, String message) {
        var sink = userEventSinks.get(userId);
        var event = ServerSentEvent.builder(message)
            .id(String.valueOf(counter.incrementAndGet())).build();
        if (sink == null) {
            addToBuffer(userId, event);
            return false;
        }
        sink.next(event);
        return true;
    }

    public void addToBuffer(Integer userId, ServerSentEvent<String> message) {
        eventBuffer.computeIfAbsent(userId, key -> new ArrayList<>()).add(message);
    }

    private void resendMissedEvents(Integer userId, long lastEventId, FluxSink<ServerSentEvent<String>> sink) {
        eventBuffer.getOrDefault(userId, List.of())
            .stream()
            .filter(event -> convertEventId(event.id()) > lastEventId)
            .forEach(sink::next);
    }

    private long convertEventId(String eventId) {
        return Long.parseLong(eventId);
    }
}
