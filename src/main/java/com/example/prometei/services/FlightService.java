package com.example.prometei.services;

import com.example.prometei.dto.FlightDtos.AirportInfo;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.repositories.FlightFavorRepository;
import com.example.prometei.repositories.FlightRepository;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FlightService implements BasicService<Flight> {
    private final FlightRepository flightRepository;
    private final FlightFavorRepository flightFavorRepository;
    private final TicketService ticketService;
    private final AirportInfo[] airportInfoList;
    private final Logger log = LoggerFactory.getLogger(FlightService.class);

    public FlightService(FlightRepository flightRepository, FlightFavorRepository flightFavorRepository, TicketService ticketService) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader("airports.json"));

        this.flightRepository = flightRepository;
        this.flightFavorRepository = flightFavorRepository;
        this.ticketService = ticketService;
        this.airportInfoList = new Gson().fromJson(reader, AirportInfo[].class);
    }

    /**
     * Получает список всех аэропортов из файла.
     *
     * @return список всех аэропортов
     */
    public AirportInfo[] getAllAirports(){
        log.info("Get list of airports");
        return airportInfoList;
    }

    public Double getDistance(Pair<Double,Double> coordinatesB, Pair<Double,Double> coordinatesD) {
        Pair<Double, Double> coordinatesA = new Pair<>(coordinatesD.a, coordinatesB.b);

        double meridianDistDegree = 111.1;

        double AB = meridianDistDegree * Math.abs(coordinatesA.a - coordinatesB.a);

        double degreeD = Math.abs(Math.cos(coordinatesD.a * Math.PI / 180.0));
        double degreeB = Math.abs(Math.cos(coordinatesB.a * Math.PI / 180.0));

        Double parallelDistDegreeBC = 111.3 * degreeB;
        Double parallelDistDegreeAD = 111.3 * degreeD;

        Double parallelDegree = Math.abs(coordinatesD.b - coordinatesB.b);

        Double BC = parallelDistDegreeBC * parallelDegree;
        Double AD = parallelDistDegreeAD * parallelDegree;

        Double cathetusSmall = 0.5 * Math.abs(AD - BC);
        double BH = Math.sqrt(Math.pow(AB, 2) - Math.pow(cathetusSmall, 2));

        double cathetusLarge = BC > AD ? BC -cathetusSmall : AD - cathetusSmall;

        return Math.sqrt(Math.pow(BH, 2) + Math.pow(cathetusLarge, 2));
    }

    /**
     * Добавляет новую сущность рейса в базу данных.
     *
     * @param entity сущность рейса, которую необходимо добавить
     * @throws NullPointerException если указанная сущность равна null
     */
    @Transactional
    @Override
    public void add(Flight entity) {
        if (entity == null) {
            log.error("Error adding flight. Flight = null");
            throw new NullPointerException();
        }

        setPointsAndTimes(entity);

        flightRepository.save(entity);
        createTicketsByFlight(entity);
        log.info("Flight with id = {} successfully added", entity.getId());
    }

    private void setPointsAndTimes(Flight entity){
        AirportInfo departureAirport = null;
        AirportInfo destinationAirport = null;
        LocalDateTime departure = entity.getDepartureDate().atTime(entity.getDepartureTime());

        for (AirportInfo airportInfo : airportInfoList) {
            if (airportInfo.getLabel().contains(entity.getDeparturePoint())) {
                entity.setDeparturePoint(
                        airportInfo.getLabel()
                );

                entity.setDepartureTime(departure
                        .plusHours(Integer.parseInt(
                                airportInfo.getTimezone()
                        )).toLocalTime()
                );

                entity.setDepartureDate(departure
                        .plusHours(Integer.parseInt(
                                airportInfo.getTimezone()
                        )).toLocalDate());

                departureAirport = airportInfo;
            } else if (airportInfo.getLabel().contains(entity.getDestinationPoint())) {
                entity.setDestinationPoint(
                        airportInfo.getLabel()
                );

                destinationAirport = airportInfo;
            }
        }

        if (destinationAirport == null || departureAirport == null) {
            log.error("Error search Airport. Airport = null");
            throw new NullPointerException();
        }

        if (destinationAirport == departureAirport) {
            log.error("Airports can not be equals!");
            throw new IllegalArgumentException();
        }

        entity.setDistance(getDistance(new Pair<>(departureAirport.getLatitude(), departureAirport.getLongitude()),new Pair<>(destinationAirport.getLatitude(), destinationAirport.getLongitude())));
        entity.setFlightTime(entity.getDistance() / 450.0);

        entity.setDestinationTime(departure
                .plusMinutes(Math.round(entity.getFlightTime() * 60))
                .plusHours(Integer.parseInt(
                        destinationAirport.getTimezone()
                ))
                .toLocalTime()
        );

        entity.setDestinationDate(departure
                .plusMinutes(Math.round(entity.getFlightTime() * 60))
                .plusHours(Integer.parseInt(
                        destinationAirport.getTimezone()
                )).toLocalDate());

        log.info("Search Airport complete. Airport was found.");
    }

    /**
     * Удаляет сущность рейса из базы данных.
     *
     * @param entity сущность рейса, которую необходимо удалить
     * @throws NullPointerException если указанная сущность равна null
     */
    @Override
    public void delete(Flight entity) {
        if (entity == null) {
            log.error("Error deleting flight. Flight = null");
            throw new NullPointerException();
        }

        flightRepository.delete(entity);
        log.info("Flight with id = {} successfully delete", entity.getId());
    }

    /**
     * Получает список всех рейсов из базы данных.
     *
     * @return список всех рейсов
     */
    @Override
    public List<Flight> getAll() {
        log.info("Get list of flights");
        return flightRepository.findAll();
    }

    @Deprecated
    public List<Flight> getSearchResult(String departurePoint,
                                        String destinationPoint,
                                        OffsetDateTime departureTime) {
        log.info("Get list of sorted flights");
        return flightRepository.findFlightsByPointsAndTime(departurePoint, destinationPoint, departureTime);
    }

    public List<FlightFavor> getFlightFavors(Long id) {
        log.info("Get list of sorted Flight Favors");
        return flightFavorRepository.findFlightFavorsByFlight(id);
    }

    /**
     * Удаляет все рейсы из базы данных.
     */
    @Override
    public void deleteAll() {
        log.info("Deleting all flights");
        flightRepository.deleteAll();
    }

    /**
     * Редактирует информацию о рейсе по его идентификатору.
     *
     * @param id идентификатор рейса, который требуется отредактировать
     * @param entity новая информация о рейсе
     * @throws EntityNotFoundException если рейс с указанным идентификатором не найден
     */
    @Transactional
    @Override
    public void edit(Long id, Flight entity) {
        Flight currentFlight = getById(id);

        if (currentFlight == null) {
            log.error("Flight with id = {} not found", id);
            throw new EntityNotFoundException();
        }

        setPointsAndTimes(entity);

        entity.setId(id);

        if (entity.getEconomSeats() == null || entity.getBusinessSeats() == null) {
            log.error("Can not edit flight");
            throw new NullPointerException();
        }

        if ((!Objects.equals(currentFlight.getBusinessSeats(), entity.getBusinessSeats())
                || !Objects.equals(currentFlight.getEconomSeats(), entity.getEconomSeats()))) {
            for (Ticket ticket : ticketService.getTicketsByFlight(id)) {
                ticketService.delete(ticket);
            }
        }

        currentFlight.getTickets().clear();

        createTicketsByFlight(entity);
        flightRepository.save(entity);
        log.info("Flight with id = {} successfully edit", id);
    }

    /**
     * Получает информацию о рейсе по его идентификатору.
     *
     * @param id идентификатор рейса, который требуется найти
     * @return объект класса Flight с информацией о рейсе, или null, если рейс не найден
     */
    @Override
    public Flight getById(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);

        if(flight != null) {
            log.info("Flight with id = {} successfully find", id);
            return flight;
        }

        log.error("Search flight with id = {} failed", id);
        return null;
    }

    /**
     * Добавляет список доступных услуг к рейсу по его идентификатору.
     *
     * @param id идентификатор рейса, к которому необходимо добавить доступные услуги
     * @param flightFavors список доступных услуг, которые нужно добавить
     * @throws NullPointerException если рейс или список доступных услуг равны null
     */
    @Transactional
    public void addFlightFavorsToFlight(Long id, List<FlightFavor> flightFavors) {
        Flight flight = flightRepository.findById(id).orElse(null);

        if (flight == null) {
            log.error("Adding flightFavors to the flight failed. Flight == null");
            throw new NullPointerException();
        }

        if (flightFavors == null) {
            log.error("Adding flightFavors to the flight failed. FlightFavors == null");
            throw new NullPointerException();
        }

        flightFavorRepository.deleteAll(flightFavorRepository.findFlightFavorsByFlight(id));

        flight.setFlightFavors(flightFavors);

        for (FlightFavor flightFavor : flightFavors) {
            if (flightFavor == null) {
                log.error("Adding flightFavors to the flight with id = {} failed. FlightFavor == null", flight.getId());
                throw new NullPointerException();
            }

            flightFavor.setFlight(flight);
            flightFavorRepository.save(flightFavor);
        }

        flightRepository.save(flight);
        log.info("Adding flightFavors to the flight with id = {} was completed successfully", flight.getId());
    }

    @Deprecated
    @Transactional
    public void addTicketsToFlight(Flight flight, List<Ticket> tickets) {
        if (flight == null) {
            log.error("Adding tickets to the flight failed. Flight == null");
            throw new NullPointerException();
        }

        if (tickets == null) {
            log.error("Adding tickets to the flight failed. Tickets == null");
            throw new NullPointerException();
        }

        flight.setTickets(tickets);

        for (Ticket ticket : tickets) {
            if(ticket == null) {
                log.error("Adding tickets to the flight with id = {} failed. Ticket == null", flight.getId());
                throw new NullPointerException();
            }

            ticketService.addFlightToTicket(ticket, flight);
        }

        flightRepository.save(flight);
        log.info("Adding tickets to the flight with id = {} was completed successfully", flight.getId());
    }

    /**
     * Создает билеты для рейса на основе заданного рейса.
     *
     * @param flight рейс, для которого необходимо создать билеты
     * @throws NullPointerException если рейс равен null
     */
    @Transactional
    public void createTicketsByFlight(Flight flight) {
        if (flight == null) {
            log.error("Create tickets by the flight failed. Flight == null");
            throw new NullPointerException();
        }

        List<Ticket> listTickets = new ArrayList<>();

        if (flight.getEconomSeats() == null || flight.getBusinessSeats() == null) {
            log.error("Create tickets by the flight failed. No seats.");
            throw new NullPointerException();
        }

        createTicketsByModel(flight, listTickets);

        flight.setTickets(listTickets);
        log.info("Creating tickets by the flight with id = {} was completed successfully", flight.getId());
    }

    private void buildTicketList(List<Ticket> listTickets,
                                 TicketType ticketType,
                                 Flight flight,
                                 String num) {
        Ticket ticket = Ticket.builder()
                .ticketType(ticketType)
                .flight(flight)
                .seatNumber(num)
                .build();

        listTickets.add(ticket);
        ticketService.add(ticket);
    }

    private void createTicketsByModel(Flight flight, List<Ticket> listTickets) {
        if (flight.getAirplaneModel() == AirplaneModel.AIRBUS320) {
            for (int i = 1 + flight.getBusinessSeats() / 4; i <= flight.getEconomSeats() / 6 + flight.getBusinessSeats() / 4; i++) {
                for (char j = 'A'; j <= 'F'; j++) {
                    buildTicketList(listTickets,
                            TicketType.ECONOMIC,
                            flight,
                            Integer.toString(i) + j);
                }
            }

            for (int i = 1; i <= flight.getBusinessSeats() / 4; i++) {
                for (char j = 'A'; j <= 'F'; j++) {
                    buildTicketList(listTickets,
                            TicketType.BUSINESS,
                            flight,
                            Integer.toString(i) + j);

                    if (j == 'A' || j == 'D') {
                        j++;
                    }
                }
            }
        }
        else if (flight.getAirplaneModel() == AirplaneModel.AIRBUS330) {
            for (int i = 5 + flight.getBusinessSeats() / 6; i <= 6 + flight.getEconomSeats() / 8 + flight.getBusinessSeats() / 6; i++) {
                if (i == 28) {
                    for (char j = 'D'; j <= 'G'; j++) {
                        buildTicketList(listTickets,
                                TicketType.ECONOMIC,
                                flight,
                                Integer.toString(i) + j);
                    }
                    continue;
                }

                if (i >= 41 && i <= 43) {
                    for (char j = 'A'; j <= 'K'; j++) {
                        buildTicketList(listTickets,
                                TicketType.ECONOMIC,
                                flight,
                                Integer.toString(i) + j);

                        if (j == 'A' || j == 'E') {
                            j++;
                        }
                        else if (j == 'H') {
                            j+=2;
                        }
                    }
                    continue;
                }

                if (i == 44) {
                    for (char j = 'A'; j <= 'G'; j++) {
                        buildTicketList(listTickets,
                                TicketType.ECONOMIC,
                                flight,
                                Integer.toString(i) + j);

                        if (j == 'A' || j == 'E') {
                            j++;
                        }
                    }
                    continue;
                }

                if (i == 45) {
                    for (char j = 'D'; j <= 'G'; j++) {
                        buildTicketList(listTickets,
                                TicketType.ECONOMIC,
                                flight,
                                Integer.toString(i) + j);

                        if (j == 'E') {
                            j++;
                        }
                    }
                    continue;
                }

                for (char j = 'A'; j <= 'K'; j++) {
                    buildTicketList(listTickets,
                            TicketType.ECONOMIC,
                            flight,
                            Integer.toString(i) + j);

                    if (j == 'A') {
                        j++;
                    }
                    else if (j == 'H') {
                        j+=2;
                    }
                }
            }

            for (int i = 1; i <= flight.getBusinessSeats() / 6; i++) {
                for (char j = 'A'; j <= 'K'; j++) {
                    buildTicketList(listTickets,
                            TicketType.BUSINESS,
                            flight,
                            Integer.toString(i) + j);

                    if (j == 'A') {
                        j++;
                    }
                    else if (j == 'D' || j == 'H') {
                        j+=2;
                    }
                }
            }
        }
    }

    @Deprecated
    public void addAdditionalFavorsToFlightFavor(FlightFavor flightFavor, List<AdditionalFavor> additionalFavors) {
        if (flightFavor == null) {
            log.error("Adding additionalFavors to the flightFavor failed. FlightFavor == null");
            throw new NullPointerException();
        }

        if (additionalFavors == null) {
            log.error("Adding additionalFavors to the flightFavor failed. AdditionalFavors == null");
            throw new NullPointerException();
        }

        flightFavor.setAdditionalFavors(additionalFavors);

        for (AdditionalFavor additionalFavor : additionalFavors) {
            ticketService.addFlightFavorToAdditionalFavor(additionalFavor, flightFavor);
        }

        flightFavorRepository.save(flightFavor);
        log.info("Adding additionalFavors to the flightFavor with id = {} was completed successfully", flightFavor.getId());
    }
}
