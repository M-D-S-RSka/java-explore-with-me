package ru.practicum.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionHelper {
    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bedRequestException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Unhandled exception occurred", e);
        return Map.of("error", "Internal Server Error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException e) {
        log.error("Validation error occurred", e);
        return Map.of("error", Objects.requireNonNull(Objects.requireNonNull(e.getBindingResult().getFieldError())
                .getDefaultMessage()));
    }
}
