package com.example.prometei.dto.Statistic;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyTicketSales {
    private LocalDate date;
    private long ticketCount;

    public DailyTicketSales(LocalDate date, long ticketCount) {
        this.date = date;
        this.ticketCount = ticketCount;
    }
}
