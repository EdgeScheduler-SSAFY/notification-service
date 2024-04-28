package com.edgescheduler.notificationservice.service;

import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final EventSinkManager eventSinkManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AtomicLong counter = new AtomicLong(0);

    public void publish(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    @KafkaListener(topics = "${KAFKA_NOTIFICATION_TOPIC}", groupId = "${KAFKA_GROUP_ID}")
    private void consume(
        ConsumerRecord<String, String> message,
        ConsumerRecordMetadata metadata
    ) {
        log.info("Consumed message: {} | From partition: {} | From offset: {}", message.value(), metadata.partition(), metadata.offset());

    }
}
