package ru.practicum.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHelper {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.error("Method Argument Not Valid: {}", e.getMessage(), e);
        return Map.of("error", "Bad Request - Invalid Data");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bedRequestException(final ValidationException e) {
        log.error("Validation Exception: {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String, String> handleAllExceptions(final Throwable e) {
//        log.error("Internal Server Error: {}", e.getMessage(), e);
//        return Map.of("error", "Internal Server Error");
//    }
//

    //  Рекомендованный на прошлом ревью хэндлер перехватывает все подряд и возвращает ошибку "Internal Server Error"

//     1.  AssertionError  Ответ должен содержать код статуса 400
//    expected response to have status reason 'BAD REQUEST' but got 'INTERNAL SERVER ERROR'
//    at assertion:0 in test-script
//    inside "Тест на верную обработку запроса без даты начала"
//
//            2.  AssertionError  Ответ должен содержать код статуса 400
//    expected response to have status reason 'BAD REQUEST' but got 'INTERNAL SERVER ERROR'
//    at assertion:0 in test-script
//    inside "Тест на верную обработку запроса без даты конца"
}
