package com.sumit.creatorstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createErrorBody(
            HttpStatus status, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return body;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(createErrorBody(status, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(
            InsufficientStockException ex) {

        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(createErrorBody(status, ex.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            InvalidRequestException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(createErrorBody(status, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null
                                ? "Invalid value"
                                : error.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        Map<String, Object> body = createErrorBody(
                status, "Request validation failed");

        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(
            HttpMessageNotReadableException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(createErrorBody(
                        status,
                        "Request body is missing or contains invalid JSON or data types"));
    }
}
