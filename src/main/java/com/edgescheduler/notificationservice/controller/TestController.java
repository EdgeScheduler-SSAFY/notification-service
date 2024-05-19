package com.edgescheduler.notificationservice.controller;

import com.edgescheduler.notificationservice.client.ScheduleServiceClient;
import com.edgescheduler.notificationservice.client.ScheduleServiceClient.ScheduleInfo;
import com.edgescheduler.notificationservice.client.UserServiceClient;
import com.edgescheduler.notificationservice.client.UserServiceClient.UserInfo;
import com.edgescheduler.notificationservice.event.AttendeeProposalEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseEvent;
import com.edgescheduler.notificationservice.event.AttendeeStatus;
import com.edgescheduler.notificationservice.event.MeetingCreateEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateFieldsEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeEvent;
import com.edgescheduler.notificationservice.event.NotificationType;
import com.edgescheduler.notificationservice.event.Response;
import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.service.EmailService;
import com.edgescheduler.notificationservice.service.KafkaTestService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final KafkaTestService kafkaService;
    private final EmailService emailService;
    private final UserServiceClient userServiceClient;
    private final ScheduleServiceClient scheduleServiceClient;

    @PostMapping("/meeting-created")
    public Mono<String> test(){
        return kafkaService.publishMeetingCreateEvent();
    }

    @PostMapping("/meeting-deleted")
    public Mono<String> test2(){
        return kafkaService.publicMeetingDeleteEvent();
    }

    @PostMapping("/meeting-updated")
    public Mono<String> test7(){
        return kafkaService.publishMeetingUpdateEvent();
    }

    @PostMapping("/attendee-response")
    public Mono<String> test3(){
        return kafkaService.publishAttendeeResponseEvent();
    }

    @PostMapping("/attendee-proposal")
    public Mono<String> test4(){
        return kafkaService.publishAttendeeProposalEvent();
    }

    @PostMapping("/send-email/create")
    public Mono<String> test5(){
        MeetingCreateEvent createEvent = MeetingCreateEvent.builder()
            .id("test")
            .type(NotificationType.MEETING_CREATED)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .isRead(false)
            .organizerId(1)
            .organizerName("주최자 1")
            .scheduleId(2L)
            .scheduleName("미팅 생성 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
            .receiverStatus(AttendeeStatus.PENDING)
            .build();
        return emailService.sendSampleEmail(createEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/delete")
    public Mono<String> test9(){
        MeetingDeleteEvent deleteEvent = MeetingDeleteEvent.builder()
            .id("test")
            .type(NotificationType.MEETING_DELETED)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .isRead(false)
            .organizerId(1)
            .organizerName("주최자 1")
            .scheduleId(2L)
            .scheduleName("미팅 삭제 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
            .build();
        return emailService.sendSampleEmail(deleteEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/update-fields")
    public Mono<String> test10(){
        MeetingUpdateFieldsEvent updateFieldsEvent = MeetingUpdateFieldsEvent.builder()
            .id("test")
            .type(NotificationType.MEETING_UPDATED_FIELDS)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .isRead(false)
            .organizerId(1)
            .organizerName("주최자 1")
            .scheduleId(2L)
            .scheduleName("미팅 수정 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
            .updatedFields(List.of(UpdatedField.TIME, UpdatedField.TITLE, UpdatedField.DESCRIPTION))
            .build();
        return emailService.sendSampleEmail(updateFieldsEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/update-time")
    public Mono<String> test14(){
        MeetingUpdateTimeEvent updateTimeEvent = MeetingUpdateTimeEvent.builder()
            .id("test")
            .type(NotificationType.MEETING_UPDATED_TIME)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .isRead(false)
            .organizerId(1)
            .organizerName("주최자 1")
            .scheduleId(2L)
            .scheduleName("미팅 시간 변경 이메일 테스트")
            .previousStartTime(LocalDateTime.now())
            .previousEndTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
            .updatedStartTime(LocalDateTime.now().plusHours(1))
            .updatedEndTime(LocalDateTime.now().plusHours(2))
            .build();
        return emailService.sendSampleEmail(updateTimeEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/attendee-accept")
    public Mono<String> test11(){
        AttendeeResponseEvent attendeeResponseEvent = AttendeeResponseEvent.builder()
            .id("test")
            .type(NotificationType.ATTENDEE_RESPONSE)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .attendeeId(1)
            .attendeeName("참석자 1")
            .isRead(false)
            .scheduleId(2L)
            .scheduleName("참석자 응답 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .response(Response.ACCEPTED)
            .build();
        return emailService.sendSampleEmail(attendeeResponseEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/attendee-decline")
    public Mono<String> test12(){
        AttendeeResponseEvent attendeeResponseEvent = AttendeeResponseEvent.builder()
            .id("test")
            .type(NotificationType.ATTENDEE_RESPONSE)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .attendeeId(1)
            .attendeeName("참석자 1")
            .isRead(false)
            .scheduleId(2L)
            .scheduleName("참석자 응답 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .response(Response.DECLINED)
            .build();
        return emailService.sendSampleEmail(attendeeResponseEvent)
            .thenReturn("success");
    }

    @PostMapping("/send-email/attendee-proposal")
    public Mono<String> test13(){
        AttendeeProposalEvent attendeeProposalEvent = AttendeeProposalEvent.builder()
            .id("test")
            .type(NotificationType.ATTENDEE_PROPOSAL)
            .receiverId(8)
            .occurredAt(LocalDateTime.now())
            .isRead(false)
            .attendeeId(1)
            .attendeeName("참석자 1")
            .scheduleId(2L)
            .scheduleName("참석자 제안 이메일 테스트")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(1))
            .runningTime(60)
            .proposedStartTime(LocalDateTime.now().plusHours(1))
            .proposedEndTime(LocalDateTime.now().plusHours(2))
            .reason("참석자 제안 이유")
            .build();
        return emailService.sendSampleEmail(attendeeProposalEvent)
            .thenReturn("success");
    }

    @GetMapping("/user-service/{id}")
    public Mono<UserInfo> test6(@PathVariable Integer id){
        return userServiceClient.getUserInfo(id);
    }

    @GetMapping("/schedule-service/{scheduleId}")
    public Mono<ScheduleInfo> test8(
        @PathVariable Long scheduleId,
        @RequestParam Integer receiverId
    ){
        return scheduleServiceClient.getSchedule(scheduleId, receiverId);
    }
}
