package com.example.prometei.dto.PurchaseDtos;

import com.example.prometei.dto.UserDtos.PassengerDto;
import com.example.prometei.models.enums.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class CreatePurchaseDto implements Serializable {
    private PaymentMethod paymentMethod;
    private PassengerDto user;
    private String[] tickets;
    private List<PassengerDto> passengers;
}
