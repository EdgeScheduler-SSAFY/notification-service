package com.edgescheduler.notificationservice.message;

import com.edgescheduler.notificationservice.dto.UpdatedField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingUpdateMessage extends EventMessage {

    private Long meetingId;
    private String meetingName;
    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> attendeeIds;
    private List<Integer> maintainedAttendeeIds;
    private List<Integer> addedAttendeeIds;
    private List<Integer> removedAttendeeIds;
    private List<UpdatedField> updatedFields;
}
