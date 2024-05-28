package com.example.prometei.dto.PurchaseDtos;

import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.Purchase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class CreatePurchaseDto implements Serializable {
    private PaymentMethod paymentMethod;
    private String userEmail;
    private long[] ticketIds;

    public Purchase dtoToEntity() {
        return Purchase.builder()
                .paymentMethod(this.paymentMethod)
                .build();
    }
}
