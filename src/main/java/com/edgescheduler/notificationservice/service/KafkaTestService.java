package com.edgescheduler.notificationservice.service;


import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.AttendeeProposalMessage;
import com.edgescheduler.notificationservice.message.AttendeeResponseMessage;
import com.edgescheduler.notificationservice.message.NotificationMessage;
import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaTestService {

    private final ReactiveKafkaProducerTemplate<String, NotificationMessage> producerTemplate;

    public Mono<String> publishMeetingCreateEvent() {
        MeetingCreateMessage meetingCreateMessage = MeetingCreateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(2)
            .organizerName("Organizer")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
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
            .runningTime(60)
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
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
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
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .proposalId(1L)
            .proposedStartTime(LocalDateTime.now().plusHours(1))
            .proposedEndTime(LocalDateTime.now().plusHours(2))
            .runningTime(60)
            .reason("Reason")
            .build();
        return producerTemplate.send("attendee-proposal", attendeeProposalMessage)
            .map(result -> result.recordMetadata().toString());
    }

    public Mono<String> publishMeetingUpdateEvent() {
        MeetingUpdateMessage meetingUpdateMessage = MeetingUpdateMessage.builder()
            .occurredAt(LocalDateTime.now())
            .scheduleId(1L)
            .scheduleName("Meeting")
            .organizerId(2)
            .organizerName("Organizer")
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE))
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusHours(1))
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(2))
            .runningTime(60)
            .maintainedAttendeeIds(IntStream.range(1, 5).boxed().toList())
            .addedAttendeeIds(IntStream.range(10, 13).boxed().toList())
            .removedAttendeeIds(IntStream.range(5, 10).boxed().toList())
            .build();
        return producerTemplate.send("meeting-updated", meetingUpdateMessage)
            .map(result -> result.recordMetadata().toString());
    }
}
