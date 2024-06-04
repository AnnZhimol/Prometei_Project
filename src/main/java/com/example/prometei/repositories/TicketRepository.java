package com.example.prometei.repositories;

import com.example.prometei.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight.id = :id ")
    List<Ticket> findTicketsByFlight(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.user.id = :id")
    List<Ticket> findTicketsByUser(Long id);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.purchase.id = :id")
    List<Ticket> findTicketsByPurchase(Long id);
}
