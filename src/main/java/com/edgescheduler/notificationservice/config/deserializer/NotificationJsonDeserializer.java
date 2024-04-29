package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.dto.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.dto.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.dto.NotificationMessage;
import com.edgescheduler.notificationservice.dto.NotificationMessage.NotificationType;
import com.edgescheduler.notificationservice.dto.ScheduleCreateMessage;
import com.edgescheduler.notificationservice.dto.ScheduleDeleteMessage;
import com.edgescheduler.notificationservice.dto.ScheduleUpdateNotTimeMessage;
import com.edgescheduler.notificationservice.dto.ScheduleUpdateTimeMessage;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public class NotificationJsonDeserializer extends JsonDeserializer<NotificationMessage> {

    public NotificationJsonDeserializer() {
        super(NotificationMessage.class);
    }

    @Override
    public NotificationMessage deserialize(String topic, byte[] data) {

        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            log.info("jsonNode = {}", jsonNode.toString());
            String type = jsonNode.get("type").asText();
            log.info("type: {}", type);
            Class<? extends NotificationMessage> targetClass = getTargetClass(type);
            log.info("targetClass: {}", targetClass);
            return objectMapper.treeToValue(jsonNode, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotificationMessage deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    private Class<? extends NotificationMessage> getTargetClass(String type) {

        NotificationType notificationType = NotificationType.valueOf(type);
        log.info("notificationType: {}", notificationType);
        return switch (notificationType) {
            case SCHEDULE_CREATED -> ScheduleCreateMessage.class;
            case SCHEDULE_DELETED -> ScheduleDeleteMessage.class;
            case SCHEDULE_UPDATED_NOT_TIME -> ScheduleUpdateNotTimeMessage.class;
            case SCHEDULE_UPDATED_TIME -> ScheduleUpdateTimeMessage.class;
            case ATTENDEE_RESPONSE -> AttendeeResponseMessage.class;
            case ATTENDEE_SCHEDULE_PROPOSAL -> AttendeeProposalMessage.class;
        };
    }
}
