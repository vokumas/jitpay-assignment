package io.vokumas.jitpayassignment;

import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import io.vokumas.jitpayassignment.back.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class JitpayMongoRepositoryIntegrationTests {

    @Container
    private final static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:latest"); // version is to be set as stable for a real application

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository.deleteAll();
    }

    @Test
    void shouldInsertNewUsers_CountUsers() {
        this.userRepository.upsertUser(new User(UUID.randomUUID(), "jumeirah66@salst.engineer",
                "Jumeirah", "Espinoza"));
        this.userRepository.upsertUser(new User(UUID.randomUUID(), "erpppj@stinkypoopoo.com",
                "Cool Name", "Some cool Lastname"));
        this.userRepository.upsertUser(new User(UUID.randomUUID(), "jont273@akanshabhatia.com",
                "John", "Canmore"));

        List<User> users = userRepository.findAll();
        assertEquals(3, users.size());
    }

    @Test
    void shouldInsertOne_MatchFields() {
        var userId = java.util.UUID.randomUUID();
        this.userRepository.upsertUser(new User(userId, "jumeirah66@salst.engineer",
                "Jumeirah", "Espinoza"));

        var user = userRepository.findUserByUserId(userId).get();

        assertEquals(userId, user.getUserId());
        assertEquals("jumeirah66@salst.engineer", user.getEmail());
        assertEquals("Jumeirah", user.getFirstName());
        assertEquals("Espinoza", user.getSecondName());
    }

    @Test
    void shouldInsert_RetrieveByNonExistingId_ShouldReturnEmptyOptional() {
        var userId = UUID.randomUUID();
        this.userRepository.upsertUser(new User(userId, "jumeirah66@salst.engineer",
                "Jumeirah", "Espinoza"));

        var user = userRepository.findUserByUserId(UUID.randomUUID());

        assertTrue(user.isEmpty());
    }

    @Test
    void shouldInsertOneWithLocations_CountLocations() {
        var userId = java.util.UUID.randomUUID();
        var user = new User(userId, "jumeirah66@salst.engineer",
                "Jumeirah", "Espinoza");

        userRepository.upsertUser(user);
        userRepository.addLocation(userId, new Location(-5.86906, 172.35367, LocalDateTime.now().minus(10, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(23.77165, 92.73155, LocalDateTime.now().minus(9, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(7.15081, 163.10450, LocalDateTime.now().minus(8, ChronoUnit.DAYS)));

        var retUser = userRepository.findUserByUserId(userId).get();

        assertEquals(3, retUser.getLocations().size());
    }

    @Test
    void shouldAddSomeLocationsAsAUserField_CountLocations_ShouldReturnZero() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var latestLocationDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        var locations = Arrays.asList(
                new Location(-68.27599, 130.78756, LocalDateTime.now().minus(6, ChronoUnit.DAYS)),
                new Location(-13.97378, 138.91421, LocalDateTime.now().minus(5, ChronoUnit.DAYS)),
                new Location(52.25742342295784, 10.540583401747602, latestLocationDate)
        );
        user.setLocations(locations);
        userRepository.upsertUser(user);

        var retUser = userRepository.findUserByUserId(userId).get();

        assertEquals(0, retUser.getLocations().size());
    }

    @Test
    void shouldAddSomeLocations_ShouldReturnLatestLocation_MatchFields() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var latestLocationDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        userRepository.upsertUser(user);

        userRepository.addLocation(userId, new Location(-5.86906, 172.35367, LocalDateTime.now().minus(10, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(23.77165, 92.73155, LocalDateTime.now().minus(9, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(7.15081, 163.10450, LocalDateTime.now().minus(8, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(12.42749, 107.37883, LocalDateTime.now().minus(7, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(-68.27599, 130.78756, LocalDateTime.now().minus(6, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(-13.97378, 138.91421, LocalDateTime.now().minus(5, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(52.25742342295784, 10.540583401747602, latestLocationDate));

        var retUser = userRepository.findByUserIdAndLastLocation(userId).get();

        //These need to be converted to millis, since java stores fractions of a second in nanos,
        // while mongo can store only millis
        assertEquals(latestLocationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                retUser.getLocation().getCreatedOn().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertEquals(52.25742342295784, retUser.getLocation().getLatitude());
        assertEquals(10.540583401747602, retUser.getLocation().getLongitude());
    }

    @Test
    void shouldAddSomeLocations_ShouldReturnEmptyUserOptionalWhenNoUserExistsForLatestLocation() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var latestLocationDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        userRepository.upsertUser(user);

        userRepository.addLocation(userId, new Location(12.42749, 107.37883, LocalDateTime.now().minus(7, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(-68.27599, 130.78756, LocalDateTime.now().minus(6, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(-13.97378, 138.91421, LocalDateTime.now().minus(5, ChronoUnit.DAYS)));
        userRepository.addLocation(userId, new Location(52.25742342295784, 10.540583401747602, latestLocationDate));

        var retUser = userRepository.findByUserIdAndLastLocation(UUID.randomUUID());

        assertTrue(retUser.isEmpty());

    }

    @Test
    void shouldAddSomeLocations_ShouldReturnLocationsInRange_CountLocations() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var date = LocalDate.of(2022, 11, 1);
        var time = LocalTime.of(0, 0, 0, 0);
        var dateTime = LocalDateTime.of(date, time);

        var from = LocalDateTime.of(2022, 11, 2, 0, 0, 0, 0);
        var to = LocalDateTime.of(2022, 11, 5, 0, 0, 0, 0);

        userRepository.upsertUser(user);

        userRepository.addLocation(userId, new Location(-5.86906, 172.35367,
                dateTime));
        userRepository.addLocation(userId, new Location(23.77165, 92.73155,
                dateTime.plusDays(1)));
        userRepository.addLocation(userId, new Location(7.15081, 163.10450,
                dateTime.plusDays(2)));
        userRepository.addLocation(userId, new Location(12.42749, 107.37883,
                dateTime.plusDays(3)));
        userRepository.addLocation(userId, new Location(-68.27599, 130.78756,
                dateTime.plusDays(4)));
        userRepository.addLocation(userId, new Location(-13.97378, 138.91421,
                dateTime.plusDays(5)));

        var retUser = userRepository.findByUserIdAndLocationsInRange(userId, from, to).get();

        assertEquals(4, retUser.getLocations().size());
    }

    @Test
    void shouldAddSomeLocations_ShouldReturnEmptyLocationWhenNotInRange_CountLocations() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var date = LocalDate.of(2022, 11, 1);
        var time = LocalTime.of(0, 0, 0, 0);
        var dateTime = LocalDateTime.of(date, time);

        var from = LocalDateTime.of(2021, 11, 2, 0, 0, 0, 0);
        var to = LocalDateTime.of(2021, 11, 5, 0, 0, 0, 0);

        userRepository.upsertUser(user);

        userRepository.addLocation(userId, new Location(-5.86906, 172.35367,
                dateTime));
        userRepository.addLocation(userId, new Location(23.77165, 92.73155,
                dateTime.plusDays(1)));
        userRepository.addLocation(userId, new Location(7.15081, 163.10450,
                dateTime.plusDays(2)));
        userRepository.addLocation(userId, new Location(12.42749, 107.37883,
                dateTime.plusDays(3)));
        userRepository.addLocation(userId, new Location(-68.27599, 130.78756,
                dateTime.plusDays(4)));
        userRepository.addLocation(userId, new Location(-13.97378, 138.91421,
                dateTime.plusDays(5)));

        var retUser = userRepository.findByUserIdAndLocationsInRange(userId, from, to).get();

        assertEquals(0, retUser.getLocations().size());
    }

    @Test
    void shouldAddSomeLocations_ShouldReturnEmptyLocationWhenUserHasNoLocations_CountLocations() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var date = LocalDate.of(2022, 11, 1);
        var time = LocalTime.of(0, 0, 0, 0);
        var dateTime = LocalDateTime.of(date, time);

        var from = LocalDateTime.of(2021, 11, 2, 0, 0, 0, 0);
        var to = LocalDateTime.of(2021, 11, 5, 0, 0, 0, 0);

        userRepository.upsertUser(user);

        var retUser = userRepository.findByUserIdAndLocationsInRange(userId, from, to).get();

        assertEquals(0, retUser.getLocations().size());
    }

    @Test
    void shouldAddSomeLocations_ShouldReturnEmptyUserOptionalWhenNoUserExistsForLocationsInRange() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");
        var date = LocalDate.of(2022, 11, 1);
        var time = LocalTime.of(0, 0, 0, 0);
        var dateTime = LocalDateTime.of(date, time);

        var from = LocalDateTime.of(2022, 11, 2, 0, 0, 0, 0);
        var to = LocalDateTime.of(2022, 11, 5, 0, 0, 0, 0);

        userRepository.upsertUser(user);

        userRepository.addLocation(userId, new Location(-5.86906, 172.35367,
                dateTime));
        userRepository.addLocation(userId, new Location(23.77165, 92.73155,
                dateTime.plusDays(1)));
        userRepository.addLocation(userId, new Location(7.15081, 163.10450,
                dateTime.plusDays(2)));
        userRepository.addLocation(userId, new Location(12.42749, 107.37883,
                dateTime.plusDays(3)));
        userRepository.addLocation(userId, new Location(-68.27599, 130.78756,
                dateTime.plusDays(4)));
        userRepository.addLocation(userId, new Location(-13.97378, 138.91421,
                dateTime.plusDays(5)));

        var retUser = userRepository.findByUserIdAndLocationsInRange(UUID.randomUUID(), from, to);

        assertTrue(retUser.isEmpty());
    }

    @Test
    void shouldUpdateExistingUser() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "some@email.com", "Yet another cool name",
                "Even cooler lastname");

        userRepository.upsertUser(user);

        var retUser = userRepository.findUserByUserId(userId).get();
        retUser.setFirstName("David");
        retUser.setSecondName("Goliath-uly");
        retUser.setEmail("new-email@email.com");
        userRepository.upsertUser(retUser);

        var finalUser = userRepository.findUserByUserId(userId).get();

        assertEquals("David", finalUser.getFirstName());
        assertEquals("Goliath-uly", finalUser.getSecondName());
        assertEquals("new-email@email.com", finalUser.getEmail());
    }

    @Test
    void shouldCreateNoUserWhenAddingLocationsToNonExistentUser() {
        var userId = UUID.randomUUID();
        var date = LocalDate.of(2022, 11, 1);
        var time = LocalTime.of(0, 0, 0, 0);
        var dateTime = LocalDateTime.of(date, time);

        var user = userRepository.addLocation(userId,
                new Location(-5.86906, 172.35367, dateTime));

        assertTrue(user.isEmpty());
    }

    //This belongs to unit test rather than here. For a real application with real tests.
    @Test
    void shouldThrowIllegalArgumentException_upsertUser() {
        assertThatThrownBy(() -> {
            userRepository.addLocation(null, null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId is marked non-null but is null");

        assertThatThrownBy(() -> {
            userRepository.addLocation(UUID.randomUUID(), null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location is marked non-null but is null");

        assertThatThrownBy(() -> {
            userRepository.addLocation(UUID.randomUUID(), new Location(null, null, null));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.createdOn cannot be null");

        assertThatThrownBy(() -> {
            userRepository.addLocation(UUID.randomUUID(), new Location(null, null, LocalDateTime.now()));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.longitude cannot be null");

        assertThatThrownBy(() -> {
            userRepository.addLocation(UUID.randomUUID(), new Location(null, 2.0, LocalDateTime.now()));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("location.latitude cannot be null");
    }
}
