package com.example.prometei.dto.FlightDtos;

import com.example.prometei.models.Flight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.example.prometei.utils.CipherUtil.encryptId;

@Data
@NoArgsConstructor
public class FlightClientViewDto {
    private String id;
    private String departurePoint;
    private String destinationPoint;
    private String destinationDate;
    private String destinationTime;
    private String departureDate;
    private String departureTime;
    private Double economyCost;
    private Double businessCost;
    private Integer flightTime;

    private String DateParser(LocalDate localDate) {
        String dayOfMonth = String.valueOf(localDate.getDayOfMonth());
        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru"));
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ru"));

        return String.format("%s %s, %s", dayOfMonth, month, dayOfWeek);
    }

    private String TimeParser(LocalTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return localTime.format(formatter);
    }

    private Integer FlightTimeParser(Double duration) {
        return (int) Math.round(duration * 60);
    }

    public FlightClientViewDto(Flight flight) {
        id = encryptId(flight.getId());
        this.departurePoint = flight.getDeparturePoint();
        this.destinationPoint = flight.getDestinationPoint();
        this.departureDate = DateParser(flight.getDepartureDate());
        this.destinationDate = DateParser(flight.getDestinationDate());
        this.destinationTime = TimeParser(flight.getDestinationTime());
        this.departureTime = TimeParser(flight.getDepartureTime());
        this.flightTime = FlightTimeParser(flight.getFlightTime());
        this.economyCost = flight.getEconomyCost();
        this.businessCost = flight.getBusinessCost();
    }
}
