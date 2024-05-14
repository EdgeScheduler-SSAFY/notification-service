package com.edgescheduler.notificationservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationReadResponse {

    private String id;
    private String status;
}
