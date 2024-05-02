package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class KafkaMessageJsonSerializer extends JsonSerializer<NotificationSseEvent> {

    public KafkaMessageJsonSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }
}
