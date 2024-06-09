package com.example.prometei.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightFromGenetic implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
}
