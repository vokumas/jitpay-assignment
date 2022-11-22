package io.vokumas.jitpayassignment.web.exception.advice;

import io.vokumas.jitpayassignment.back.exception.JITPayUserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Marker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public final ResponseEntity<RestErrorResponse> handleException(RuntimeException ex) {
        val logId = getLogId();

        val response = new RestErrorResponse(
                -1,
                "Oops. Something wrong happened",
                logId,
                null
        );

        log.error("Unexpected error occurred. Log id {} for details", logId, ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(JITPayUserNotFoundException.class)
    public final ResponseEntity<RestErrorResponse> handleException(JITPayUserNotFoundException ex) {
        val logId = getLogId();

        val response = new RestErrorResponse(
                -1,
                "User not found",
                logId,
                null
        );

        log.warn("User {} not found. Log id {} for details", ex.getUserId(), logId, ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        val logId = getLogId();

        var validationErrors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> validationErrors.put(e.getField(), e.getDefaultMessage()));

        val response = new RestErrorResponse(
                -1,
                "Bad request. There are some request data errors",
                logId,
                validationErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @Override
    public final ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        val logId = getLogId();

        val response = new RestErrorResponse(
                -1,
                "Bad request. There are some parameters missing",
                logId,
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private static UUID getLogId() {
        return UUID.randomUUID();
    }
}
