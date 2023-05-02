package app.fortuneconnect.payments.Utils;

import app.fortuneconnect.payments.DTO.ResponseTemplate;
import app.fortuneconnect.payments.Exceptions.AuthenticationFailed;
import app.fortuneconnect.payments.Exceptions.ExpressPaymentUnsuccessful;
import app.fortuneconnect.payments.Exceptions.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { AuthenticationFailed.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(AuthenticationFailed ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ResponseTemplate.builder()
                .error(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(value = { ExpressPaymentUnsuccessful.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(ExpressPaymentUnsuccessful ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ResponseTemplate.builder()
                .error(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(ResourceNotFoundException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ResponseTemplate.builder()
                .error(ex.getMessage())
                .build()
        );
    }

//    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<ResponseTemplate<?>> handleCustomException(NullPointerException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ResponseTemplate.builder()
                .error(ex.getMessage())
                .build()
        );
    }
}
