package com.edgescheduler.notificationservice.domain;

import com.edgescheduler.notificationservice.dto.UpdatedField;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Getter
@SuperBuilder
@NoArgsConstructor
@TypeAlias("scheduleUpdateNotTime")
public class ScheduleUpdateNotTimeNotification extends Notification {
    private Long scheduleId;
    private String updatedName;
    private List<UpdatedField> updatedFields;
}
