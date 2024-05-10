package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;
    private Integer receiverId;
    private LocalDateTime occurredAt;
    private Boolean isRead;
}
