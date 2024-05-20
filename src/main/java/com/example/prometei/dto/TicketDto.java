package com.example.prometei.dto;

import com.example.prometei.models.TicketType;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.Ticket}
 */
@Value
public class TicketDto implements Serializable {
    long id;
    TicketType ticketType;
    UserDto user;
}