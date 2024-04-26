package com.edgescheduler.notificationservice.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@SuperBuilder
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;
    private Integer userId;
    private LocalDateTime createdAt;
}
