package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.Flight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateFlightDto {
    private String departurePoint;
    private String destinationPoint;
    private LocalTime departureTime;
    private LocalDate departureDate;
    private Double economyCost;
    private Double businessCost;
    private Integer airplaneNumber;
    private AirplaneModel airplaneModel;

    public Flight dtoToEntity() {
        LocalDateTime departure = this.departureDate.atTime(this.departureTime);
        return Flight.builder()
                .airplaneModel(this.airplaneModel)
                .departureTime(OffsetDateTime.of(departure, ZoneOffset.ofHours(0)).toLocalTime())
                .departureDate(OffsetDateTime.of(departure, ZoneOffset.ofHours(0)).toLocalDate())
                .departurePoint(this.departurePoint)
                .economyCost(this.economyCost)
                .businessCost(this.businessCost)
                .airplaneNumber(this.airplaneNumber)
                .destinationPoint(this.destinationPoint)
                .economSeats(airplaneModel == AirplaneModel.AIRBUS320 ? 120 : 265)
                .businessSeats(airplaneModel == AirplaneModel.AIRBUS320 ? 20 : 36)
                .build();
    }
}
