package ru.skypro.homework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerValidationError(ConstraintViolationException e) {
        String resultValidations = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .reduce((s1, s2) -> s1 + ". " + s2).orElse("");
        LOGGER.error("Переданный в запросе json не валиден, ошибки валидации: {}", resultValidations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handlerUserNotFound(UserNotFoundException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(PasswordsNotEqualsException.class)
    public ResponseEntity<String> handlerPasswordsNotEquals(PasswordsNotEqualsException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            EntityNotFoundException.class,
            EmptyResultDataAccessException.class}) //EmptyResultDataAccessException - при удалении несуществующего
    public ResponseEntity<String> handlerEntityNotFound(Exception e) {
        LOGGER.error("Entity not found. " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found. " + e.getMessage());
    }

}
