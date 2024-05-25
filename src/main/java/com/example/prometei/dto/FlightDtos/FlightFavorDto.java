package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.FlightFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@NoArgsConstructor
public class FlightFavorDto implements Serializable {
    private long id;
    private String name;
    private Double cost;

    public FlightFavorDto(FlightFavor flightFavor) {
        id = flightFavor.getId();
        this.name = flightFavor.getName();
        this.cost = flightFavor.getCost();
    }

    public FlightFavor dtoToEntity() {
        return FlightFavor.builder()
                .id(this.id)
                .name(this.name)
                .cost(this.cost)
                .build();
    }
}