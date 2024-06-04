package com.example.prometei.dto.FlightDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto implements Serializable {
    private List<FlightDto> to;
    private List<FlightDto> back;
}
