package com.samuel.loja.controllers.exceptions;

import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var error = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Resource not found.")
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(error);
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
        return ResponseEntity.status(status).body(error);
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
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        var status = HttpStatus.UNPROCESSABLE_ENTITY;

        var error = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Dados inválidos.")
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ValidationError error = new ValidationError();
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Database exception.");
        error.setMessage(ex.getMessage());
        error.setPath(request.getDescription(false));

        for (FieldError f: ex.getBindingResult().getFieldErrors()) {
            error.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }
}
