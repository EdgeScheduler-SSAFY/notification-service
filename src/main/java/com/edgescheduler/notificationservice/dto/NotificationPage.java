package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.event.NotificationEvent;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationPage {

    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Integer totalElements;
    private List<NotificationEvent> data;
}
