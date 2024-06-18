package com.example.prometei.dto.Statistic;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AverageCost {
    private Map<AgeCategory, AverageCost.StatByGender> categories = new HashMap<>();

    @Data
    public static class StatByGender {
        private Double male;
        private Double female;
    }
}
