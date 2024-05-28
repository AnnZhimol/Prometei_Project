package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FlightGeneticDto implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
    private LocalDateTime destinationTime;
    private LocalDateTime departureTime;
    private Double distance;
    private Integer countEconomic;
    private Integer countBusiness;

    public FlightGeneticDto(Flight flight) {
        id = flight.getId();
        this.departurePoint = flight.getDeparturePoint();
        this.destinationPoint = flight.getDestinationPoint();
        this.destinationTime = flight.getDestinationDate().atTime(flight.getDestinationTime());
        this.departureTime = flight.getDepartureDate().atTime(flight.getDepartureTime());
        this.distance = flight.getDistance();
        this.countBusiness = flight.getBusinessSeats();
        this.countEconomic = flight.getEconomSeats();
    }
}
