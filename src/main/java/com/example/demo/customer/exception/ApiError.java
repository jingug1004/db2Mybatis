package com.example.demo.customer.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        Map<String, String> errors
) {

    public static ApiError of(int status, String code, String message, Map<String, String> errors) {
        return new ApiError(Instant.now(), status, code, message, errors);
    }
}
