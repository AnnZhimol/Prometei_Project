package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.enums.AirplaneModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto implements Serializable {
    private String id;
    private String departurePoint;
    private String destinationPoint;
    private String destinationDate;
    private String destinationTime;
    private String departureDate;
    private String departureTime;
    private Double economyCost;
    private Double businessCost;
    private Integer flightTime;
    private AirplaneModel model;
}
