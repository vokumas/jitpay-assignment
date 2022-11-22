package io.vokumas.jitpayassignment.web.controller;

import io.vokumas.jitpayassignment.back.model.dto.GetUserLocationRangeResponseDto;
import io.vokumas.jitpayassignment.back.model.dto.PutUserLocationRequestDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDto;
import io.vokumas.jitpayassignment.back.model.dto.UserSingleLocationDto;
import io.vokumas.jitpayassignment.back.service.declaration.UserService;
import io.vokumas.jitpayassignment.util.constants.ValidationConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping(value = "/location", consumes = "application/json")
    public ResponseEntity<UserDto> putLocation(@RequestBody @Valid PutUserLocationRequestDto dto) {
        val newLocation = service.putUserLocation(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newLocation);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "application/json")
    public ResponseEntity<UserDto> createOrUpdateUser(@RequestBody @Valid UserDto dto) {
        val user = service.createOrUpdateUser(dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/location/latest")
    public ResponseEntity<UserSingleLocationDto> getUserLatestLocation(@RequestParam UUID userId) {
        val userResponse = service.getUserLatestLocation(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponse);
    }

    @GetMapping("/location/range")
    public ResponseEntity<GetUserLocationRangeResponseDto> getUserLocationsInRange(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(pattern = ValidationConstants.TIMESTAMP_URL_PATTERN) LocalDateTime from,
            @RequestParam @DateTimeFormat(pattern = ValidationConstants.TIMESTAMP_URL_PATTERN) LocalDateTime to
            ) {
        val user = service.getUserLocationsRange(userId, from, to);

        val response = new GetUserLocationRangeResponseDto(userId, user.locations());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
