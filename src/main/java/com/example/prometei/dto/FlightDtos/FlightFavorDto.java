package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.FlightFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.example.prometei.utils.CipherUtil.encryptId;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@NoArgsConstructor
public class FlightFavorDto implements Serializable {
    private String id;
    private String name;
    private Double cost;

    public FlightFavorDto(FlightFavor flightFavor) {
        id = encryptId(flightFavor.getId());
        this.name = flightFavor.getName();
        this.cost = flightFavor.getCost();
    }

    public FlightFavor dtoToEntity() {
        return FlightFavor.builder()
                .name(this.name)
                .cost(this.cost)
                .build();
    }
}