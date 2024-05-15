package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public class NotificationMessageJsonDeserializer extends JsonDeserializer<NotificationMessage> {

    private String meetingCreatedTopic;
    private String meetingDeletedTopic;
    private String meetingUpdatedTopic;
    private String attendeeResponseTopic;
    private String attendeeProposalTopic;

    public NotificationMessageJsonDeserializer() {
        super(NotificationMessage.class);
    }

    @Override
    public NotificationMessage deserialize(String topic, byte[] data) {

        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            log.info("jsonNode = {}", jsonNode.toString());
            Class<? extends NotificationMessage> targetClass = getTargetClass(topic);
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

    private Class<? extends NotificationMessage> getTargetClass(String topic) {
        if (topic.equals(meetingCreatedTopic)) {
            return MeetingCreateMessage.class;
        }
        if (topic.equals(meetingDeletedTopic)) {
            return MeetingDeleteMessage.class;
        }
        if (topic.equals(meetingUpdatedTopic)) {
            return MeetingUpdateMessage.class;
        }
        if (topic.equals(attendeeResponseTopic)) {
            return AttendeeResponseMessage.class;
        }
        if (topic.equals(attendeeProposalTopic)) {
            return AttendeeProposalMessage.class;
        }
        throw ErrorCode.KAFKA_TOPIC_NOT_FOUND.exception(topic);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        super.configure(configs, isKey);
        meetingCreatedTopic = (String) configs.get("topic.meeting-created");
        meetingDeletedTopic = (String) configs.get("topic.meeting-deleted");
        meetingUpdatedTopic = (String) configs.get("topic.meeting-updated");
        attendeeResponseTopic = (String) configs.get("topic.attendee-response");
        attendeeProposalTopic = (String) configs.get("topic.attendee-proposal");
    }
}
