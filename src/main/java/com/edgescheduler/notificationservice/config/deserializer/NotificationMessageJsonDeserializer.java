package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public class NotificationMessageJsonDeserializer extends JsonDeserializer<NotificationMessage> {

    @Value("${kafka.topic.meeting-created}")
    private String meetingCreatedTopic;
    @Value("${kafka.topic.meeting-deleted}")
    private String meetingDeletedTopic;
    @Value("${kafka.topic.meeting-updated}")
    private String meetingUpdatedTopic;
    @Value("${kafka.topic.attendee-response}")
    private String attendeeResponseTopic;
    @Value("${kafka.topic.attendee-proposal}")
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
}
