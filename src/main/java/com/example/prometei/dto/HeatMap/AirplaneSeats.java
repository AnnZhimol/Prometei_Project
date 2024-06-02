package com.example.prometei.dto.HeatMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AirplaneSeats {
    private String airplane;
    private List<SeatOccupancy> seats;
    private List<SeatOccupancy> userSeats;

    public static class SeatOccupancy {
        private Map<String, Double> seatsMap = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Double> getSeatsMap() {
            return seatsMap;
        }

        @JsonAnySetter
        public void setSeat(String seatNumber, Double occupancyRate) {
            seatsMap.put(seatNumber, occupancyRate);
        }
    }
}
