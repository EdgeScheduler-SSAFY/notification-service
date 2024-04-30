package com.edgescheduler.notificationservice.message;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class EventMessage {

    private LocalDateTime occurredAt;
}
