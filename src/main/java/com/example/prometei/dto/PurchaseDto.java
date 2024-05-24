package com.example.prometei.dto;

import com.example.prometei.models.PaymentMethod;
import com.example.prometei.models.Purchase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class PurchaseDto implements Serializable {
    private long id;
    private Double totalCost;
    private PaymentMethod paymentMethod;
    private LocalDate createDate;
    private String userEmail;

    public PurchaseDto(Purchase purchase) {
        id = purchase.getId();
        this.totalCost = purchase.getTotalCost();
        this.paymentMethod = purchase.getPaymentMethod();
        this.createDate = purchase.getCreateDate();
        this.userEmail = purchase.getUser().getEmail();
    }

    public Purchase dtoToEntity() {
        return Purchase.builder()
                .id(this.id)
                .totalCost(this.totalCost)
                .paymentMethod(this.paymentMethod)
                .createDate(this.createDate)
                .build();
    }
}
