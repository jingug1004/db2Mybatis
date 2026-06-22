package com.example.demo.customer.exception;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(CustomerNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), "CUSTOMER_NOT_FOUND", ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first
                ));

        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), "VALIDATION_FAILED", "Invalid request.", errors));
    }
}
