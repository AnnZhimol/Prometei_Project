package com.example.prometei.dto.FlightDtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AirportInfo implements Serializable {
    private String value;
    private String label;
    private String timezone;
    private Double latitude;
    private Double longitude;
}
