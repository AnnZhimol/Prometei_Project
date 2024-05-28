package com.example.prometei.services;

import com.example.prometei.dto.FlightDtos.CreateFlightDto;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.repositories.AdditionalFavorRepository;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.baseServices.TicketService;
import com.example.prometei.services.baseServices.UserService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class GenerateService {
    private final FlightService flightService;
    private final TicketService ticketService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    private final AdditionalFavorRepository additionalFavorRepository;
    private final Random random;
    private final AuthenticationService authenticationService;
    private final Logger log = LoggerFactory.getLogger(FlightService.class);
    private static final Faker faker = new Faker(new Locale("en-US"));

    public GenerateService(FlightService flightService, TicketService ticketService, UserService userService, PurchaseService purchaseService, AdditionalFavorRepository additionalFavorRepository, AuthenticationService authenticationService){
        this.flightService = flightService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.purchaseService = purchaseService;
        this.additionalFavorRepository = additionalFavorRepository;
        this.authenticationService = authenticationService;
        this.random = new Random();
    }

    @Transactional
    public void addAllAirports(List<Airport> airports) {
        if (airports.contains(null)) {
            log.error("Adding airports failed. Element can't be null.");
            throw new NullPointerException();
        }

        log.info("Adding airports completed.");
        flightService.addAllAirports(airports);
    }

    @Transactional
    public void addAllFavors(List<Favor> favors) {
        if (favors.contains(null)) {
            log.error("Adding favors failed. Element can't be null.");
            throw new NullPointerException();
        }

        log.info("Adding favors completed.");
        flightService.addAllFavors(favors);
    }

    @Transactional
    public void generateUser() {
        String password = faker.internet().password(10, 20);
        SignUpUser user = SignUpUser.builder()
                .email(faker.internet().emailAddress())
                .password(password)
                .passwordConfirm(password)
                .build();

        if (userService.getByEmail(user.getEmail()) == null) {
            authenticationService.signUp(user);
        }
    }

    @Transactional
    public void generatePurchase() {
        List<User> users = userService.getAll();
        List<Ticket> tickets = ticketService.getAll();

        List<Ticket> availableTickets = tickets.stream()
                .filter(ticket -> ticket.getPurchase() == null).toList();

        if (availableTickets.size() < 2) {
            log.error("Not enough available tickets to generate a purchase. Available tickets: {}", availableTickets.size());
            return;
        }

        Set<Long> selectedTicketIds = new HashSet<>();
        while (selectedTicketIds.size() < 2) {
            Ticket randomTicket = availableTickets.get(random.nextInt(availableTickets.size()));
            selectedTicketIds.add(randomTicket.getId());
        }
        List<Long> ticketIds = new ArrayList<>(selectedTicketIds);

        Purchase purchase = Purchase.builder()
                .paymentMethod(faker.options().option(PaymentMethod.class))
                .build();

        User randomUser = users.get(random.nextInt(users.size()));

        purchaseService.createPurchase(purchase,
                ticketIds.stream()
                        .mapToLong(Long::longValue)
                        .toArray(),
                randomUser.getEmail());
    }

    public void generateAdditionalFavor() {
        List<Ticket> tickets = ticketService.getAll();

        for (Ticket ticket : tickets) {
            if (ticket.getAdditionalFavors().isEmpty()) {
                List<FlightFavor> flightFavors = new ArrayList<>();

                for (FlightFavor flightFavor : ticket.getFlight().getFlightFavors()) {
                    if (random.nextBoolean()) {
                        flightFavors.add(flightFavor);
                    }
                }

                List<AdditionalFavor> list = ticketService.createAdditionalFavorsByFlightFavor(ticket.getId(), flightFavors);

                if (!flightFavors.isEmpty()) {
                    try {
                        ticketService.addAdditionalFavorsToTicket(ticket.getId(), list);
                    } catch (IllegalArgumentException e) {
                        additionalFavorRepository.deleteAll(list);
                        log.error("Failed to add additional favors to the ticket with id {}. Duplicate favors detected. Error: {}", ticket.getId(), e.getMessage());
                    }
                }
            }
        }
    }

    public void generateRandomFlight() {
        List<Airport> airports = flightService.getAllAirports();

        Airport departure = airports.get(random.nextInt(airports.size()));
        Airport destination;
        do {
            destination = airports.get(random.nextInt(airports.size()));
        } while (departure.equals(destination));

        LocalTime departureTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
        LocalDate departureDate = LocalDate.now().plusDays(random.nextInt(120));

        Flight randomFlight = CreateFlightDto.builder()
                .departurePoint(departure.getLabel())
                .destinationPoint(destination.getLabel())
                .departureTime(departureTime)
                .departureDate(departureDate)
                .economyCost(1000 + (random.nextDouble() * 4000))
                .businessCost(5000 + (random.nextDouble() * 10000))
                .airplaneNumber(random.nextInt(9999))
                .airplaneModel(random.nextBoolean() ? AirplaneModel.AIRBUS320 : AirplaneModel.AIRBUS330)
                .build().dtoToEntity();

        flightService.add(randomFlight);
    }

    public void generateRandomFlightFavor() {
        List<Favor> favors = flightService.getAllFavors();
        List<FlightFavor> flightFavors = new ArrayList<>();

        outerLoop:
        for (int i = 0; i < 5; i++) {
            Favor randomFavor = favors.get(random.nextInt(favors.size()));

            for (FlightFavor flightFavor : flightFavors) {
                if (Objects.equals(flightFavor.getName(), randomFavor.getName())) {
                    break outerLoop;
                }
            }

            flightFavors.add(FlightFavor.builder()
                    .name(randomFavor.getName())
                    .cost(randomFavor.getCost())
                    .build());
        }

        flightService.addFlightFavorsToFlight(flightService.getAll().get(random.nextInt(flightService.getAll().size())).getId(), flightFavors);
    }
}
