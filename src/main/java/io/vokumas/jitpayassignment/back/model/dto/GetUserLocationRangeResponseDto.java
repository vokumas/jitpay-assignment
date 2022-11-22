package io.vokumas.jitpayassignment.back.model.dto;

import java.util.List;
import java.util.UUID;

public record GetUserLocationRangeResponseDto(UUID userId, List<LocationTimedDto> locations) {
}
