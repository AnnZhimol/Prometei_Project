package com.example.prometei.dto.Statistic;

import lombok.Data;

@Data
public class RouteStat {
    private String route;
    private long ticketCount;

    public RouteStat(String route, long ticketCount) {
        this.route = route;
        this.ticketCount = ticketCount;
    }
}
