package com.example.prometei.dto.EmailDto;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent implements Serializable {
    private String passengerName;
    private String seatNum;
    private String ticketNumber;
    private String ticketType;
    private Double ticketTotalCost;
    private String airplaneModel;
    private String departurePoint;
    private String destinationPoint;
    private String departureTime;
    private String destinationTime;
    private List<AdditionalFavorDto> additionalFavors;
    private double totalFavorCost;
}
