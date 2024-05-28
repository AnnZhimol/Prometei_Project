package com.example.prometei.dto.FlightDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DataGenetic {
    private String from;
    private String to;
    private Integer countBusiness;
    private Integer countEconomic;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private List<FlightGeneticDto> flights;
}
