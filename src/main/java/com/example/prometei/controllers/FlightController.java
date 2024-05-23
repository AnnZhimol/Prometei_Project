package com.example.prometei.controllers;

import com.example.prometei.dto.FlightDto;
import com.example.prometei.dto.FlightFavorDto;
import com.example.prometei.models.Flight;
import com.example.prometei.models.FlightFavor;
import com.example.prometei.services.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/flight")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/get")
    public ResponseEntity<FlightDto> getFlight(@RequestParam Long id) {
        Flight flight = flightService.getById(id);
        return flight == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new FlightDto(flight), HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@RequestParam String departurePoint,
                                                      @RequestParam String destinationPoint,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime departureTime) {
        return new ResponseEntity<>(flightService.getSearchResult(departurePoint,
                destinationPoint, departureTime), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        return new ResponseEntity<>(flightService.getAll()
                                    .stream()
                                    .map(FlightDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/getFlightFavors")
    public ResponseEntity<List<FlightFavorDto>> getFlightFavors(Long flightId) {
        return new ResponseEntity<>(flightService.getFlightFavors(flightId)
                .stream()
                .map(FlightFavorDto::new)
                .toList(),
                HttpStatus.OK);
    }

    // создание билетов вместе с полетом
    @PostMapping("/create")
    public void addFlight(@RequestBody FlightDto flightDto) {
        flightService.add(flightDto.dtoToEntity());
    }

    @PostMapping("/addFlightFavors")
    public void addFlightFavors(@RequestParam Long id,
                                @RequestBody List<FlightFavorDto> flightFavorsDto) {
        List<FlightFavor> listFavors = new ArrayList<>();

        for(FlightFavorDto flightFavorDto : flightFavorsDto) {
            listFavors.add(flightFavorDto.dtoToEntity());
        }

        flightService.addFlightFavorsToFlight(id, listFavors);
    }

    @PatchMapping("/edit")
    public void editFlight(@RequestParam Long id,
                           @RequestBody FlightDto flightDto) {
        flightService.edit(id, flightDto.dtoToEntity());
    }

    @DeleteMapping("/delete")
    public void deleteFlight(@RequestBody FlightDto flightDto) {
        flightService.delete(flightDto.dtoToEntity());
    }
}
