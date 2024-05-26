package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.*;

import static com.example.prometei.utils.CipherUtil.encryptId;

/**
 * DTO for {@link com.example.prometei.models.Flight}
 */
@Data
@NoArgsConstructor
public class FlightDto implements Serializable {
    private String id;
    private String departurePoint;
    private String destinationPoint;
    private LocalTime destinationTime;
    private LocalTime departureTime;
    private LocalDate departureDate;
    private LocalDate destinationDate;
    private Double economyCost;
    private Double businessCost;
    private Integer airplaneNumber;
    private AirplaneModel airplaneModel;
    private Double distance;
    private Double flightTime;

    public FlightDto(Flight flight) {
        id = encryptId(flight.getId());
        this.airplaneModel = flight.getAirplaneModel();
        this.departurePoint = flight.getDeparturePoint();
        this.destinationPoint = flight.getDestinationPoint();
        this.destinationTime = flight.getDestinationTime();
        this.departureTime = flight.getDepartureTime();
        this.departureDate = flight.getDepartureDate();
        this.destinationDate = flight.getDestinationDate();
        this.businessCost = flight.getBusinessCost();
        this.economyCost = flight.getEconomyCost();
        this.airplaneNumber = flight.getAirplaneNumber();
        this.distance = flight.getDistance();
        this.flightTime = flight.getFlightTime();
    }
}