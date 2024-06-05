package com.example.prometei.services;

import com.example.prometei.dto.HeatMap.AirplaneSeats;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.services.baseServices.TicketService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    private final TicketService ticketService;
    public StatisticService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    private List<AirplaneSeats.SeatOccupancy> getAllPercent(AirplaneModel airplaneModel) {
        List<Ticket> tickets = ticketService.getAll();

        Map<String, Long> seatPurchaseCount = tickets.stream()
                .filter(ticket -> ticket.getFlight().getAirplaneModel() == airplaneModel &&
                        ticket.getPurchase() != null &&
                        ticket.getAdditionalFavors()
                                .stream()
                                .anyMatch(favor -> Objects.equals(favor.getFlightFavor().getName(), "Выбор места в салоне") ||
                                        Objects.equals(favor.getFlightFavor().getName(), "Выбор места у окна") ||
                                        Objects.equals(favor.getFlightFavor().getName(), "Выбор места с увеличенным пространством для ног")))
                .collect(Collectors.groupingBy(Ticket::getSeatNumber, Collectors.counting()));

        return getSeatOccupancies(seatPurchaseCount);
    }

    private List<AirplaneSeats.SeatOccupancy> getPercentByUser(AirplaneModel airplaneModel,
                                                              Long userId) {
        List<Ticket> tickets = ticketService.getAll();

        Map<String, Long> seatPurchaseCount = tickets.stream()
                .filter(ticket -> ticket.getUser() != null && ticket.getUser().getId() == userId &&
                                  ticket.getFlight().getAirplaneModel() == airplaneModel &&
                                  ticket.getAdditionalFavors()
                                        .stream()
                                        .anyMatch(favor -> Objects.equals(favor.getFlightFavor().getName(), "Выбор места в салоне") ||
                                                           Objects.equals(favor.getFlightFavor().getName(), "Выбор места у окна") ||
                                                           Objects.equals(favor.getFlightFavor().getName(), "Выбор места с увеличенным пространством для ног")))
                .collect(Collectors.groupingBy(Ticket::getSeatNumber, Collectors.counting()));

        return getSeatOccupancies(seatPurchaseCount);
    }

    private List<AirplaneSeats.SeatOccupancy> getSeatOccupancies(Map<String, Long> seatPurchaseCount) {
        long totalCount = seatPurchaseCount.values().stream()
                .mapToLong(value -> value).sum();

        Map<String, Double> seatPurchasePercentage = seatPurchaseCount.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ((double) entry.getValue() / (double) totalCount)
                ));

        return seatPurchasePercentage.entrySet().stream()
                .map(entry -> {
                    AirplaneSeats.SeatOccupancy seatOccupancy = new AirplaneSeats.SeatOccupancy();
                    seatOccupancy.setSeat(entry.getKey(), entry.getValue());
                    return seatOccupancy;
                })
                .collect(Collectors.toList());
    }

    public List<AirplaneSeats> getDataForHeatMap(Long userId) {
        List<AirplaneSeats> airplaneSeatsList = new ArrayList<>();

        for (AirplaneModel airplaneModel : AirplaneModel.values()) {
            AirplaneSeats airplaneSeats = new AirplaneSeats();
            airplaneSeats.setAirplane(airplaneModel.name());
            airplaneSeats.setSeats(getAllPercent(airplaneModel));
            airplaneSeats.setUserSeats(getPercentByUser(airplaneModel, userId));
            airplaneSeatsList.add(airplaneSeats);
        }

        return airplaneSeatsList;
    }
}
