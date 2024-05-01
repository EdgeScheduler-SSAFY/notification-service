package com.edgescheduler.notificationservice.domain;

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
}
