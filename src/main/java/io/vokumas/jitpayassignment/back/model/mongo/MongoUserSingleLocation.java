package io.vokumas.jitpayassignment.back.model.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
public class MongoUserSingleLocation {

    @Id
    private UUID userId;

    private String email;

    private String firstName;

    private String secondName;

    private Location location;

}
