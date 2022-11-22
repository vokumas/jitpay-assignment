package io.vokumas.jitpayassignment.back.exception;

public class JITPayException extends RuntimeException {

    public JITPayException() {
    }

    public JITPayException(String message) {
        super(message);
    }

    public JITPayException(String message, Throwable cause) {
        super(message, cause);
    }

    public JITPayException(Throwable cause) {
        super(cause);
    }

    public JITPayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
