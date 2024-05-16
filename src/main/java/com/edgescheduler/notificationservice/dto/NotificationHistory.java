package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.event.NotificationEvent;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationHistory {

    private List<NotificationEvent> data;
}
