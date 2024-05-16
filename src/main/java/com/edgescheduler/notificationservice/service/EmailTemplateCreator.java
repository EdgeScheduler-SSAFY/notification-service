package com.edgescheduler.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmailTemplateCreator {

    private final SpringTemplateEngine templateEngine;

    public Mono<String> createTemplate(String templateName, Context context) {
        return Mono.fromCallable(() -> templateEngine.process(templateName, context));
    }
}
