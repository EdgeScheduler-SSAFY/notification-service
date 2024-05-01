package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class KafkaMessageJsonSerializer extends JsonSerializer<NotificationEvent> {

    public KafkaMessageJsonSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }
}
