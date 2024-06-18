package com.example.prometei.dto.Statistic;

import com.example.prometei.models.enums.TicketType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AgeTicketDto {
    private Map<AgeCategory, StatByGender> categories = new HashMap<>();

    @Data
    public static class StatByGender {
        private Map<TicketType, Double> male = new HashMap<>();
        private Map<TicketType, Double> female = new HashMap<>();
    }
}
