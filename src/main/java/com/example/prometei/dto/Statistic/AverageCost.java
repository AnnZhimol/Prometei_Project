package com.example.prometei.dto.Statistic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AverageCost {
    private Map<AgeCategory, AverageCost.StatByGender> categories = new HashMap<>();

    @Data
    public static class StatByGender {
        private List<AverageCost.PurchaseStats> male;
        private List<AverageCost.PurchaseStats> female;
    }

    @Data
    public static class PurchaseStats {
        private Map<String, Double> costMap = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Double> getCostMap() {
            return costMap;
        }

        @JsonAnySetter
        public void setCostMap(String name, Double percent) {
            costMap.put(name, percent);
        }
    }
}
