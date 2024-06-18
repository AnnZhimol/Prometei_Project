package com.example.prometei.dto.TicketDtos;

import com.example.prometei.models.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.Ticket}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto implements Serializable {
    private String id;
    private TicketType ticketType;
    private String seatNumber;
    private String flightId;
    private Double costFlight;
    private Double costFavors;
    private Boolean isEmpty;
    private Boolean canReturn;
}