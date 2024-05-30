package com.example.prometei.dto.PurchaseDtos;

import com.example.prometei.dto.UserDtos.UnAuthUserDto;
import com.example.prometei.dto.UserDtos.UserPurchaseDto;
import com.example.prometei.models.UnauthUser;
import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.Purchase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

/**
 * DTO for {@link com.example.prometei.models.Purchase}
 */
@Data
@NoArgsConstructor
public class CreatePurchaseDto implements Serializable {
    private PaymentMethod paymentMethod;
    private UserPurchaseDto userPurchaseDto;
    private UnAuthUserDto unauthUser;
    private String[] ticketIds;
    private List<UnAuthUserDto> unauthUsers;

    public long[] decryptTicketIds() {
        List<Long> ids = new ArrayList<>();

        for (String id : this.getTicketIds()) {
            ids.add(decryptId(id));
        }

        long[] result = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ids.get(i);
        }

        return result;
    }

    public List<UnauthUser> listDtoToEntity() {
        List<UnauthUser> list = new ArrayList<>();

        for (UnAuthUserDto dto : this.getUnauthUsers()) {
            list.add(dto.dtoToEntity());
        }

        return list;
    }

    public Purchase dtoToEntity() {
        return Purchase.builder()
                .paymentMethod(this.paymentMethod)
                .build();
    }
}
