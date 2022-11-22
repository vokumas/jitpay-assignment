package io.vokumas.jitpayassignment.back.model.repository;

import io.vokumas.jitpayassignment.back.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID>, CustomMongoRepository {

    Optional<User> findUserByUserId(UUID userId);

}
