package com.example.prometei.controllers;

import com.example.prometei.models.Flight;
import com.example.prometei.models.FlightFavor;
import com.example.prometei.services.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/flight")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/get")
    public ResponseEntity<Flight> getFlight(@RequestParam Long id) {
        Flight flight = flightService.getById(id);
        return flight == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(flight, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@RequestParam String departurePoint,
                                                      @RequestParam String destinationPoint,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime departureTime) {
        return new ResponseEntity<>(flightService.getSearchResult(departurePoint,
                destinationPoint, departureTime), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return new ResponseEntity<>(flightService.getAll(),HttpStatus.OK);
    }

    // создание билетов вместе с полетом
    @PostMapping("/create")
    public void addFlight(@RequestBody Flight flight) {
        flightService.add(flight);
        flightService.createTicketsByFlight(flight);
    }

    @PostMapping("/addFlightFavors")
    public void addFlightFavors(@RequestParam Long id,
                                @RequestBody List<FlightFavor> flightFavors) {
        flightService.addFlightFavorsToFlight(id, flightFavors);
    }

    @PatchMapping("/edit")
    public void editFlight(@RequestParam Long id,
                           @RequestBody Flight flight) {
        flightService.edit(id, flight);
    }

    @DeleteMapping("/delete")
    public void deleteFlight(@RequestBody Flight flight) {
        flightService.delete(flight);
    }
}
