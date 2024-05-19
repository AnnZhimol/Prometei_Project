package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.AdditionalFavorRepository;
import com.example.prometei.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService implements BasicService<Ticket> {
    private final TicketRepository ticketRepository;
    private final AdditionalFavorRepository additionalFavorRepository;
    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    public TicketService(TicketRepository ticketRepository, AdditionalFavorRepository additionalFavorRepository){
        this.ticketRepository = ticketRepository;
        this.additionalFavorRepository = additionalFavorRepository;
    }

    @Override
    public void add(Ticket entity) {
        if (entity != null) {
            ticketRepository.save(entity);
            log.info("Ticket with id = {} successfully added", entity.getId());
        }
        else {
            log.error("Error adding ticket. Ticket = null");
            throw new NullPointerException();
        }
    }

    @Override
    public void delete(Ticket entity) {
        if (entity != null) {
            ticketRepository.delete(entity);
            log.info("Ticket with id = {} successfully delete", entity.getId());
        }
        else {
            log.error("Error deleting ticket. Ticket = null");
            throw new NullPointerException();
        }
    }

    @Override
    public List<Ticket> getAll() {
        log.info("Get list of tickets");
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByFlight(Flight flight) {
        log.info("Get list of sorted tickets by flight with id = {}", flight.getId());
        return ticketRepository.findTicketsByFlight(flight);
    }

    public List<Ticket> getTicketsByUser(User user) {
        log.info("Get list of sorted tickets by user with id = {}", user.getId());
        return ticketRepository.findTicketsByUser(user);
    }

    public List<Ticket> getTicketsByPurchase(Purchase purchase) {
        log.info("Get list of sorted tickets purchase with id = {}", purchase.getId());
        return ticketRepository.findTicketsByPurchase(purchase);
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all tickets");
        ticketRepository.deleteAll();
    }

    @Override
    public void edit(Long id, Ticket entity) {
        Ticket currentTicket = getById(id);

        if (currentTicket == null) {
            log.error("Ticket with id = {} not found", id);
            throw new EntityNotFoundException();
        }
        else {
            currentTicket = Ticket.builder()
                    .id(currentTicket.getId())
                    .ticketType(entity.getTicketType())
                    .build();
            ticketRepository.save(currentTicket);
            log.info("Ticket with id = {} successfully edit", id);
        }
    }

    @Override
    public Ticket getById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if (ticket != null) {
            log.info("Ticket with id = {} successfully find", id);
            return ticket;
        }
        else {
            log.error("Search ticket with id = {} failed", id);
            return null;
        }
    }

    public void addFlightToTicket(Ticket ticket, Flight flight) {
        if (flight == null) {
            log.error("Adding flight to the ticket failed. Flight == null");
            throw new NullPointerException();
        }
        else if (ticket == null) {
            log.error("Adding flight to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }
        else {
            ticket.setFlight(flight);
            ticketRepository.save(ticket);
            log.info("Adding flight to the ticket with id = {} was completed successfully", ticket.getId());
        }
    }

    public void addPurchaseToTicket(Ticket ticket, Purchase purchase) {
        if (purchase == null) {
            log.error("Adding purchase to the ticket failed. Purchase == null");
            throw new NullPointerException();
        }
        else if (ticket == null) {
            log.error("Adding purchase to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }
        else {
            ticket.setPurchase(purchase);
            ticketRepository.save(ticket);
            log.info("Adding purchase to the ticket with id = {} was completed successfully", ticket.getId());
        }
    }

    public void addUserToTicket(Ticket ticket, User user) {
        if (ticket == null) {
            log.error("Adding user to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }
        else if (user == null) {
            log.error("Adding user to the ticket failed. User == null");
            throw new NullPointerException();
        }
        else {
            ticket.setUser(user);
            ticketRepository.save(ticket);
            log.info("Adding user to the ticket with id = {} was completed successfully", ticket.getId());
        }
    }

    public void addFlightFavorToAdditionalFavor(AdditionalFavor additionalFavor, FlightFavor flightFavor) {
        if (additionalFavor == null) {
            log.error("Adding flightFavor to the additionalFavor failed. AdditionalFavor == null");
            throw new NullPointerException();
        }
        else if (flightFavor == null) {
            log.error("Adding flightFavor to the additionalFavor failed. FlightFavor == null");
            throw new NullPointerException();
        }
        else {
            additionalFavor.setFlightFavor(flightFavor);
            additionalFavorRepository.save(additionalFavor);
            log.info("Adding flightFavor to the additionalFavor with id = {} was completed successfully", additionalFavor.getId());
        }
    }

    public List<AdditionalFavor> createAdditionalFavorsByFlightFavor(List<FlightFavor> flightFavors) {
        if (flightFavors == null) {
            log.error("Creating additionalFavor by flightFavor failed. FlightFavors == null");
            throw new NullPointerException();
        }
        else {
            List<AdditionalFavor> additionalFavorList= new ArrayList<>();
            for (FlightFavor flightFavor : flightFavors) {
                AdditionalFavor additionalFavor = AdditionalFavor.builder()
                        .flightFavor(flightFavor)
                        .build();
                additionalFavorList.add(additionalFavor);
                flightFavor.getAdditionalFavors().add(additionalFavor);
                additionalFavorRepository.save(additionalFavor);
            }

            log.info("Creating additionalFavors by the flightFavors was completed successfully");
            return additionalFavorList;
        }
    }

    public void addAdditionalFavorsToTicket(Ticket ticket, List<AdditionalFavor> additionalFavors) {
        if (ticket == null) {
            log.error("Adding additionalFavors to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }
        else if (additionalFavors == null) {
            log.error("Adding additionalFavors to the ticket failed. AdditionalFavors == null");
            throw new NullPointerException();
        }
        else {
            ticket.setAdditionalFavors(additionalFavors);

            for (AdditionalFavor additionalFavor : additionalFavors) {
                additionalFavor.setTicket(ticket);
                additionalFavorRepository.save(additionalFavor);
            }

            ticketRepository.save(ticket);
            log.info("Adding additionalFavors to the ticket with id = {} was completed successfully", ticket.getId());
        }
    }
}
