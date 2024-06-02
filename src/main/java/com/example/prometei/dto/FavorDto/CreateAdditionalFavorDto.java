package com.example.prometei.dto.FavorDto;

import com.example.prometei.models.FlightFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.prometei.utils.CipherUtil.decryptId;

/**
 * DTO for {@link com.example.prometei.models.FlightFavor}
 */
@Data
@NoArgsConstructor
public class CreateAdditionalFavorDto {
    private String id;
    private String name;
    private Double cost;

    public FlightFavor dtoToEntity() {
        return FlightFavor.builder()
                .id(decryptId(this.id))
                .name(this.name)
                .cost(this.cost)
                .build();
    }
}
