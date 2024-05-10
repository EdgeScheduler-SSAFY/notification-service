package com.edgescheduler.notificationservice.domain;

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

    // temporary
//    private String organizerName;
}
