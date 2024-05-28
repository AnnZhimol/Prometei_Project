package com.example.prometei.dto.FavorDto;

import com.example.prometei.models.FlightFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@NoArgsConstructor
public class CreateFlightFavorDto {
    private String name;
    private Double cost;

    public FlightFavor dtoToEntity() {
        return FlightFavor.builder()
                .name(this.name)
                .cost(this.cost)
                .build();
    }
}
