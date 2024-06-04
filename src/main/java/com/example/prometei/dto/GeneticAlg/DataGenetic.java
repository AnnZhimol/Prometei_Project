package com.example.prometei.dto.GeneticAlg;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DataGenetic {
    private String fromPoint;
    private String toPoint;
    private Integer countBusiness;
    private Integer countEconomic;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private List<FlightGeneticDto> flightsTo;
    private List<FlightGeneticDto> flightsBack;
}
