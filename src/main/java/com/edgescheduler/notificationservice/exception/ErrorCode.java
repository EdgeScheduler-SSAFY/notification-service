package com.edgescheduler.notificationservice.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ErrorCode {

    EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "EX001", "This is an example error"),
    DUPLICATE_CONNECTION(HttpStatus.BAD_REQUEST, "NS001", "Only one connection is allowed at a time"),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NS002", "Failed to send email"),
    MAIL_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NS002", "Failed to create email"),
    REQUEST_VALIDATION(HttpStatus.BAD_REQUEST, "NS003", "Request validation failed: %s"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "NS004", "Member not found: id = %d"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "NS005", "Schedule not found: id = %d"),
    KAFKA_TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "KAFKA001", "Kafka topic not found: %s");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ApplicationException exception() {
        return new ApplicationException(httpStatus, code, message);
    }

    public ApplicationException exception(Object... args) {
        return new ApplicationException(httpStatus, code, message.formatted(args));
    }
}
