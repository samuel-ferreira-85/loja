package com.samuel.loja.controllers.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        var error = StandardError.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource not found.")
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleInvalidUUIDException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;

        var error = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Not found.")
            .message("Não há recurso para o ID informado.")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(DataBaseException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;

        var error = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Database exception.")
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
}
