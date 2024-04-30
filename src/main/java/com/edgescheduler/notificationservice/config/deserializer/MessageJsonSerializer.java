package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MessageJsonSerializer extends JsonSerializer<NotificationMessage> {

    public MessageJsonSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }
}
