package io.vokumas.jitpayassignment.back.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.vokumas.jitpayassignment.util.constants.ValidationConstants;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record PutUserLocationRequestDto(
        @NotNull(message = "userId cannot be empty")
        UUID userId,

        @NotNull(message = "createdOn cannot be empty")
        @JsonFormat(pattern = ValidationConstants.TIMESTAMP_DTO_PATTERN,
                timezone = ValidationConstants.TIMESTAMP_DEFAULT_REGION)
        LocalDateTime createdOn,

        @NotNull(message = "location cannot be empty")
        LocationDto location) {
}
