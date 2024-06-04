package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.enums.AirplaneModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.*;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateFlightDto implements Serializable {
    private String departurePoint;
    private String destinationPoint;
    private LocalTime departureTime;
    private LocalDate departureDate;
    private Double economyCost;
    private Double businessCost;
    private Integer airplaneNumber;
    private AirplaneModel airplaneModel;
}
