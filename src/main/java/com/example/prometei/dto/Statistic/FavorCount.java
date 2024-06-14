package com.example.prometei.dto.Statistic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FavorCount {
    private Map<String, Long> favorCountMap = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Long> getFavorCountMap() {
        return favorCountMap;
    }

    @JsonAnySetter
    public void setFavorCountMap(String nameFavor, Long count) {
        favorCountMap.put(nameFavor, count);
    }
}

