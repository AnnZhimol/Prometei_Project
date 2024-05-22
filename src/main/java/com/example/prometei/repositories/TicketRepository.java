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
            "WHERE t.flight.id = :id ")
    List<Ticket> findTicketsByFlight(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight.departurePoint = :departurePoint " +
            "AND t.flight.destinationPoint = :destinationPoint " +
            "AND t.flight.departureTime = :departureTime " +
            "AND t.ticketType = :ticketType")
    List<Ticket> findTicketsForSearch(String departurePoint, String destinationPoint, OffsetDateTime departureTime, TicketType ticketType);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.user.id = :id")
    List<Ticket> findTicketsByUser(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.purchase.id = :id")
    List<Ticket> findTicketsByPurchase(Long id);
}
