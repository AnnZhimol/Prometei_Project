package com.example.prometei.dto;

import com.example.prometei.models.AirplaneModel;
import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Data
@NoArgsConstructor
public class FlightDto implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
    private LocalDateTime destinationTime;
    private LocalDateTime departureTime;
    private Double economyCost;
    private Double businessCost;
    private Integer airplaneNumber;
    private AirplaneModel airplaneModel;

    public FlightDto(Flight flight) {
        id = flight.getId();
        this.airplaneModel = flight.getAirplaneModel();
        this.departurePoint = flight.getDeparturePoint();
        this.destinationPoint = flight.getDestinationPoint();
        this.destinationTime = flight.getDestinationTime();
        this.departureTime = flight.getDepartureTime();
        this.businessCost = flight.getBusinessCost();
        this.economyCost = flight.getEconomyCost();
        this.airplaneNumber = flight.getAirplaneNumber();
        flight.setEconomSeats(flight.getAirplaneModel() == AirplaneModel.AIRBUS320 ? 120 : 265);
        flight.setBusinessSeats(flight.getAirplaneModel() == AirplaneModel.AIRBUS320 ? 20 : 36);
    }
}