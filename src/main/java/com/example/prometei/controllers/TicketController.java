package com.example.prometei.controllers;

import com.example.prometei.models.Flight;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import com.example.prometei.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/get")
    public ResponseEntity<Ticket> getTicket(@RequestParam Long id) {
        Ticket ticket = ticketService.getById(id);
        return ticket == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/getByFlight")
    public ResponseEntity<List<Ticket>> getTicketsByFlight(@RequestBody Flight flight) {
        return new ResponseEntity<>(ticketService.getTicketsByFlight(flight), HttpStatus.OK);
    }

    @GetMapping("/getByPurchase")
    public ResponseEntity<List<Ticket>> getTicketsByPurchase(@RequestBody Purchase purchase) {
        return new ResponseEntity<>(ticketService.getTicketsByPurchase(purchase), HttpStatus.OK);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@RequestBody User user) {
        return new ResponseEntity<>(ticketService.getTicketsByUser(user), HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addTicket(@RequestBody Ticket ticket) {
        ticketService.add(ticket);
    }

    @PatchMapping("/edit")
    public void editTicket(@RequestParam Long id,
                           @RequestBody Ticket ticket) {
        ticketService.edit(id, ticket);
    }

    @DeleteMapping("/delete")
    public void deleteTicket(@RequestBody Ticket ticket) {
        ticketService.delete(ticket);
    }
}
