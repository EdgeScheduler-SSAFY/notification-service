package com.edgescheduler.notificationservice.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateNotTimeSseEvent extends NotificationSseEvent {

    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;
    private List<UpdatedField> updatedFields;
}
