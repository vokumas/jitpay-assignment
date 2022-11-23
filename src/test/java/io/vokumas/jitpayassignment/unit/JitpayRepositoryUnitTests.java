package io.vokumas.jitpayassignment.unit;

import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import io.vokumas.jitpayassignment.back.model.repository.CustomMongoRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class JitpayRepositoryUnitTests {

    @Mock
    private MongoTemplate mongoTemplate;
    private CustomMongoRepositoryImpl repository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        repository = new CustomMongoRepositoryImpl(mongoTemplate);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenParamsMissing_addLocation() {
        assertThatThrownBy(() -> {
            repository.addLocation(null, null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId cannot be null");

        assertThatThrownBy(() -> {
            repository.addLocation(UUID.randomUUID(), null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location cannot be null");

        assertThatThrownBy(() -> {
            repository.addLocation(UUID.randomUUID(), new Location(null, null, null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.createdOn cannot be null");

        assertThatThrownBy(() -> {
            repository.addLocation(UUID.randomUUID(), new Location(null, null, LocalDateTime.now()));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.longitude cannot be null");

        assertThatThrownBy(() -> {
            repository.addLocation(UUID.randomUUID(), new Location(null, 2.0, LocalDateTime.now()));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.latitude cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenParamsMissing_upsertUser() {
        assertThatThrownBy(() -> {
            repository.upsertUser(null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user cannot be null");

        assertThatThrownBy(() -> {
            repository.upsertUser(new User(null, null, null, null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user.userId cannot be null");

        assertThatThrownBy(() -> {
            repository.upsertUser(new User(UUID.randomUUID(), null, null, null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user.email cannot be null");

        assertThatThrownBy(() -> {
            repository.upsertUser(new User(UUID.randomUUID(), "email@email.com", null, null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user.firstName cannot be null");

        assertThatThrownBy(() -> {
            repository.upsertUser(new User(UUID.randomUUID(), "email@email.com", "firstName", null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user.secondName cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenParamsMissing_getLatest() {
        assertThatThrownBy(() -> {
            repository.findByUserIdAndLatestLocation(null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenParamsMissing_getInRange() {
        assertThatThrownBy(() -> {
            repository.findByUserIdAndLocationsInRange(null, null, null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId cannot be null");

        assertThatThrownBy(() -> {
            repository.findByUserIdAndLocationsInRange(UUID.randomUUID(), null, null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("from cannot be null");

        assertThatThrownBy(() -> {
            repository.findByUserIdAndLocationsInRange(UUID.randomUUID(), LocalDateTime.now(), null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("to cannot be null");
    }
}
