package io.vokumas.jitpayassignment.web.exception.advice;

import io.vokumas.jitpayassignment.back.exception.JITPayUserNotFoundException;
import io.vokumas.jitpayassignment.web.exception.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
        var message = "Oops. Something wrong happened";
        val logId = getLogId("error", message, ex);

        val response = new RestErrorResponse(
                -1,
                message.toString(),
                logId,
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(JITPayUserNotFoundException.class)
    public final ResponseEntity<RestErrorResponse> handleException(JITPayUserNotFoundException ex) {
        var message = "User not found";
        val logId = getLogId("info", message, ex);

        val response = new RestErrorResponse(
                -1,
                message,
                logId,
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        var message = "Bad request. There are some request data errors";
        val logId = getLogId("error", message, ex);

        var validationErrors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> validationErrors.put(e.getField(), e.getDefaultMessage()));

        val response = new RestErrorResponse(
                -1,
                message,
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

        var message = "Bad request. There are some parameters missing";
        val logId = getLogId("error", message, ex);

        val response = new RestErrorResponse(
                -1,
                message,
                logId,
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private static UUID getLogId(String level, String message, Throwable cause) {
        var logId = UUID.randomUUID();
        message = message + ". LogId: {}";

        switch (level) {
            case "info" -> log.info(message, logId, cause);
            case "error" -> log.error(message, logId, cause);
            case "warn" -> log.warn(message, logId, cause);
            case "debug" -> log.debug(message, logId, cause);
            case "trace" -> log.trace(message, logId, cause);
        }

        return logId;
    }
}
