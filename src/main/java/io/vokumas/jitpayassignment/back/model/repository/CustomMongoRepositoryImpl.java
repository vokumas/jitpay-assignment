package io.vokumas.jitpayassignment.back.model.repository;

import com.google.common.base.Preconditions;
import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.MongoUserSingleLocation;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.BsonBinary;
import org.bson.UuidRepresentation;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomMongoRepositoryImpl implements CustomMongoRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * Adds a Location to the User document.
     * This method would not insert Location into there is User document if User.locations is null.
     * This method guarantees to not insert a malformed User document in case User was not previously in the DB.
     * @param userId userId to be searched by.
     * @param location Location to be inserted.
     * @return Optional<User> appended to with new Location or return empty Optional if no User present in the DB.
     */
    @Override
    public Optional<User> addLocation(final UUID userId, final Location location) {
        Preconditions.checkArgument(userId != null,
                "userId cannot be null");
        Preconditions.checkArgument(location != null,
                "location cannot be null");
        Preconditions.checkArgument(location.getCreatedOn() != null,
                "location.createdOn cannot be null");
        Preconditions.checkArgument(location.getLongitude() != null,
                "location.longitude cannot be null");
        Preconditions.checkArgument(location.getLatitude() != null,
                "location.latitude cannot be null");

        BsonBinary binary = new BsonBinary(userId, UuidRepresentation.STANDARD);
        Query query = new Query().addCriteria(Criteria.where("_id").is(binary));
        Update updateDef = new Update().push("locations", location);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, updateDef, options, User.class));
    }

    /**
     * Creates of updates a User document.
     * Guarantees to insert a new User document if there were no in the DB.
     * Guarantees to not update User.locations field.
     * @param user User to be inserted or updated.
     * @return User that has been inserted or updated.
     */
    @Override
    public User upsertUser(final User user) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        Preconditions.checkArgument(user.getUserId() != null, "user.userId cannot be null");
        Preconditions.checkArgument(user.getEmail() != null, "user.email cannot be null");
        Preconditions.checkArgument(user.getFirstName() != null, "user.firstName cannot be null");
        Preconditions.checkArgument(user.getSecondName() != null, "user.secondName cannot be null");

        val query = new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(Criteria.where("userId").is(user.getUserId()));
        val update = new Update()
                .set("email", user.getEmail())
                .set("firstName", user.getFirstName())
                .set("secondName", user.getSecondName());
        val options = new FindAndModifyOptions().returnNew(true).upsert(true);

        return mongoTemplate.findAndModify(query, update, options, User.class);
    }

    /**
     * Finds User document by its ID with single latest Location in the User.locations list.
     * Would return User.locations == null when no locations exist.
     * @param userId userId to be searched by.
     * @return A specifically created DTO to contain only one Location object instead of list.
     */
    @Override
    public Optional<MongoUserSingleLocation> findByUserIdAndLatestLocation(final UUID userId) {
        Preconditions.checkArgument(userId != null,"userId cannot be null");
        BsonBinary binary = new BsonBinary(userId, UuidRepresentation.STANDARD);
        MatchOperation match = Aggregation.match(new Criteria("_id").is(binary));

        String addFieldsQuery = """
                    {$addFields : {location : 
                    {$reduce : {
                        input : "$locations",
                        initialValue : null,
                        in : {$cond: [{$gte : ["$$this.createdOn", "$$value.createdOn"]},"$$this", "$$value"]}}
                    }}}""";

        val aggregation = Aggregation.newAggregation(match,
                new RawJsonAggregationOperation(addFieldsQuery));
        val result = mongoTemplate
                .aggregate(aggregation, "user", MongoUserSingleLocation.class);

        return Optional.ofNullable(result.getUniqueMappedResult());
    }

    /**
     * Finds User document by its ID and returns locations in range of [from, to]
     * This method guarantees to only return empty Optional<User> where there is no User document by the userId in the DB,
     * otherwise it returns a User with or without locations.
     * Would return empty User.locations when no locations satisfy search criteria.
     * As mongo db does not have a dedicated time for storing timestamps with timezone the implementations
     * has to resort to LocalDateTime and additionally this also leads to a situation where it needs to be converted
     * both from and to params to UTC time zone so that we can query correctly.
     * @param userId userId to be searched by.
     * @param from beginning date in the range query. Starting 'from'.
     * @param to ending date in the range query. Going 'to'.
     * @return An Optional containing user if it exists with user.locations() filtered by createdOn BETWEEN range
     *         inclusive both sides. Returns empty Optional if no user found.
     */
    @Override
    public Optional<User> findByUserIdAndLocationsInRange(final UUID userId,
                                                          final LocalDateTime from,
                                                          final LocalDateTime to) {
        Preconditions.checkArgument(userId != null,"userId cannot be null");
        Preconditions.checkArgument(from != null,"from cannot be null");
        Preconditions.checkArgument(to != null,"to cannot be null");

        ZonedDateTime fromToZoned = from.atZone(ZoneId.systemDefault());
        ZonedDateTime fromUtcZoned = fromToZoned.withZoneSameInstant(ZoneId.of("UTC"));
        val fromAtUtc = fromUtcZoned.toLocalDateTime();

        ZonedDateTime toToZoned = to.atZone(ZoneId.systemDefault());
        ZonedDateTime toUtcZoned = toToZoned.withZoneSameInstant(ZoneId.of("UTC"));
        val toAtUtc = toUtcZoned.toLocalDateTime();


        BsonBinary binary = new BsonBinary(userId, UuidRepresentation.STANDARD);
        MatchOperation match = Aggregation.match(new Criteria("_id").is(binary));

        String projectionQuery = String.format("""
                    { $project: {
                        _id: 1,
                        email: 1,
                        firstName: 1,
                        secondName: 1,
                        locations: {$filter: {
                            input: '$locations',
                            as: 'item',
                            cond: { $and:[
                                        {$gte: ['$$item.createdOn', ISODate("%sZ")]},
                                        {$lte: ['$$item.createdOn', ISODate("%sZ")]}
                                     ] }
                        }}
                    }}""", fromAtUtc, toAtUtc);

        val aggregation = Aggregation.newAggregation(match,
                new RawJsonAggregationOperation(projectionQuery));
        val result = mongoTemplate
                .aggregate(aggregation, "user", User.class);

        return Optional.ofNullable(result.getUniqueMappedResult());
    }

}
