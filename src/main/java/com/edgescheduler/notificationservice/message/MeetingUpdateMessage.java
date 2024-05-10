package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.event.UpdatedField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateMessage extends KafkaEventMessage {

    private Long scheduleId;
    private String scheduleName;
    private Integer organizerId;
    private String organizerName;
    private LocalDateTime previousStartTime;
    private LocalDateTime previousEndTime;
    private LocalDateTime updatedStartTime;
    private LocalDateTime updatedEndTime;
    private List<Integer> maintainedAttendeeIds;
    private List<Integer> addedAttendeeIds;
    private List<Integer> removedAttendeeIds;
    private List<UpdatedField> updatedFields;
}
