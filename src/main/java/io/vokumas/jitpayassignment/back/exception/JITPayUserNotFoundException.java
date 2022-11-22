package io.vokumas.jitpayassignment.back.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class JITPayUserNotFoundException extends JITPayException {

    private final UUID userId;

    public JITPayUserNotFoundException(UUID userId) {
        this.userId = userId;
    }

    public JITPayUserNotFoundException(String message, UUID userId) {
        super(message);
        this.userId = userId;
    }

    public JITPayUserNotFoundException(String message, Throwable cause, UUID userId) {
        super(message, cause);
        this.userId = userId;
    }

    public JITPayUserNotFoundException(Throwable cause, UUID userId) {
        super(cause);
        this.userId = userId;
    }

}
