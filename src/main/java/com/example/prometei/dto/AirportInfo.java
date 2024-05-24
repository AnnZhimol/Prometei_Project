package com.example.prometei.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AirportInfo implements Serializable {
    private String value;
    private String label;
    private String timezone;

    public AirportInfo(String value,String label,String timezone) {
        this.value = value;
        this.timezone = timezone;
        this.label = label;
    }
}
