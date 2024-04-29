package com.edgescheduler.notificationservice.config;

import com.edgescheduler.notificationservice.config.deserializer.NotificationJsonDeserializer;
import com.edgescheduler.notificationservice.config.deserializer.NotificationJsonSerializer;
import com.edgescheduler.notificationservice.dto.NotificationMessage;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${KAFKA_NOTIFICATION_TOPIC:notification}")
    private String topic;

    @Bean
    public NewTopic notification() {
        return TopicBuilder.name(topic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, NotificationMessage> producerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.putAll(Map.of(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NotificationJsonSerializer.class));
        log.info("Producer properties: {}", producerProperties);
        SenderOptions<String, NotificationMessage> senderOptions = SenderOptions.create(
            producerProperties);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, NotificationMessage> consumerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            NotificationJsonDeserializer.class);
        log.info("Consumer properties: {}", consumerProperties);
        ReceiverOptions<String, NotificationMessage> receiverOptions = ReceiverOptions.create(
            consumerProperties);
        receiverOptions = receiverOptions.subscription(
            Collections.singletonList(topic));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
