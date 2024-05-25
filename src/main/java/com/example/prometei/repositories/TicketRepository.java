package com.example.prometei.repositories;

import com.example.prometei.models.*;
import com.example.prometei.models.enums.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight.id = :id ")
    List<Ticket> findTicketsByFlight(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight.departurePoint = :departurePoint " +
            "AND t.flight.destinationPoint = :destinationPoint " +
            "AND t.flight.departureDate = :departureDate " +
            "AND t.ticketType = :ticketType")
    List<Ticket> findTicketsForSearch(String departurePoint, String destinationPoint, LocalDate departureDate, TicketType ticketType);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.user.id = :id")
    List<Ticket> findTicketsByUser(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.purchase.id = :id")
    List<Ticket> findTicketsByPurchase(Long id);
}
