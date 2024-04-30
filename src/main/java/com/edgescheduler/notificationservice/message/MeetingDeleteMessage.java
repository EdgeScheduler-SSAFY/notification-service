package com.edgescheduler.notificationservice.message;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MeetingDeleteMessage extends EventMessage {

    private Long meetingId;
    private String meetingName;
    private Integer organizerId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> attendeeIds;
}
