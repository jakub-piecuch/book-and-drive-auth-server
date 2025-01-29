package redcode.bookanddrive.auth_server.exceptions;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //    Handle Generic Exceptions
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMediaTypeNotAcceptableException(
        Exception ex, WebRequest request) {
        log.error("Request: {}, has failed with exception.", request, ex);


        HttpMediaTypeNotAcceptableException exception = (HttpMediaTypeNotAcceptableException) ex;
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(exception.getStatusCode().value())
            .reason(exception.getLocalizedMessage())
            .message(exception.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.valueOf((exception.getStatusCode().value())));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(
        Exception ex, WebRequest request
    ) {
        ValidationException exception = (ValidationException) ex;
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(400)
            .reason(exception.getReason())
            .message(exception.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatusCode.valueOf((errorDetails.getStatus())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArguemtNotValidException(
        Exception ex, WebRequest request) {
        log.error("Request: {}, has failed with exception.", request, ex);

        MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
        ProblemDetail problemDetail = exception.getBody();
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(problemDetail.getStatus())
            .reason("missing_or_wrong_" + exception.getFieldError().getField())
            .message(problemDetail.getDetail())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(problemDetail.getStatus()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .reason(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateSchemaException(DuplicateResourceException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordsMismatchException.class)
    public ResponseEntity<ErrorDetails> handlePasswordMismatchException(PasswordsMismatchException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDetails> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    //     Handle Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(
        Exception ex, WebRequest request) {

        log.error("Request: {}, has failed with exception.", request, ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
