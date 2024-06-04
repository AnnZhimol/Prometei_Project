package com.example.prometei.dto.FavorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightFavorDto implements Serializable {
    private String id;
    private String name;
    private Double cost;
}