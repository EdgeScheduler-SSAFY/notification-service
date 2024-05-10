package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.event.NotificationSseEvent;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationHistory {

    private List<NotificationSseEvent> data;
}
