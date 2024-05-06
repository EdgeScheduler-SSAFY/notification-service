package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.exception.ErrorCode;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    // TODO: send(MimeMessage... mimeMessages) 메서드를 통해 여러 메시지를 한 번에 보낼 수 있도록 수정해야함.
    public Mono<Object> sendEmail(String to, String subject, String code) {
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
                throw ErrorCode.MAIL_SEND_ERROR.build();
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
