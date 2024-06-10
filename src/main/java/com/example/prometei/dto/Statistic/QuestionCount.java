package com.example.prometei.dto.Statistic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class QuestionCount {
    @JsonProperty("Negative")
    private Map<String, Long> negative;

    @JsonProperty("Positive")
    private Map<String, Long> positive;
}
