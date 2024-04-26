package com.edgescheduler.notificationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

@Component
public class EventSinkManager {

    private final Map<Integer, FluxSink<ServerSentEvent<String>>> userEventSinks = new ConcurrentHashMap<>();
    private final Map<Integer, List<ServerSentEvent<String>>> eventBuffer = new ConcurrentHashMap<>();

    public boolean addEventSink(Integer userId, FluxSink<ServerSentEvent<String>> sink, long lastEventId) {
        var existingSink = userEventSinks.putIfAbsent(userId, sink);
        if (existingSink == null) {
            resendMissedEvents(userId, lastEventId, sink);
            return true;
        }
        return false;
    }

    public void removeEventSink(Integer userId) {
        userEventSinks.remove(userId);
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
