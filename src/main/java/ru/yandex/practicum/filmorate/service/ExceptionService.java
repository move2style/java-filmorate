package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeption.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionService {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> throwValidationException(final ValidationException e) {
        log.error(e.getMessage());
        return Map.of("ConditionsNotMetException", e.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> throwNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return Map.of("NotFoundException", e.getMessage());
    }

    @ExceptionHandler(value = DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> throwDuplicationException(final DuplicatedDataException e) {
        log.error(e.getMessage());
        return Map.of("DuplicatedDataException", e.getMessage());
    }
}