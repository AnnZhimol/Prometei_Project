package com.example.prometei.dto.TicketDtos;

import com.example.prometei.models.AdditionalFavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.example.prometei.utils.CipherUtil.encryptId;

/**
 * DTO for {@link com.example.prometei.models.AdditionalFavor}
 */
@Data
@NoArgsConstructor
public class AdditionalFavorDto  implements Serializable {
    private String id;
    private String name;
    private Double cost;
    private String seatNum;
    private Long ticketId;

    public AdditionalFavorDto(AdditionalFavor additionalFavor) {
        id = encryptId(additionalFavor.getId());
        this.name = additionalFavor.getFlightFavor().getName();
        this.cost = additionalFavor.getFlightFavor().getCost();
        this.seatNum = additionalFavor.getTicket().getSeatNumber();
        this.ticketId = additionalFavor.getTicket().getId();
    }
}