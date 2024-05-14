package com.edgescheduler.notificationservice.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationReadAllResponse {

    private List<String> ids;
    private String status;
}
