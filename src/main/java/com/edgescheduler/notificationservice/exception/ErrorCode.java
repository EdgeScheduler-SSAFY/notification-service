package com.edgescheduler.notificationservice.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ErrorCode {

    EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "EX001", "This is an example error"),
    DUPLICATE_CONNECTION(HttpStatus.BAD_REQUEST, "NS001", "Only one connection is allowed at a time"),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NS002", "Failed to send email"),
    REQUEST_VALIDATION(HttpStatus.BAD_REQUEST, "NS003", "Request validation failed: %s");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ApplicationException build() {
        return new ApplicationException(httpStatus, code, message);
    }

    public ApplicationException build(Object... args) {
        return new ApplicationException(httpStatus, code, message.formatted(args));
    }
}
