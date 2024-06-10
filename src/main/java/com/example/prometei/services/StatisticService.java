package com.example.prometei.services;

import com.example.prometei.api.NeuralApi;
import com.example.prometei.dto.Statistic.*;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.PaymentState;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.models.enums.UserGender;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.baseServices.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final NeuralApi neuralApi;
    private final Logger log = LoggerFactory.getLogger(StatisticService.class);

    public StatisticService(TicketService ticketService, PurchaseService purchaseService, NeuralApi neuralApi) {
        this.ticketService = ticketService;
        this.purchaseService = purchaseService;
        this.neuralApi = neuralApi;
    }

    private List<AirplaneSeats.SeatOccupancy> getAllPercent(AirplaneModel airplaneModel) {
        if (airplaneModel == null) {
            log.error("Can not create heat map. AirplaneModel == null.");
            throw new NullPointerException();
        }

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

        log.info("Finding percent for heat map was complete.");
        return getSeatOccupancies(seatPurchaseCount);
    }

    private List<AirplaneSeats.SeatOccupancy> getPercentByUser(AirplaneModel airplaneModel,
                                                              Long userId) {
        if (airplaneModel == null) {
            log.error("Can not create heat map. AirplaneModel == null.");
            throw new NullPointerException();
        }

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

        log.info("Finding percent by userId = {} for heat map was complete.", userId);
        return getSeatOccupancies(seatPurchaseCount);
    }

    private List<AirplaneSeats.SeatOccupancy> getSeatOccupancies(Map<String, Long> seatPurchaseCount) {
        if (seatPurchaseCount == null) {
            log.error("Can not create heat map. SeatPurchaseCount == null.");
            throw new NullPointerException();
        }

        long totalCount = seatPurchaseCount.values().stream()
                .mapToLong(value -> value).sum();

        Map<String, Double> seatPurchasePercentage = seatPurchaseCount.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ((double) entry.getValue() / (double) totalCount)
                ));

        log.info("Getting seatOccupancies was complete.");
        return seatPurchasePercentage.entrySet().stream()
                .map(entry -> {
                    AirplaneSeats.SeatOccupancy seatOccupancy = new AirplaneSeats.SeatOccupancy();
                    seatOccupancy.setSeat(entry.getKey(), entry.getValue());
                    return seatOccupancy;
                })
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список объектов AirplaneSeats, содержащих данные для тепловой карты по моделям самолетов и пользователю.
     *
     * @param userId Идентификатор пользователя, для которого необходимо получить данные по местам.
     * @return Список объектов AirplaneSeats, содержащих информацию о процентах занятости мест и местах, занятых пользователем.
     */
    public List<AirplaneSeats> getDataForHeatMap(Long userId) {
        List<AirplaneSeats> airplaneSeatsList = new ArrayList<>();

        for (AirplaneModel airplaneModel : AirplaneModel.values()) {
            AirplaneSeats airplaneSeats = new AirplaneSeats();
            airplaneSeats.setAirplane(airplaneModel.name());
            airplaneSeats.setSeats(getAllPercent(airplaneModel));
            airplaneSeats.setUserSeats(getPercentByUser(airplaneModel, userId));
            airplaneSeatsList.add(airplaneSeats);
        }

        log.info("Getting data for heat map complete successfully.");
        return airplaneSeatsList;
    }

    /**
     * Возвращает объект QuestionCount, содержащий количество запросов, полученных от нейронной сети по каждой категории.
     *
     * @return Объект QuestionCount, содержащий данные о количестве запросов.
     */
    public QuestionCount getDataFromNeural() {
        log.info("Getting statistic about count of question complete successfully.");
        return neuralApi.getQuestionCount();
    }

    private List<AgeTicketDto.TicketStats> createTicketStatsList(Map<TicketType, Long> ticketTypeCounts) {
        if (ticketTypeCounts == null) {
            log.error("Can not create ticket statistic by age. TicketTypeCounts == null.");
            throw new NullPointerException();
        }

        long total = ticketTypeCounts.values().stream().mapToLong(Long::longValue).sum();

        log.info("Creating list for ticket stats complete.");
        return ticketTypeCounts.entrySet().stream()
                .map(entry -> {
                    AgeTicketDto.TicketStats ticketStats = new AgeTicketDto.TicketStats();
                    ticketStats.setTicketType(entry.getKey(), (double) entry.getValue() / total * 100);
                    return ticketStats;
                })
                .collect(Collectors.toList());
    }

    private AgeCategory categorizeAge(Ticket ticket) {
        if (ticket == null) {
            log.error("Can not create ticket statistic by age. Ticket == null.");
            throw new NullPointerException();
        }

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

    /**
     * Возвращает объект AgeTicketDto, содержащий статистику по билетам в зависимости от возрастной категории,
     * пола пользователя и типа билета.
     *
     * @return Объект AgeTicketDto, содержащий данные по возрастным категориям, полу пользователей и типам билетов.
     */
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

        log.info("Statistic by age, gender and ticket type complete successfully.");
        return ageTicketDto;
    }

    /**
     * Возвращает объект PopularFavors, содержащий популярные услуги за указанный месяц.
     *
     * @param month Месяц, за который необходимо получить популярные услуги.
     * @return Объект PopularFavors, содержащий список популярных услуг с их количеством.
     */
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

        log.info("Getting popular bought favors by month.");
        return popularFavors;
    }
}
