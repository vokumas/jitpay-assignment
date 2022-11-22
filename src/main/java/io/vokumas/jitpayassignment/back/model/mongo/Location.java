package io.vokumas.jitpayassignment.back.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class Location {

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdOn;

}
