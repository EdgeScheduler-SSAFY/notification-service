package com.edgescheduler.notificationservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApplicationAdvice {

    @ExceptionHandler(ApplicationException.class)
    Mono<ResponseEntity<ErrorResponse>> handleApplicationException(ApplicationException e) {
        return Mono.just(ResponseEntity
            .status(e.getHttpStatus())
            .body(new ErrorResponse(e.getCode(), e.getMessage()))
        );
    }

    public record ErrorResponse(String code, String message) {}
}
