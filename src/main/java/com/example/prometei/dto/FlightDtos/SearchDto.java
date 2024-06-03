package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchDto implements Serializable {
    private List<FlightDto> to;
    private List<FlightDto> back;

    public SearchDto(Pair<List<Flight>, List<Flight>> pairFlight) {
        this.to = pairFlight.a.stream().map(FlightDto::new).toList();
        this.back = pairFlight.b == null ? null : pairFlight.b.stream().map(FlightDto::new).toList();
    }
}
