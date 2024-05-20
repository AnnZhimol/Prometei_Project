package com.example.prometei.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Value
public class FlightFavorDto implements Serializable {
    long id;
    String name;
    Double cost;
}