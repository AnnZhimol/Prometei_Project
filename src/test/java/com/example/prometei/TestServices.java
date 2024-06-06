package com.example.prometei;

import com.example.prometei.models.*;
import com.example.prometei.models.enums.PaymentMethod;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.models.enums.UserGender;
import com.example.prometei.services.StatisticService;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.baseServices.TicketService;
import com.example.prometei.services.baseServices.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestServices {

    @Test
    void contextLoads() {

    }

    @Autowired
    TicketService ticketService;

    @Autowired
    FlightService flightService;

    @Autowired
    PurchaseService purchaseService;

    @Autowired
    UserService userService;

    @Autowired
    StatisticService statisticService;

    @Test
    void createUser() {
        User user = User.builder()
                .birthDate(LocalDate.of(1995,6,25))
                .gender(UserGender.MALE)
                .email("aaa@mu.ru")
                .firstName("Petr")
                .lastName("Kolohov")
                .phoneNumber("9839298565389")
                .residenceCity("Moscow")
                .password("kfjijfdghtde")
                .passport("9999 999999")
                .internationalPassportNum("8374783")
                .internationalPassportDate(LocalDate.of(2024,1,15))
                .build();

        userService.add(user);
    }

    @Test
    void getData() {
        statisticService.getDataForAgeMap();
    }

    @Test
    void editUser(){
        User user = User.builder()
                .birthDate(LocalDate.of(1945,6,25))
                .gender(UserGender.MALE)
                .email("aarrra@mu.ru")
                .firstName("Petr")
                .lastName("Kolohov")
                .phoneNumber("983929856389")
                .residenceCity("Moscow")
                .password("апиетрьтгониавк434")
                .passport("5559 999382")
                .internationalPassportNum("8374784443")
                .internationalPassportDate(LocalDate.of(2022,1,15))
                .build();

        userService.edit(1L, user);
    }

    @Test
    void createPurchase() {
        Purchase purchase = Purchase.builder()
                .createDate(LocalDate.now().atStartOfDay())
                .paymentMethod(PaymentMethod.BANKCARD)
                .totalCost(0.0)
                .build();

        purchaseService.add(purchase);
    }

    @Test
    void editPurchase(){
        Purchase purchase = Purchase.builder()
                .createDate(LocalDate.now().atStartOfDay())
                .paymentMethod(PaymentMethod.CASH)
                .totalCost(2445.0)
                .build();

        purchaseService.edit(52L, purchase);
    }

    @Test
    void addTicketsToUser(){
        List<Ticket> ticketList = new ArrayList<>();

        ticketList.add(Ticket.builder()
                .ticketType(TicketType.BUSINESS)
                .build());
        ticketList.add(Ticket.builder()
                .ticketType(TicketType.ECONOMIC)
                .build());

        userService.addTicketsToUser(userService.getById(152L),ticketList);
    }

    @Test
    void addPurchaseToTicket(){
        List<Ticket> tickets = new ArrayList<>();

        tickets.add(ticketService.getById(102L));
        tickets.add(ticketService.getById(103L));

        purchaseService.addTicketsToPurchase(52L,tickets);
    }

    @Test
    void addTicketsToFlight(){
        List<Ticket> ticketList = new ArrayList<>();

        ticketList.add(ticketService.getById(102L));
        ticketList.add(ticketService.getById(103L));

        flightService.addTicketsToFlight(flightService.getById(302L),ticketList);
    }

    @Test
    void addFlightFavorsToFlight(){
        List<FlightFavor> flightFavors = new ArrayList<>();

        flightFavors.add(FlightFavor.builder()
                                    .name("Обед 1")
                                    .cost(2399.45)
                                    .build());
        flightFavors.add(null);
        flightFavors.add(FlightFavor.builder()
                                    .name("Обед 2")
                                    .cost(2399.45)
                                    .build());

        flightService.addFlightFavorsToFlight(2L,flightFavors);
    }
}
