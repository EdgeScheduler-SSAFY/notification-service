package com.edgescheduler.notificationservice.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class NotificationSseEvent {

    private String id;
    private NotificationType type;
    private Integer receiverId;
    private LocalDateTime occurredAt;
    private Long scheduleId;
    private String scheduleName;
    private Boolean isRead;
}
