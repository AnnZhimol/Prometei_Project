package com.example.prometei.dto.Statistic;

import com.example.prometei.models.enums.TicketType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AgeTicketDto {
    private Map<AgeCategory, StatByGender> categories = new HashMap<>();

    @Data
    public static class StatByGender {
        private List<TicketStats> male;
        private List<TicketStats> female;
    }

    @Data
    public static class TicketStats {
        private Map<TicketType, Double> ticketTypeMap = new HashMap<>();

        @JsonAnyGetter
        public Map<TicketType, Double> getTicketTypeMap() {
            return ticketTypeMap;
        }

        @JsonAnySetter
        public void setTicketType(TicketType ticketType, Double percent) {
            ticketTypeMap.put(ticketType, percent);
        }
    }
}
