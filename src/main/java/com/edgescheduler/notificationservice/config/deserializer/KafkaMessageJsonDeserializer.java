package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.EventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public class KafkaMessageJsonDeserializer extends JsonDeserializer<EventMessage> {

    public KafkaMessageJsonDeserializer() {
        super(EventMessage.class);
    }

    @Override
    public EventMessage deserialize(String topic, byte[] data) {

        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            log.info("jsonNode = {}", jsonNode.toString());
            Class<? extends EventMessage> targetClass = getTargetClass(topic);
            log.info("targetClass: {}", targetClass);
            return objectMapper.treeToValue(jsonNode, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EventMessage deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    private Class<? extends EventMessage> getTargetClass(String topic) {
        return switch (topic) {
            case "meeting-created" -> MeetingCreateMessage.class;
            case "meeting-deleted" -> MeetingDeleteMessage.class;
            case "meeting-updated" -> MeetingUpdateMessage.class;
            case "attendee-response" -> AttendeeResponseMessage.class;
            case "attendee-proposal" -> AttendeeProposalMessage.class;
            default -> throw new IllegalStateException("Topic 이름 오류, topic = " + topic);
        };
    }
}
