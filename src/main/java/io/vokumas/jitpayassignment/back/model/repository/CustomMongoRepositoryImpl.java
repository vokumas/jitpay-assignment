package io.vokumas.jitpayassignment.back.model.repository;

import com.google.common.base.Preconditions;
import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import io.vokumas.jitpayassignment.back.model.mongo.MongoUserSingleLocation;
import lombok.NonNull;
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

    @Override
    public Optional<User> addLocation(@NonNull final UUID userId, @NonNull final Location location) {
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

    @Override
    public User upsertUser(@NonNull User user) {
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

    @Override
    public Optional<MongoUserSingleLocation> findByUserIdAndLastLocation(@NonNull UUID userId) {
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
     * Finds user by its ID and returns locations in range of [from, to]
     * As mongo db does not have a dedicated time for storing timestamps with timezone we have to resort to LocalDateTime
     * and additionally this also leads us to a situation where we need convert both from and to params to UTC time zone
     * so that we can query correctly.
     * @param userId
     * @param from
     * @param to
     * @return An Optional containing user if it exists with user.locations() filtered by createdOn BETWEEN range
     *         inclusive both sides. Returns empty Optional if no user found.
     */
    @Override
    public Optional<User> findByUserIdAndLocationsInRange(@NonNull UUID userId,
                                                          @NonNull LocalDateTime from,
                                                          @NonNull LocalDateTime to) {
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
