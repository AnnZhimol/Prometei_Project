package com.example.prometei.repositories;

import com.example.prometei.models.Flight;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t " +
            "WHERE t.flight = :flight")
    List<Ticket> findTicketsByFlight(Flight flight);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.user = :user")
    List<Ticket> findTicketsByUser(User user);

    @Query("SELECT t FROM Ticket t " +
            "WHERE t.purchase = :purchase")
    List<Ticket> findTicketsByPurchase(Purchase purchase);
}
