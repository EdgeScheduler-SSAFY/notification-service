package com.edgescheduler.notificationservice.kafka;

import static org.junit.jupiter.api.Assertions.*;

import com.edgescheduler.notificationservice.config.deserializer.NotificationMessageJsonDeserializer;
import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.condition.EmbeddedKafkaCondition;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;
import reactor.test.StepVerifier;

@Slf4j
@EmbeddedKafka(topics = {
    KafkaTest.MEETING_CREATED_TOPIC,
    KafkaTest.MEETING_DELETED_TOPIC,
    KafkaTest.MEETING_UPDATED_TOPIC,
    KafkaTest.ATTENDEE_RESPONSE_TOPIC,
    KafkaTest.ATTENDEE_PROPOSAL_TOPIC}, partitions = 2)
public class KafkaTest {

    public static final String MEETING_CREATED_TOPIC = "meeting-created";
    public static final String MEETING_DELETED_TOPIC = "meeting-deleted";
    public static final String MEETING_UPDATED_TOPIC = "meeting-updated";
    public static final String ATTENDEE_RESPONSE_TOPIC = "attendee-response";
    public static final String ATTENDEE_PROPOSAL_TOPIC = "attendee-proposal";

    private static final Duration DEFAULT_VERIFY_TIMEOUT = Duration.ofSeconds(10);

    private static ReactiveKafkaConsumerTemplate<String, NotificationMessage> consumerTemplate;
    private ReactiveKafkaProducerTemplate<String, NotificationMessage> producerTemplate;

    @BeforeEach
    void setUpProducer() {
        Map<String, Object> props = KafkaTestUtils.producerProps(
            EmbeddedKafkaCondition.getBroker());
        log.info("sender props: {}", props);
        producerTemplate = new ReactiveKafkaProducerTemplate<>(senderOptions(props));
    }

