package ru.netology.cloudstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudstorage.dto.ExceptionResponse;
import ru.netology.cloudstorage.exceptions.BadRequestException;
import ru.netology.cloudstorage.exceptions.InternalServerException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(400, e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerError(InternalServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(500, e.getMessage()));
    }
}
