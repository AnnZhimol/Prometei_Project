package com.example.prometei.repositories;

import com.example.prometei.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight = :flight ")
    List<Ticket> findTicketsByFlight(Flight flight);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight.departurePoint = :departurePoint " +
            "AND t.flight.destinationPoint = :destinationPoint " +
            "AND t.flight.departureTime = :departureTime " +
            "AND t.ticketType = :ticketType")
    List<Ticket> findTicketsForSearch(String departurePoint, String destinationPoint, OffsetDateTime departureTime, TicketType ticketType);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.user = :user")
    List<Ticket> findTicketsByUser(User user);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.purchase = :purchase")
    List<Ticket> findTicketsByPurchase(Purchase purchase);
}
