package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.message.MeetingCreateMessage;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("meetingCreate")
public class MeetingCreateNotification extends Notification {
    private Long scheduleId;

    public static MeetingCreateNotification from(Integer attendeeId, MeetingCreateMessage message) {
        return MeetingCreateNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .build();
    }

    public static MeetingCreateNotification from(Integer attendeeId, MeetingUpdateMessage message) {
        return MeetingCreateNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .build();
    }
}
