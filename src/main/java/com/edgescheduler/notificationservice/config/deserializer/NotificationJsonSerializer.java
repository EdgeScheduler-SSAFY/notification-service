package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class NotificationJsonSerializer extends JsonSerializer<NotificationMessage> {

    public NotificationJsonSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }
}
