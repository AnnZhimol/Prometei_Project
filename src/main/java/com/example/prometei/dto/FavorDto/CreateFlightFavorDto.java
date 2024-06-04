package com.example.prometei.dto.FavorDto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@NoArgsConstructor
public class CreateFlightFavorDto implements Serializable {
    private String name;
    private Double cost;
}
