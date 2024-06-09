package com.example.prometei.services;

import com.example.prometei.dto.Statistic.AirplaneSeats;
import com.example.prometei.dto.Statistic.AgeCategory;
import com.example.prometei.dto.Statistic.AgeTicketDto;
import com.example.prometei.dto.Statistic.PopularFavors;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.PaymentState;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.models.enums.UserGender;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.baseServices.TicketService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    private final TicketService ticketService;
    private final PurchaseService purchaseService;
    public StatisticService(TicketService ticketService, PurchaseService purchaseService) {
        this.ticketService = ticketService;
        this.purchaseService = purchaseService;
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

    private static List<AgeTicketDto.TicketStats> createTicketStatsList(Map<TicketType, Long> ticketTypeCounts) {
        long total = ticketTypeCounts.values().stream().mapToLong(Long::longValue).sum();

        return ticketTypeCounts.entrySet().stream()
                .map(entry -> {
                    AgeTicketDto.TicketStats ticketStats = new AgeTicketDto.TicketStats();
                    ticketStats.setTicketType(entry.getKey(), (double) entry.getValue() / total * 100);
                    return ticketStats;
                })
                .collect(Collectors.toList());
    }

    private AgeCategory categorizeAge(Ticket ticket) {
        LocalDate birthDate = (ticket.getUser() != null) ?
                ticket.getUser().getBirthDate() :
                ticket.getUnauthUser().getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 22 && age >= 18) {
            return AgeCategory.YOUNG;
        } else if (age >= 22 && age < 35) {
            return AgeCategory.MIDDLE_AGE_LOW;
        } else if (age >= 35 && age < 60) {
            return AgeCategory.MIDDLE_AGE_HIGH;
        } else {
            return AgeCategory.ELDERLY;
        }
    }

    public AgeTicketDto getDataForAgeMap() {
        List<Purchase> purchases = purchaseService.getAll()
                .stream().filter(x -> x.getPayment().getState() == PaymentState.PAID).toList();

        List<Ticket> tickets = purchases.stream()
                .flatMap(purchase -> purchase.getTickets().stream()).toList();

        AgeTicketDto ageTicketDto = new AgeTicketDto();

        Map<AgeCategory, Map<UserGender, Map<TicketType, Long>>> stats = tickets.stream()
                .collect(Collectors.groupingBy(
                        this::categorizeAge,
                        Collectors.groupingBy(
                                ticket -> {
                                    User user = ticket.getUser();
                                    UnauthUser unauthUser = ticket.getUnauthUser();
                                    return user != null ? user.getGender() : unauthUser.getGender();
                                },
                                Collectors.groupingBy(Ticket::getTicketType, Collectors.counting())
                        )
                ));

        stats.forEach((ageCategory, genderMap) -> {
            AgeTicketDto.StatByGender statByGender = new AgeTicketDto.StatByGender();
            statByGender.setMale(createTicketStatsList(genderMap.getOrDefault(UserGender.MALE, Collections.emptyMap())));
            statByGender.setFemale(createTicketStatsList(genderMap.getOrDefault(UserGender.FEMALE, Collections.emptyMap())));
            ageTicketDto.getCategories().put(ageCategory, statByGender);
        });

        return ageTicketDto;
    }

    public PopularFavors getPopularFavorsByMonth(Month month) {
        List<AdditionalFavor> additionalFavors = ticketService.getAllAdFavors();

        Map<String, Long> groupedFavors = additionalFavors.stream()
                .filter(favor -> {
                    Ticket ticket = favor.getTicket();
                    Purchase purchase = ticket.getPurchase();
                    return purchase != null && purchase.getCreateDate().getMonth() == month;
                })
                .collect(Collectors.groupingBy(
                        favor -> favor.getFlightFavor().getName(),
                        Collectors.counting()
                ));

        PopularFavors popularFavors = new PopularFavors();
        PopularFavors.FavorCount favorCount = new PopularFavors.FavorCount();
        groupedFavors.forEach(favorCount::setFavorCountMap);
        popularFavors.setList(Collections.singletonList(favorCount));

        return popularFavors;
    }
}
