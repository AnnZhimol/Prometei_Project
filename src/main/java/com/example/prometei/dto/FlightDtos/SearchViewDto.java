package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SearchViewDto implements Serializable {
    private FlightClientViewDto to;
    private FlightClientViewDto back;

    public SearchViewDto(Pair<Flight, Flight> pairFlight) {
        this.to = new FlightClientViewDto(pairFlight.a);
        this.back = pairFlight.b == null ? null : new FlightClientViewDto(pairFlight.b);
    }
}
