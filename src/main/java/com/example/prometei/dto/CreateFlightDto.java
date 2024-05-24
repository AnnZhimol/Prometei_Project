package com.example.prometei.dto;

import com.example.prometei.models.AirplaneModel;
import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Data
@NoArgsConstructor
public class CreateFlightDto implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
    private LocalDateTime destinationTime;
    private LocalDateTime departureTime;
    private Double economyCost;
    private Double businessCost;
    private Integer airplaneNumber;
    private AirplaneModel airplaneModel;

    public Flight dtoToEntity() {
        return Flight.builder()
                .id(this.id)
                .airplaneModel(this.airplaneModel)
                .departureTime(OffsetDateTime.of(this.departureTime, ZoneOffset.ofHours(0)).toLocalDateTime())
                .departurePoint(this.departurePoint)
                .economyCost(this.economyCost)
                .businessCost(this.businessCost)
                .airplaneNumber(this.airplaneNumber)
                .destinationTime(OffsetDateTime.of(this.destinationTime, ZoneOffset.ofHours(0)).toLocalDateTime())
                .destinationPoint(this.destinationPoint)
                .economSeats(airplaneModel == AirplaneModel.AIRBUS320 ? 120 : 265)
                .businessSeats(airplaneModel == AirplaneModel.AIRBUS320 ? 20 : 36)
                .build();
    }
}
