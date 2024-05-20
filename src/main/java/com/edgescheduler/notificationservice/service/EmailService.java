package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.AttendeeProposalEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateFieldsEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeEvent;
import com.edgescheduler.notificationservice.event.NotificationEvent;
import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MemberInfoRepository memberInfoRepository;
    private final EmailTemplateCreator emailTemplateCreator;

    public Mono<Void> sendEmail(NotificationEvent event) {
        return memberInfoRepository.findById(event.getReceiverId())
            .flatMap(memberInfo -> send(
                memberInfo.getEmail(), "[EdgeScheduler] " + getMailSubject(event), event));
    }

    public Mono<Void> sendSampleEmail(NotificationEvent event) {
        return send(
            "seahee0130@gmail.com", "[EdgeScheduler] " + getMailSubject(event), event);
    }

    public Mono<Void> send(String to, String subject, NotificationEvent event) {
        return event.emailContext()
            .flatMap(
                context -> emailTemplateCreator.createTemplate(event.getTemplateName(), context))
            .flatMap(template -> Mono.fromCallable(() -> {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(template, true);
                return mimeMessage;
            }).onErrorMap(MessagingException.class, ErrorCode.MAIL_CREATE_ERROR::exception))
            .flatMap(mimeMessage -> Mono.fromRunnable(() -> {
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            }).onErrorMap(MessagingException.class, ErrorCode.MAIL_SEND_ERROR::exception))
            .then();
    }

    private String getMailSubject(NotificationEvent event) {
        if (event instanceof MeetingCreateEvent) {
            return "Meeting Created";
        } else if (event instanceof MeetingDeleteEvent) {
            return "Meeting Deleted";
        } else if (event instanceof MeetingUpdateTimeEvent) {
            return "Meeting Time Updated";
        } else if (event instanceof MeetingUpdateFieldsEvent) {
            return "Meeting Fields Updated";
        } else if (event instanceof AttendeeResponseEvent) {
            return "Attendee Response";
        } else if (event instanceof AttendeeProposalEvent) {
            return "Attendee Proposal";
        }
        return "Notification";
    }
}
