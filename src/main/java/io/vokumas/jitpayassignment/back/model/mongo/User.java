package io.vokumas.jitpayassignment.back.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Document
public class User {

    public User(UUID userId, String email, String firstName, String secondName) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    @Id
    @Setter(AccessLevel.NONE)
    private final UUID userId;

    private String email;

    private String firstName;

    private String secondName;

    private List<Location> locations = new ArrayList<>();

}
