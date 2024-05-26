package com.example.prometei.dto.TicketDtos;

import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.TicketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.example.prometei.utils.CipherUtil.decryptId;
import static com.example.prometei.utils.CipherUtil.encryptId;

/**
 * DTO for {@link com.example.prometei.models.Ticket}
 */
@Data
@NoArgsConstructor
public class TicketDto implements Serializable {
    private String id;
    private TicketType ticketType;
    private String seatNumber;
    private long flightId;

    public TicketDto(Ticket ticket) {
        id = encryptId(ticket.getId());
        this.seatNumber = ticket.getSeatNumber();
        this.ticketType = ticket.getTicketType();
        this.flightId = ticket.getFlight().getId();
    }

    public Ticket dtoToEntity() {
        return Ticket.builder()
                .id(decryptId(this.id))
                .seatNumber(this.seatNumber)
                .ticketType(this.ticketType)
                .build();
    }
}