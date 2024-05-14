package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.event.AttendeeProposalSseEvent;
import com.edgescheduler.notificationservice.event.AttendeeResponseSseEvent;
import com.edgescheduler.notificationservice.event.MeetingCreateSseEvent;
import com.edgescheduler.notificationservice.event.MeetingDeleteSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateNotTimeSseEvent;
import com.edgescheduler.notificationservice.event.MeetingUpdateTimeSseEvent;
import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import com.edgescheduler.notificationservice.exception.ErrorCode;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MemberInfoRepository memberInfoRepository;
    private final SpringTemplateEngine templateEngine;

    public Mono<Object> sendEmail(NotificationSseEvent event) {
        return memberInfoRepository.findById(event.getReceiverId())
            .flatMap(memberInfo -> {
                if (memberInfo.getEmail() == null) {
                    return Mono.empty();
                }
                if (event instanceof MeetingCreateSseEvent meetingCreateSseEvent) {
                    return send(memberInfo.getEmail(),
                        meetingCreateSseEvent.getReceiverId() + "번 유저에게.", meetingCreateSseEvent);
                }
                if (event instanceof MeetingDeleteSseEvent meetingDeleteSseEvent) {
                    return send(memberInfo.getEmail(),
                        meetingDeleteSseEvent.getReceiverId() + "번 유저에게.", meetingDeleteSseEvent);
                }
                if (event instanceof MeetingUpdateTimeSseEvent meetingUpdateTimeSseEvent) {
                    return send(memberInfo.getEmail(),
                        meetingUpdateTimeSseEvent.getReceiverId() + "번 유저에게.", meetingUpdateTimeSseEvent);
                }
                if (event instanceof MeetingUpdateNotTimeSseEvent meetingUpdateNotTimeSseEvent) {
                    return send(memberInfo.getEmail(),
                        meetingUpdateNotTimeSseEvent.getReceiverId() + "번 유저에게.", meetingUpdateNotTimeSseEvent);
                }
                if (event instanceof AttendeeResponseSseEvent attendeeResponseSseEvent) {
                    return send(memberInfo.getEmail(),
                        attendeeResponseSseEvent.getReceiverId() + "번 유저에게.", attendeeResponseSseEvent);
                }
                if (event instanceof AttendeeProposalSseEvent attendeeProposalSseEvent) {
                    return send(memberInfo.getEmail(),
                        attendeeProposalSseEvent.getReceiverId() + "번 유저에게.", attendeeProposalSseEvent);
                }

                return Mono.just(new Object());
            })
            .switchIfEmpty(
                Mono.error(ErrorCode.MEMBER_NOT_FOUND.exception(event.getReceiverId() + "번 유저")));
    }

    public Mono<Object> send(String to, String subject, MeetingCreateSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public Mono<Object> send(String to, String subject, MeetingDeleteSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public Mono<Object> send(String to, String subject, MeetingUpdateTimeSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public Mono<Object> send(String to, String subject, MeetingUpdateNotTimeSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public Mono<Object> send(String to, String subject, AttendeeResponseSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public Mono<Object> send(String to, String subject, AttendeeProposalSseEvent event) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(event.getScheduleName()), true);
                log.info("Sending email to {}", to);
                javaMailSender.send(mimeMessage);
                log.info("Email sent");
            } catch (MessagingException e) {
                log.error("Failed to send email", e);
                throw ErrorCode.MAIL_SEND_ERROR.exception();
            }

            return mimeMessage;
        });
    }

    public String getMailContent(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email-notification", context);
    }
}
