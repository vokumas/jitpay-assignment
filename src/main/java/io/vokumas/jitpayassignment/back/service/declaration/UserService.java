package io.vokumas.jitpayassignment.back.service.declaration;

import io.vokumas.jitpayassignment.back.model.dto.PutUserLocationRequestDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDtoTimedLocation;
import io.vokumas.jitpayassignment.back.model.dto.UserSingleLocationDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    UserDto putUserLocation(PutUserLocationRequestDto userLocationDto);
    UserDto createOrUpdateUser(UserDto userDto);
    UserSingleLocationDto getUserLatestLocation(UUID userId);
    UserDtoTimedLocation getUserLocationsRange(UUID userId, LocalDateTime from, LocalDateTime to);

}
