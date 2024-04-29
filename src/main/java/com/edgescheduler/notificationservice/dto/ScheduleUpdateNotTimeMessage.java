package com.edgescheduler.notificationservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScheduleUpdateNotTimeMessage extends NotificationMessage {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<UpdatedField> updatedFields;

    public enum UpdatedField {
        TIME,
        TITLE,
        DESCRIPTION
    }
}
