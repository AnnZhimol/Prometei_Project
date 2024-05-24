package com.example.prometei.dto;

import com.example.prometei.models.PaymentMethod;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class CreatePurchaseDto implements Serializable {
    private long id;
    private PaymentMethod paymentMethod;
    private String userEmail;
    private long[] ticketIds;

    public CreatePurchaseDto(Purchase purchase) {
        id = purchase.getId();
        this.paymentMethod = purchase.getPaymentMethod();
        this.userEmail = purchase.getUser().getEmail();
        this.ticketIds = purchase.getTickets().stream().mapToLong(Ticket::getId).toArray();
    }

    public Purchase dtoToEntity() {
        return Purchase.builder()
                .id(this.id)
                .paymentMethod(this.paymentMethod)
                .build();
    }
}
