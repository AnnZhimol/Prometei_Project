package com.example.prometei.api.response;

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
public class GeneticSearch implements Serializable {
    private List<FlightFromGenetic> to;
    private List<FlightFromGenetic> back;
}
