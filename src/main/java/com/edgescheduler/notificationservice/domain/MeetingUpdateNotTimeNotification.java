package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.event.UpdatedField;
import com.edgescheduler.notificationservice.message.MeetingUpdateMessage;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("meetingUpdateNotTime")
public class MeetingUpdateNotTimeNotification extends Notification {

    private List<UpdatedField> updatedFields;

    public static MeetingUpdateNotTimeNotification from(Integer attendeeId, MeetingUpdateMessage message) {
        return MeetingUpdateNotTimeNotification.builder()
            .receiverId(attendeeId)
            .occurredAt(message.getOccurredAt())
            .isRead(false)
            .scheduleId(message.getScheduleId())
            .updatedFields(message.getUpdatedFields())
            .build();
    }
}
