package com.example.prometei.dto.TicketDtos;

import com.example.prometei.models.AdditionalFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.AdditionalFavor}
 */
@Data
@NoArgsConstructor
public class AdditionalFavorDto  implements Serializable {
    private long id;
    private String name;
    private Double cost;
    private String seatNum;
    private Long ticketId;

    public AdditionalFavorDto(AdditionalFavor additionalFavor) {
        id = additionalFavor.getId();
        this.name = additionalFavor.getFlightFavor().getName();
        this.cost = additionalFavor.getFlightFavor().getCost();
        this.seatNum = additionalFavor.getTicket().getSeatNumber();
        this.ticketId = additionalFavor.getTicket().getId();
    }
}