package io.vokumas.jitpayassignment.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RestErrorResponse(Integer code, String message, UUID logId, Map<String, String> validationErrors) {
}
