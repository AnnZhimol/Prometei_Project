package com.example.prometei.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Value
public class FlightDto implements Serializable {
    long id;
    String departurePoint;
    String destinationPoint;
    OffsetDateTime destinationTime;
    OffsetDateTime departureTime;
    Integer economSeats;
    Integer businessSeats;
    Double economyCost;
    Double businessCost;
    Integer airplaneNumber;
}