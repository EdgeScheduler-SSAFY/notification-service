package com.edgescheduler.notificationservice.config;

import com.edgescheduler.notificationservice.config.deserializer.ChangeTimeZoneMessageDeserializer;
import com.edgescheduler.notificationservice.config.deserializer.MemberEmailMessageDeserializer;
import com.edgescheduler.notificationservice.config.deserializer.NotificationMessageJsonDeserializer;
import com.edgescheduler.notificationservice.message.ChangeTimeZoneMessage;
import com.edgescheduler.notificationservice.message.MemberEmailMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

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
    @Value("${kafka.topic.timezone-configured}")
    private String timeZoneConfiguredTopic;
    @Value("${kafka.topic.member-created}")
    private String memberCreatedTopic;

    @Bean
    public NewTopics notification() {
        return new NewTopics(
            TopicBuilder.name(meetingCreatedTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(meetingDeletedTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(meetingUpdatedTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(attendeeResponseTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(attendeeProposalTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(timeZoneConfiguredTopic).partitions(3).replicas(2).build(),
            TopicBuilder.name(memberCreatedTopic).partitions(3).replicas(2).build()
        );
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, NotificationMessage> producerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> producerProperties = properties.buildProducerProperties(null);
        SenderOptions<String, NotificationMessage> senderOptions = SenderOptions.create(
            producerProperties);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, NotificationMessage> notificationConsumerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties(null);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            NotificationMessageJsonDeserializer.class);
        ReceiverOptions<String, NotificationMessage> receiverOptions = ReceiverOptions.create(
            consumerProperties);
        receiverOptions = receiverOptions.subscription(
            List.of(meetingCreatedTopic, meetingDeletedTopic, meetingUpdatedTopic,
                attendeeResponseTopic, attendeeProposalTopic));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, ChangeTimeZoneMessage> timeZoneConsumerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties(null);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ChangeTimeZoneMessageDeserializer.class);
        ReceiverOptions<String, ChangeTimeZoneMessage> receiverOptions = ReceiverOptions.create(
            consumerProperties);
        receiverOptions = receiverOptions.subscription(List.of(timeZoneConfiguredTopic));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, MemberEmailMessage> emailConsumerTemplate(
        KafkaProperties properties
    ) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties(null);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            MemberEmailMessageDeserializer.class);
        ReceiverOptions<String, MemberEmailMessage> receiverOptions = ReceiverOptions.create(
            consumerProperties);
        receiverOptions = receiverOptions.subscription(List.of(memberCreatedTopic));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
