package com.edgescheduler.notificationservice.service;

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
//        return memberInfoRepository.findById(event.getReceiverId())
//            .flatMap(memberInfo -> send(
//                memberInfo.getEmail(), event.getReceiverId() + "번 유저에게.", event));
        return send(
            "oh052679@naver.com", event.getReceiverId() + "번 유저에게.", event);
    }

    public Mono<Void> send(String to, String subject, NotificationEvent event) {
        return event.emailContext()
            .flatMap(context -> emailTemplateCreator.createTemplate(event.getTemplateName(), context))
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
}
