package com.edgescheduler.notificationservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ScheduleServiceClient {

    private final WebClient webClient;

    public ScheduleServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://schedule-service").build();
    }
}
