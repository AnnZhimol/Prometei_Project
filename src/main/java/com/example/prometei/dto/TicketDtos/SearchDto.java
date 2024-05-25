package com.example.prometei.dto.TicketDtos;

import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.TicketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@NoArgsConstructor
public class SearchDto implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
    private String destinationDate;
    private String destinationTime;
    private String departureDate;
    private String departureTime;
    private TicketType ticketType;
    private Double economyCost;
    private Double businessCost;
    private String flightTime;

    private String DateParser(LocalDate localDate) {
        String dayOfMonth = String.valueOf(localDate.getDayOfMonth());
        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru"));
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ru"));

        return String.format("%s %s, %s", dayOfMonth, month, dayOfWeek);
    }

    private String TimeParser(LocalTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return localTime.format(formatter);
    }

    private String FlightTimeParser(Double duration) {
        int hours = duration.intValue();
        int minutes = (int) ((duration - hours) * 60);

        return String.format("В пути: %dч %dм", hours, minutes);
    }

    public SearchDto(Ticket ticket) {
        id = ticket.getId();
        this.ticketType = ticket.getTicketType();
        this.departurePoint = ticket.getFlight().getDeparturePoint();
        this.destinationPoint = ticket.getFlight().getDestinationPoint();
        this.departureDate = DateParser(ticket.getFlight().getDepartureDate());
        this.destinationDate = DateParser(ticket.getFlight().getDestinationDate());
        this.destinationTime = TimeParser(ticket.getFlight().getDestinationTime());
        this.departureTime = TimeParser(ticket.getFlight().getDepartureTime());
        this.flightTime = FlightTimeParser(ticket.getFlight().getFlightTime());
        this.economyCost = ticketType == TicketType.ECONOMIC ? ticket.getFlight().getEconomyCost() : null;
        this.businessCost = ticketType == TicketType.BUSINESS ? ticket.getFlight().getBusinessCost() : null;
    }
}
