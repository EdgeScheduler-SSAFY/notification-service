package com.edgescheduler.notificationservice.service;


import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.KafkaEventMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaTestService {

    private final ReactiveKafkaProducerTemplate<String, KafkaEventMessage> producerTemplate;

    public Mono<String> publishMeetingCreateEvent() {
        MeetingCreateMessage meetingCreateMessage = MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(2)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .attendeeIds(IntStream.range(1, 10).boxed().toList())
            .build();
        return producerTemplate.send("meeting-created", meetingCreateMessage)
            .map(result -> result.recordMetadata().toString());
    }

    public Mono<String> publicMeetingDeleteEvent() {
        MeetingDeleteMessage meetingDeleteMessage = MeetingDeleteMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(2)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .attendeeIds(IntStream.range(1, 10).boxed().toList())
            .build();
        return producerTemplate.send("meeting-deleted", meetingDeleteMessage)
            .map(result -> result.recordMetadata().toString());
    }

    public Mono<String> publishAttendeeResponseEvent() {
        AttendeeResponseMessage attendeeResponseMessage = AttendeeResponseMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(1)
            .attendeeId(2)
            .attendeeName("Attendee")
            .response(Response.ACCEPTED)
            .build();
        return producerTemplate.send("attendee-response", attendeeResponseMessage)
            .map(result -> result.recordMetadata().toString());
    }

    public Mono<String> publishAttendeeProposalEvent() {
        AttendeeProposalMessage attendeeProposalMessage = AttendeeProposalMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(1)
            .attendeeId(2)
            .attendeeName("Attendee")
            .proposedStartTime(LocalDateTime.now().plusHours(1))
            .proposedEndTime(LocalDateTime.now().plusHours(2))
            .reason("Reason")
            .build();
        return producerTemplate.send("attendee-proposal", attendeeProposalMessage)
            .map(result -> result.recordMetadata().toString());
    }
}
