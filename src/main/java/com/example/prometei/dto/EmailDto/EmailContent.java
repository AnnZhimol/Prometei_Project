package com.example.prometei.dto.EmailDto;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.TicketType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmailContent implements Serializable {
    private String passengerName;
    private String departurePoint;
    private String destinationPoint;
    private AirplaneModel airplaneModel;
    private String departureTime;
    private String destinationTime;
    private TicketType ticketType;
    private String seatNum;
    private Double totalCost;
    private List<AdditionalFavorDto> additionalFavors;
}
