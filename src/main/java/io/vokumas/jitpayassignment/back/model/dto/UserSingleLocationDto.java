package io.vokumas.jitpayassignment.back.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UserSingleLocationDto(
        UUID userId,
        String email,
        String firstName,
        String secondName,
        LocationDto location
        ) {
}
