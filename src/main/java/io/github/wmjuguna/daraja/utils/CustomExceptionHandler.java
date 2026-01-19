package io.github.wmjuguna.daraja.utils;

import io.github.wmjuguna.daraja.dtos.ResponseTemplate;
import io.github.wmjuguna.daraja.exceptions.AuthenticationFailed;
import io.github.wmjuguna.daraja.exceptions.ExpressPaymentUnsuccessful;
import io.github.wmjuguna.daraja.exceptions.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = { AuthenticationFailed.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(AuthenticationFailed ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ResponseTemplate<>(null, null, ex.getMessage())
        );
    }

    @ExceptionHandler(value = { ExpressPaymentUnsuccessful.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(ExpressPaymentUnsuccessful ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ResponseTemplate<>(null, null, ex.getMessage())
        );
    }

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(ResourceNotFoundException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ResponseTemplate<>(null, null, ex.getMessage())
        );
    }

    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(NullPointerException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ResponseTemplate<>(null, null, ex.getMessage())
        );
    }
    @ExceptionHandler(value = { ParseException.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(ParseException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ResponseTemplate<>(null, null, ex.getMessage())
        );
    }
}
