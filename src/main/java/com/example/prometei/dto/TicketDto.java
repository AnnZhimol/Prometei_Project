package com.example.prometei.dto;

import com.example.prometei.models.Ticket;
import com.example.prometei.models.TicketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.Ticket}
 */
@Data
@NoArgsConstructor
public class TicketDto implements Serializable {
    private long id;
    private TicketType ticketType;
    private long flightId;

    public TicketDto(Ticket ticket) {
        id = ticket.getId();
        this.ticketType = ticket.getTicketType();
        this.flightId = ticket.getFlight().getId();
    }

    public Ticket dtoToEntity() {
        return Ticket.builder()
                .id(this.id)
                .ticketType(this.ticketType)
                .build();
    }
}