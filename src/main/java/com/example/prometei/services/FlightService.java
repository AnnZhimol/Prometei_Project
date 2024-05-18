package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.FlightFavorRepository;
import com.example.prometei.repositories.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightService implements BasicService<Flight> {
    private final FlightRepository flightRepository;
    private final FlightFavorRepository flightFavorRepository;
    private final TicketService ticketService;
    private final Logger log = LoggerFactory.getLogger(FlightService.class);

    public FlightService(FlightRepository flightRepository, FlightFavorRepository flightFavorRepository, TicketService ticketService) {
        this.flightRepository = flightRepository;
        this.flightFavorRepository = flightFavorRepository;
        this.ticketService = ticketService;
    }

    @Override
    public void add(Flight entity) {
        if (entity != null) {
            flightRepository.save(entity);
            log.info("Flight with id = {} successfully added", entity.getId());
        }
        else {
            log.error("Error adding flight. Flight = null");
            throw new NullPointerException();
        }
    }

    @Override
    public void delete(Flight entity) {
        if (entity != null) {
            flightRepository.delete(entity);
            log.info("Flight with id = {} successfully delete", entity.getId());
        }
        else {
            log.error("Error deleting flight. Flight = null");
            throw new NullPointerException();
        }
    }

    @Override
    public List<Flight> getAll() {
        log.info("Get list of flights");
        return flightRepository.findAll();
    }

    public List<Flight> getSearchResult(String departurePoint, String destinationPoint, OffsetDateTime departureTime) {
        log.info("Get list of sorted flights");
        return flightRepository.findFlightsByPointsAndTime(departurePoint, destinationPoint, departureTime);
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all flights");
        flightRepository.deleteAll();
    }

    @Override
    public void edit(Long id, Flight entity) {
        Flight currentFlight = getById(id);

        if (currentFlight == null) {
            log.error("Flight with id = {} not found", id);
            throw new EntityNotFoundException();
        }
        else {
            currentFlight = Flight.builder()
                    .id(currentFlight.getId())
                    .departurePoint(entity.getDeparturePoint())
                    .destinationPoint(entity.getDestinationPoint())
                    .destinationTime(entity.getDestinationTime())
                    .departureTime(entity.getDepartureTime())
                    .businessSeats(entity.getBusinessSeats())
                    .economSeats(entity.getEconomSeats())
                    .economyCost(entity.getEconomyCost())
                    .businessCost(entity.getBusinessCost())
                    .airplaneNumber(entity.getAirplaneNumber())
                    .build();

            flightRepository.save(currentFlight);
            log.info("Flight with id = {} successfully edit", id);
        }
    }

    @Override
    public Flight getById(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);

        if(flight != null) {
            log.info("Flight with id = {} successfully find", id);
            return flight;
        }
        else {
            log.error("Search flight with id = {} failed", id);
            return null;
        }
    }

    public void addFlightFavorsToFlight(Flight flight, List<FlightFavor> flightFavors) {
        if (flight == null) {
            log.error("Adding flightFavors to the flight failed. Flight == null");
            throw new NullPointerException();
        }
        else if (flightFavors == null) {
            log.error("Adding flightFavors to the flight failed. FlightFavors == null");
            throw new NullPointerException();
        }
        else {
            flight.setFlightFavors(flightFavors);

            for (FlightFavor flightFavor : flightFavors) {
                flightFavor.setFlight(flight);
                flightFavorRepository.save(flightFavor);
            }

            flightRepository.save(flight);
            log.info("Adding flightFavors to the flight with id = {} was completed successfully", flight.getId());
        }
    }

    public void addTicketsToFlight(Flight flight, List<Ticket> tickets) {
        if (flight == null) {
            log.error("Adding tickets to the flight failed. Flight == null");
            throw new NullPointerException();
        }
        else if (tickets == null) {
            log.error("Adding tickets to the flight failed. Tickets == null");
            throw new NullPointerException();
        }
        else {
            flight.setTickets(tickets);

            for (Ticket ticket : tickets) {
                ticketService.addFlightToTicket(ticket, flight);
            }

            flightRepository.save(flight);
            log.info("Adding tickets to the flight with id = {} was completed successfully", flight.getId());
        }
    }

    public void createTicketsByFlight(Flight flight) {
        if (flight == null) {
            log.error("Create tickets by the flight failed. Flight == null");
            throw new NullPointerException();
        }
        else {
            for (int i = 0; i < flight.getEconomSeats(); i++) {
                ticketService.add(Ticket.builder()
                                .ticketType(TicketType.ECONOMIC)
                                .flight(flight)
                                .build());
            }

            for (int i = 0; i < flight.getBusinessSeats(); i++) {
                ticketService.add(Ticket.builder()
                        .ticketType(TicketType.BUSINESS)
                        .flight(flight)
                        .build());
            }
        }
        log.info("Creating tickets by the flight with id = {} was completed successfully", flight.getId());
    }

    public void addAdditionalFavorsToFlightFavor(FlightFavor flightFavor, List<AdditionalFavor> additionalFavors) {
        if (flightFavor == null) {
            log.error("Adding additionalFavors to the flightFavor failed. FlightFavor == null");
            throw new NullPointerException();
        }
        else if (additionalFavors == null) {
            log.error("Adding additionalFavors to the flightFavor failed. AdditionalFavors == null");
            throw new NullPointerException();
        }
        else {
            flightFavor.setAdditionalFavors(additionalFavors);

            for (AdditionalFavor additionalFavor : additionalFavors) {
                ticketService.addFlightFavorToAdditionalFavor(additionalFavor, flightFavor);
            }

            flightFavorRepository.save(flightFavor);
            log.info("Adding additionalFavors to the flightFavor with id = {} was completed successfully", flightFavor.getId());
        }
    }
}
