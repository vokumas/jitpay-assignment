package io.vokumas.jitpayassignment.back.model.dto;

import io.vokumas.jitpayassignment.util.constants.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public record UserDtoTimedLocation(
        @NotNull(message = "userId cannot be empty")
        UUID userId,

        @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "email should be a valid email according to RFC 5322")
        String email,

        @NotBlank(message = "firstName cannot be empty")
        String firstName,

        @NotBlank(message = "secondName cannot be empty")
        String secondName,

        List<LocationTimedDto> locations
        ) {
}
