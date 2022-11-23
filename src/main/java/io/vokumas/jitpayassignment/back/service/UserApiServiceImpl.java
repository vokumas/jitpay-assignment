package io.vokumas.jitpayassignment.back.service;

import io.vokumas.jitpayassignment.back.exception.JITPayUserNotFoundException;
import io.vokumas.jitpayassignment.back.model.dto.PutUserLocationRequestDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDto;
import io.vokumas.jitpayassignment.back.model.dto.UserDtoTimedLocation;
import io.vokumas.jitpayassignment.back.model.dto.UserSingleLocationDto;
import io.vokumas.jitpayassignment.back.model.mapper.UserMapper;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import io.vokumas.jitpayassignment.back.model.repository.UserRepository;
import io.vokumas.jitpayassignment.back.service.declaration.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApiServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository mongoRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto putUserLocation(PutUserLocationRequestDto userLocationDto) {
        val user = mongoRepository.addLocation(userLocationDto.userId(),
                userMapper.dtoToEntity(userLocationDto))
                .orElseThrow(() -> new JITPayUserNotFoundException("User not found", userLocationDto.userId()));

        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto createOrUpdateUser(UserDto userDto) {
        val user = mongoRepository.upsertUser(new User(
                userDto.userId(), userDto.email(), userDto.firstName(), userDto.secondName()));

        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSingleLocationDto getUserLatestLocation(UUID userId) {
        val user = mongoRepository.findByUserIdAndLatestLocation(userId)
                .orElseThrow(() -> new JITPayUserNotFoundException("User not found", userId));

        return userMapper.entityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtoTimedLocation getUserLocationsRange(UUID userId, LocalDateTime from, LocalDateTime to) {
        val user = mongoRepository.findByUserIdAndLocationsInRange(userId, from, to)
                .orElseThrow(() -> new JITPayUserNotFoundException("User not found", userId));

        return userMapper.entityToDtoTimedLocation(user);
    }

}
