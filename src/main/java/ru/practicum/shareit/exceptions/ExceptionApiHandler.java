package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ExceptionHandler(AlreadyUsedEmail.class)
    public ResponseEntity<ErrorMessage> alreadyUsedEmail(AlreadyUsedEmail e) {
        log.error("already used email: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Already used email:" + e.getMessage()));
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ErrorMessage> entityNotFound(EntityNotFound e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(InappropriateUser.class)
    public ResponseEntity<ErrorMessage> inappropriateUser(InappropriateUser e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }
}
