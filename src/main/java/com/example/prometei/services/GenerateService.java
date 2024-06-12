package com.example.prometei.services;

import com.example.prometei.dto.FlightDtos.CreateFlightDto;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.AirplaneModel;
import com.example.prometei.models.enums.FavorType;
import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.enums.UserGender;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.baseServices.TicketService;
import com.example.prometei.services.baseServices.UserService;
import com.example.prometei.services.codeServices.PaymentService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GenerateService {
    private final FlightService flightService;
    private final TicketService ticketService;
    private final UserService userService;
    private final PurchaseService purchaseService;
    private final Random random;
    private final AuthenticationService authenticationService;
    private final TransformDataService transformDataService;
    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(FlightService.class);
    private static final Faker faker = new Faker(new Locale("ru"));

    public GenerateService(FlightService flightService, TicketService ticketService, UserService userService, PurchaseService purchaseService, AuthenticationService authenticationService, TransformDataService transformDataService, PaymentService paymentService){
        this.flightService = flightService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.purchaseService = purchaseService;
        this.authenticationService = authenticationService;
        this.transformDataService = transformDataService;
        this.paymentService = paymentService;
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
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .password(password)
                .passwordConfirm(password)
                .build();

        LocalDate startDate = LocalDate.of(1950, 1, 1);
        LocalDate endDate = LocalDate.of(2005, 12, 31);

        Date randomDate = faker.date().between(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        if (userService.getByEmail(user.getEmail()) == null) {
            authenticationService.signUp(user);
            User userTemp = userService.getByEmail(user.getEmail());
            userService.edit(userTemp.getId(), User.builder()
                    .gender(faker.options().option(UserGender.class))
                    .firstName(user.getFirstName())
                    .lastName(userTemp.getLastName())
                    .birthDate(randomDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .passport(faker.idNumber().valid())
                    .build());
        }
    }

    @Transactional
    public void generateUnAuthUser() {
        LocalDate startDate = LocalDate.of(1950, 1, 1);
        LocalDate endDate = LocalDate.of(2005, 12, 31);

        Date randomDate = faker.date().between(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        UnauthUser user = UnauthUser.builder()
                .email(faker.internet().emailAddress())
                .gender(faker.options().option(UserGender.class))
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .birthDate(randomDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .passport(faker.idNumber().valid())
                .build();

        userService.add(user);
    }

    @Transactional
    public void generatePurchase() {
        List<User> users = userService.getAll();
        List<UnauthUser> unauthUsers = userService.getAllUnauthUser();
        List<Ticket> tickets = ticketService.getAll();
        List<Ticket> availableTickets = tickets.stream()
                .filter(ticket -> ticket.getPurchase() == null).toList();

        while (availableTickets.size() > 0) {
            List<UnauthUser> passengers = new ArrayList<>();

            int numPassengers = random.nextInt(4) + 1;
            for (int i = 0; i < numPassengers; i++) {
                passengers.add(unauthUsers.get(random.nextInt(unauthUsers.size())));
            }

            availableTickets = tickets.stream()
                    .filter(ticket -> ticket.getPurchase() == null).toList();

            int totalTicketsNeeded = (numPassengers + 1) * (random.nextInt(3) + 1);

            if (availableTickets.size() < totalTicketsNeeded) {
                log.error("Not enough available tickets to generate a purchase. Available tickets: {}", availableTickets.size());
                return;
            }

            List<Ticket> selectedTickets = availableTickets.subList(0, totalTicketsNeeded);
            List<Long> ticketIds = selectedTickets.stream().map(Ticket::getId).toList();

            Purchase purchase = Purchase.builder()
                    .paymentMethod(faker.options().option(PaymentMethod.class))
                    .build();

            if (random.nextBoolean()) {
                User randomUser = users.get(random.nextInt(users.size()));
                String hash = purchaseService.createPurchaseByAuthUser(purchase,
                        ticketIds.stream().mapToLong(Long::longValue).toArray(),
                        randomUser,
                        passengers);
                if (random.nextBoolean()) {
                    paymentService.payPayment(hash);
                } else {
                    if (random.nextBoolean()) {
                        paymentService.cancelPayment(hash);
                    } else {
                        log.info("Processing.");
                    }
                }
            } else {
                UnauthUser randomUnAuthUser = unauthUsers.get(random.nextInt(unauthUsers.size()));

                if (passengers.contains(randomUnAuthUser)) {
                    log.error("Random unauthUser cannot be one of the passengers");
                    return;
                }

                String hash = purchaseService.createPurchaseByUnauthUser(purchase,
                        ticketIds.stream().mapToLong(Long::longValue).toArray(),
                        randomUnAuthUser,
                        passengers);

                if (random.nextBoolean()) {
                    paymentService.payPayment(hash);
                } else {
                    if (random.nextBoolean()) {
                        paymentService.cancelPayment(hash);
                    } else {
                        log.info("Processing.");
                    }
                }
            }
        }
    }

    public void generateAdditionalFavor() {
        List<Ticket> tickets = ticketService.getAll();

        for (Ticket ticket : tickets) {
            List<FlightFavor> flightFavors = ticket.getFlight().getFlightFavors();
            List<FlightFavor> selectedFavors = new ArrayList<>();
            Set<String> names = new HashSet<>();

            for (FlightFavor flightFavor : flightFavors) {
                boolean condition = flightFavor.getName().contains("Выбор места в салоне") || flightFavor.getName().contains("Выбор места у окна") || flightFavor.getName().contains("Выбор места с увеличенным пространством для ног");

                if (random.nextBoolean()) {
                    if (!((names.contains("Выбор места в салоне") && condition) ||
                            (names.contains("Интернет на борту (1 час)") && flightFavor.getName().contains("Интернет на борту (весь полет)")) ||
                            (names.contains("Интернет на борту (весь полет)") && (flightFavor.getName().contains("Интернет на борту (весь полет)") || flightFavor.getName().contains("Интернет на борту (1 час)"))) ||
                            (names.contains("Приоритетная посадка") && flightFavor.getName().contains("Приоритетная посадка")) ||
                            (names.contains("Выбор места у окна") && condition) ||
                            (names.contains("Выбор места с увеличенным пространством для ног") && condition) ||
                            (names.contains("Возврат билета") && flightFavor.getName().contains("Возврат билета")))) {
                        selectedFavors.add(flightFavor);
                        names.add(flightFavor.getName());
                    }
                }
            }

            if (!selectedFavors.isEmpty()) {
                List<AdditionalFavor> additionalFavors = ticketService.createAdditionalFavorsByFlightFavor(ticket.getId(), selectedFavors);
                ticketService.addAdditionalFavorsToTicket(ticket.getId(), additionalFavors);
                log.info("Successfully added additional favors to the ticket with id {}", ticket.getId());
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
        LocalDate departureDate = LocalDate.now().plusDays(random.nextInt(30));

        Flight randomFlight = transformDataService.transformToFlight(CreateFlightDto.builder()
                .departurePoint(departure.getLabel())
                .destinationPoint(destination.getLabel())
                .departureTime(departureTime)
                .departureDate(departureDate)
                .airplaneNumber(random.nextInt(9999))
                .airplaneModel(random.nextBoolean() ? AirplaneModel.AIRBUS320 : AirplaneModel.AIRBUS330)
                .build());

        flightService.add(randomFlight);
    }

    public void generateRandomFlightFavors() {
        List<Favor> favors = flightService.getAllFavors().stream()
                .filter(x -> x.getFavorType() == FavorType.NON_REQUIRED)
                .toList();

        List<Flight> flights = flightService.getAll().stream()
                .filter(flight -> flight.getFlightFavors().size() == 0)
                .toList();

        if (flights.isEmpty()) {
            log.error("No flights available for adding flight favors.");
            return;
        }

        for (Flight flight : flights) {
            List<FlightFavor> flightFavors = new ArrayList<>();
            Set<String> addedFavors = new HashSet<>();

            for (int i = 0; i < 5; i++) {
                Favor randomFavor = favors.get(random.nextInt(favors.size()));

                if (addedFavors.contains(randomFavor.getName())) {
                    continue;
                }

                flightFavors.add(FlightFavor.builder()
                        .name(randomFavor.getName())
                        .cost(randomFavor.getCost())
                        .flight(flight)
                        .build());
                addedFavors.add(randomFavor.getName());
            }

            flightService.addFlightFavorsToFlight(flight.getId(), flightFavors);
        }

        log.info("Added flight favors to all flights successfully.");
    }
}
