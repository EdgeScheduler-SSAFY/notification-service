package com.edgescheduler.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class NotificationMessage {

    @JsonProperty("type")
    private NotificationType type;
    private Integer receiverId;
    private LocalDateTime notifiedTime;
    private Long scheduleId;
    private String scheduleName;
    private Boolean isRead;

    public enum NotificationType {
        SCHEDULE_CREATED,
        SCHEDULE_UPDATED_NOT_TIME,
        SCHEDULE_UPDATED_TIME,
        SCHEDULE_DELETED,
        ATTENDEE_RESPONSE,
        ATTENDEE_SCHEDULE_PROPOSAL
    }
}
