package io.vokumas.jitpayassignment.back.service.declaration;

import io.vokumas.jitpayassignment.back.model.dto.PutUserLocationRequestDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDtoTimedLocation;
import io.vokumas.jitpayassignment.back.model.dto.UserSingleLocationDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    /**
     * Adds a user location to the User document.
     * This method does not impose guarantees that it would succeed if User document does not contain an array of
     * locations upfront addition.
     * Implementations should guarantee(from its code or from a code it depends on) that no User document would be
     * created if there were no User document in the underlying storage at the moment of addition.
     * @param userLocationDto A struct containing userId to be searched by and data to be inserted
     * @return Should return a User document DTO containing newly added location.
     */
    UserDto putUserLocation(PutUserLocationRequestDto userLocationDto);

    /**
     * Creates new or updates existing User document in underlying storage.
     * This method does not discern between creation and update operations.
     * This method does not guarantee that the operation is done in a single step as
     * this depends on the underlying code.
     * @param userDto A User document DTO to be updated or inserted.
     * @return
     */
    UserDto createOrUpdateUser(UserDto userDto);

    /**
     * Retrieves User document with the latest (by Location.createdOn) Location.
     * Implementations should guarantee that it retrieves a User even when there are no locations added
     * to the User document.
     * @param userId userId to be searched by.
     * @return Returns a specifically crafted DTO that only contains a single field for location
     * and not an array of locations.
     */
    UserSingleLocationDto getUserLatestLocation(UUID userId);

    /** Retrieves User document with locations field filtered by a date range.
     * Implementations should guarantee that it retrieves a User even when there are no locations added
     * to the User document.
     * @param userId userId to be searched by.
     * @param from beginning date in the range query. Starting 'from'.
     * @param to ending date in the range query. Going 'to'.
     * @return Returns a DTO with createdOn field extracted from the Location object.
     */
    UserDtoTimedLocation getUserLocationsRange(UUID userId, LocalDateTime from, LocalDateTime to);

}
