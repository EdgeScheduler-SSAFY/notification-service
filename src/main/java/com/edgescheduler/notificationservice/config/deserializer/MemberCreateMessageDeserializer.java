package com.edgescheduler.notificationservice.config.deserializer;

import com.edgescheduler.notificationservice.message.MemberCreateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

public class MemberCreateMessageDeserializer implements Deserializer<MemberCreateMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public MemberCreateMessage deserialize(String topic, byte[] data) {

        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, MemberCreateMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing ChangeTimeZoneMessage", e);
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
