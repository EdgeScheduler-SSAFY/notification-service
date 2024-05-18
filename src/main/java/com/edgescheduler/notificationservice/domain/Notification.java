package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "notification")
public class Notification {

    @Id
    private String id;

    private Integer receiverId;

    private Boolean isRead;

    private Long scheduleId;

    @Indexed(direction = IndexDirection.DESCENDING)
    private LocalDateTime occurredAt;
}
