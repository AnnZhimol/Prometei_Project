package com.example.prometei.dto;

import com.example.prometei.models.Ticket;
import com.example.prometei.models.TicketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class SearchDto implements Serializable {
    private long id;
    private String departurePoint;
    private String destinationPoint;
    private OffsetDateTime destinationTime;
    private OffsetDateTime departureTime;
    private TicketType ticketType;
    private Double economyCost;
    private Double businessCost;

    public SearchDto(Ticket ticket) {
        id = ticket.getId();
        this.ticketType = ticket.getTicketType();
        this.departurePoint = ticket.getFlight().getDeparturePoint();
        this.destinationPoint = ticket.getFlight().getDestinationPoint();
        this.destinationTime = ticket.getFlight().getDestinationTime();
        this.departureTime = ticket.getFlight().getDepartureTime();
        this.economyCost = ticketType == TicketType.ECONOMIC ? ticket.getFlight().getEconomyCost() : null;
        this.businessCost = ticketType == TicketType.BUSINESS ? ticket.getFlight().getBusinessCost() : null;
    }
}
