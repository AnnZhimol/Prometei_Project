package com.example.prometei.dto.FavorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.AdditionalFavor}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalFavorDto implements Serializable {
    private String id;
    private String name;
    private Double cost;
    private String seatNum;
    private String ticketId;
}