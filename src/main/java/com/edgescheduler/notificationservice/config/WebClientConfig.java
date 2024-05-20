package com.edgescheduler.notificationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final Environment environment;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(environment.getProperty("webclient.user-service.url")).build();
    }

    @Bean
    public WebClient scheduleServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(environment.getProperty("webclient.schedule-service.url")).build();
    }
}
