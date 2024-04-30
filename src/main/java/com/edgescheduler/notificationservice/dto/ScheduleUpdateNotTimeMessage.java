package com.edgescheduler.notificationservice.dto;

import com.edgescheduler.notificationservice.domain.ScheduleUpdateNotTimeNotification;
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

    public ScheduleUpdateNotTimeNotification toEntity() {
        return ScheduleUpdateNotTimeNotification.builder()
            .receiverId(this.getReceiverId())
            .notifiedAt(this.getNotifiedAt())
            .scheduleId(this.getScheduleId())
            .isRead(this.getIsRead())
            .updatedName(this.getScheduleName())
            .updatedFields(this.getUpdatedFields())
            .build();
    }
}
