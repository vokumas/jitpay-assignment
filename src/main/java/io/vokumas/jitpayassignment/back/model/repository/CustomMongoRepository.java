package io.vokumas.jitpayassignment.back.model.repository;

import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.MongoUserSingleLocation;
import io.vokumas.jitpayassignment.back.model.mongo.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CustomMongoRepository {

    Optional<User> addLocation(UUID userId, Location location);
    User upsertUser(User user);
    Optional<MongoUserSingleLocation> findByUserIdAndLastLocation(UUID userId);
    Optional<User> findByUserIdAndLocationsInRange(UUID userId, LocalDateTime from, LocalDateTime to);

}
