package com.example.prometei.dto.PurchaseDtos;

import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.enums.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDto implements Serializable {
    private String id;
    private Double totalCost;
    private PaymentMethod paymentMethod;
    private LocalDateTime createDate;
    private String userEmail;
    private PaymentState state;
}
