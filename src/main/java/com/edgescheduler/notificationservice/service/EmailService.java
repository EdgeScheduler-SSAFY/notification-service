package com.edgescheduler.notificationservice.service;

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
                return send(memberInfo.getEmail(),
                    event.getReceiverId() + "번 유저에게.", event.getType().toString())
                    .subscribeOn(Schedulers.boundedElastic());
            })
            .switchIfEmpty(
                Mono.error(ErrorCode.MEMBER_NOT_FOUND.exception(event.getReceiverId() + "번 유저")));
    }

    public Mono<Object> send(String to, String subject, String code) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(getMailContent(code), true);
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
        return templateEngine.process("sample", context);
    }
}