    @BeforeAll
    static void setUpConsumer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true",
            EmbeddedKafkaCondition.getBroker());
        log.info("receiver props: {}", props);
        consumerTemplate = new ReactiveKafkaConsumerTemplate<>(receiverOptions(props));
    }

    private SenderOptions<String, NotificationMessage> senderOptions(Map<String, Object> props) {
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return SenderOptions.create(props);
    }

    private static ReceiverOptions<String, NotificationMessage> receiverOptions(
        Map<String, Object> props) {
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NotificationMessageJsonDeserializer.class);
        ReceiverOptions<String, NotificationMessage> options = ReceiverOptions.create(props);
        return options.consumerProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            .subscription(
                List.of(
                    MEETING_CREATED_TOPIC,
                    MEETING_DELETED_TOPIC,
                    MEETING_UPDATED_TOPIC,
                    ATTENDEE_RESPONSE_TOPIC,
                    ATTENDEE_PROPOSAL_TOPIC));
    }

    @DisplayName("단일 미팅 생성 메시지 송/수신 테스트")
    @Test
    void singleMeetingCreateMessageTest() {
        var senderResultMono = producerTemplate.send(MEETING_CREATED_TOPIC, meetingCreateMessage(1L));
        StepVerifier.create(senderResultMono)
            .assertNext(result -> {
                assertEquals(MEETING_CREATED_TOPIC, result.recordMetadata().topic());
            })
            .expectComplete()
            .verify(DEFAULT_VERIFY_TIMEOUT);

        StepVerifier.create(consumerTemplate.receiveAutoAck())
            .assertNext(record -> {
                assertEquals(MEETING_CREATED_TOPIC, record.topic());
                assertNull(record.key());
                assertInstanceOf(MeetingCreateMessage.class, record.value());
                MeetingCreateMessage value = (MeetingCreateMessage) record.value();
                assertEquals(1L, value.getScheduleId());
                assertIterableEquals(IntStream.range(1, 100).boxed().toList(), value.getAttendeeIds());
            })
            .thenCancel()
            .verify(DEFAULT_VERIFY_TIMEOUT);
    }

    @DisplayName("미팅 생성, 수정, 참석자 응답 메시지 송/수신 테스트")
    @Test
    void multipleEventMessageTest() {
        var sendEvent1 = producerTemplate.send(MEETING_CREATED_TOPIC,
            meetingCreateMessage(1L));
        var sendEvent2 = producerTemplate.send(MEETING_UPDATED_TOPIC,
            meetingUpdateMessage(3L));
        var sendEvent3 = producerTemplate.send(MEETING_CREATED_TOPIC,
            meetingCreateMessage(2L));
        var sendEvent4 = producerTemplate.send(ATTENDEE_RESPONSE_TOPIC,
            attendeeResponseMessage(4L, 1, Response.DECLINED));
        sendEvent1.concatWith(sendEvent2).concatWith(sendEvent3).concatWith(sendEvent4)
            .as(StepVerifier::create)
            .assertNext(result -> assertEquals(MEETING_CREATED_TOPIC, result.recordMetadata().topic()))
            .assertNext(result -> assertEquals(MEETING_UPDATED_TOPIC, result.recordMetadata().topic()))
            .assertNext(result -> assertEquals(MEETING_CREATED_TOPIC, result.recordMetadata().topic()))
            .assertNext(result -> assertEquals(ATTENDEE_RESPONSE_TOPIC, result.recordMetadata().topic()))
            .expectComplete()
            .verify(DEFAULT_VERIFY_TIMEOUT);

        AtomicLong id = new AtomicLong(1L);
        StepVerifier.create(consumerTemplate.receiveAutoAck())
            .assertNext(record -> {
                switch (record.topic()) {
                    case MEETING_CREATED_TOPIC:
                        assertInstanceOf(MeetingCreateMessage.class, record.value());
                        MeetingCreateMessage value = (MeetingCreateMessage) record.value();
                        assertEquals(id.getAndIncrement(), value.getScheduleId());
                        break;
                    case MEETING_UPDATED_TOPIC:
                        assertInstanceOf(MeetingUpdateMessage.class, record.value());
                        MeetingUpdateMessage updateValue = (MeetingUpdateMessage) record.value();
                        assertEquals(3L, updateValue.getScheduleId());
                        break;
                    case ATTENDEE_RESPONSE_TOPIC:
                        assertInstanceOf(AttendeeResponseMessage.class, record.value());
                        AttendeeResponseMessage responseValue = (AttendeeResponseMessage) record.value();
                        assertEquals(4L, responseValue.getScheduleId());
                        assertEquals(1, responseValue.getAttendeeId());
                        assertEquals(Response.DECLINED, responseValue.getResponse());
                        break;
                    default:
                        fail("Unexpected topic: " + record.topic());
                }
            })
            .thenCancel()
            .verify(DEFAULT_VERIFY_TIMEOUT);
    }

    private MeetingCreateMessage meetingCreateMessage(Long scheduleId) {
        return MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(scheduleId)
            .scheduleName("Meeting")
            .organizerId(1)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .attendeeIds(IntStream.range(1, 100).boxed().toList())
            .build();
    }

    private MeetingUpdateMessage meetingUpdateMessage(Long scheduleId) {
        return MeetingUpdateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(scheduleId)
            .scheduleName("Updated Meeting")
            .organizerId(1)
            .organizerName("Organizer")
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(2))
            .maintainedAttendeeIds(IntStream.range(1, 40).boxed().toList())
            .removedAttendeeIds(IntStream.range(41, 60).boxed().toList())
            .addedAttendeeIds(IntStream.range(61, 100).boxed().toList())
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE))
            .build();
    }

    private AttendeeResponseMessage attendeeResponseMessage(Long scheduleId, Integer attendeeId, Response response) {
        return AttendeeResponseMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(scheduleId)
            .scheduleName("Meeting")
            .attendeeId(attendeeId)
            .attendeeName("Attendee")
            .response(response)
            .build();
    }

    @AfterEach
    void tearDown() {
        producerTemplate.close();
    }
}
