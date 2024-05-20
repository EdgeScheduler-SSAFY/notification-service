package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.message.MeetingDeleteMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("meetingDelete")
public class MeetingDeleteNotification extends Notification {

    private String scheduleName;
    private Integer organizerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningTime;

    public static MeetingDeleteNotification from(Integer attendeeId, MeetingDeleteMessage message) {
        return MeetingDeleteNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .startTime(message.getStartTime())
            .endTime(message.getEndTime())
            .runningTime(message.getRunningTime())
            .build();
    }

    public static MeetingDeleteNotification from(Integer attendeeId, MeetingUpdateMessage message) {
        return MeetingDeleteNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .scheduleName(message.getScheduleName())
            .organizerId(message.getOrganizerId())
            .startTime(message.getUpdatedStartTime())
            .endTime(message.getUpdatedEndTime())
            .runningTime(message.getRunningTime())
            .build();
    }
}
