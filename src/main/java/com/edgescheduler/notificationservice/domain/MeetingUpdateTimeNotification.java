package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("meetingUpdateTime")
public class MeetingUpdateTimeNotification extends Notification {
    private Long scheduleId;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
    private Integer runningTime;

    public static MeetingUpdateTimeNotification from(Integer attendeeId, MeetingUpdateMessage message) {
        return MeetingUpdateTimeNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .previousStartTime(message.getPreviousStartTime())
            .previousEndTime(message.getPreviousEndTime())
            .updatedStartTime(message.getUpdatedStartTime())
            .updatedEndTime(message.getUpdatedEndTime())
            .runningTime(message.getRunningTime())
            .build();
    }
}
