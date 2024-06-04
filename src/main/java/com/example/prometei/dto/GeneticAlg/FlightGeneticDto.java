package com.example.prometei.dto.GeneticAlg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
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
}
