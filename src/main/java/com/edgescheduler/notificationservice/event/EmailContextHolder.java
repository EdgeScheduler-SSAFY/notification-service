package com.edgescheduler.notificationservice.event;

import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

public interface EmailContextHolder {

    String getTemplateName();
    Mono<Context> emailContext();
}
