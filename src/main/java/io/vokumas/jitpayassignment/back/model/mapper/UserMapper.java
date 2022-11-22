package io.vokumas.jitpayassignment.back.model.mapper;

import io.vokumas.jitpayassignment.back.model.dto.*;
import io.vokumas.jitpayassignment.back.model.mongo.Location;
import io.vokumas.jitpayassignment.back.model.mongo.MongoUserSingleLocation;
import io.vokumas.jitpayassignment.back.model.mongo.User;
import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User dtoToEntity(UserDto dto);

    UserDto entityToDto(User entity);

    UserSingleLocationDto entityToDto(MongoUserSingleLocation entity);

    Location dtoToEntity(LocationDto dto);

    @Mapping(source = "location.latitude", target = "latitude")
    @Mapping(source = "location.longitude", target = "longitude")
    Location dtoToEntity(PutUserLocationRequestDto dto);

    LocationDto entityToDto(Location entity);

    UserDtoTimedLocation entityToDtoTimedLocation(User entity);

    List<LocationTimedDto> entityLocationsToDtoLocations(List<Location> locations);

    default LocationTimedDto map(Location location) {
        val dto = new LocationTimedDto(location.getCreatedOn(),
                new LocationDto(location.getLatitude(), location.getLongitude()));

        return dto;
    }

}
