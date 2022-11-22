package io.vokumas.jitpayassignment.back.model.dto;

import javax.validation.constraints.NotNull;

/**
 * Class for holding latitude and longitude of a request
 * Longitudes, and potentially latitudes, are provided with the precision of 15 decimal digits that is exactly
 * the precision of double type. This means that any additional decimal place would be lost.
 * On the other hand precisions over 10'th decimal place lie in a nm and below range, this accuracy cannot be achieved
 * with any existing GPS so losing decimal places isn't anything bad here. We can actually use floats(6 decimal places)
 * and still get a precision in mm range and save space thanks to floats.
 * @param latitude
 * @param longitude
 */
public record LocationDto(
        @NotNull(message = "latitude cannot be empty")
        Double latitude,
        @NotNull(message = "longitude cannot be empty")
        Double longitude
        ) {

}
