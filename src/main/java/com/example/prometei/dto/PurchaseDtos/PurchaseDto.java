package com.example.prometei.dto.PurchaseDtos;

import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.Purchase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.example.prometei.utils.CipherUtil.decryptId;
import static com.example.prometei.utils.CipherUtil.encryptId;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class PurchaseDto implements Serializable {
    private String id;
    private Double totalCost;
    private PaymentMethod paymentMethod;
    private LocalDateTime createDate;
    private String userEmail;

    public PurchaseDto(Purchase purchase) {
        id = encryptId(purchase.getId());
        this.totalCost = purchase.getTotalCost();
        this.paymentMethod = purchase.getPaymentMethod();
        this.createDate = purchase.getCreateDate();
        this.userEmail = purchase.getUser() != null ? purchase.getUser().getEmail() : purchase.getUnauthUser().getEmail();
    }

    public Purchase dtoToEntity() {
        return Purchase.builder()
                .id(decryptId(this.id))
                .totalCost(this.totalCost)
                .paymentMethod(this.paymentMethod)
                .createDate(this.createDate)
                .build();
    }
}
