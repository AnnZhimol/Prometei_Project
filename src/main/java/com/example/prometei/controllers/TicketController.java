package com.example.prometei.controllers;

import com.example.prometei.dto.FlightFavorDto;
import com.example.prometei.dto.SearchDto;
import com.example.prometei.dto.TicketDto;
import com.example.prometei.models.*;
import com.example.prometei.services.TicketService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/get")
    public ResponseEntity<TicketDto> getTicket(@RequestParam Long id) {
        Ticket ticket = ticketService.getById(id);
        return ticket == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new TicketDto(ticket), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAll()
                                    .stream()
                                    .map(TicketDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/getByFlight")
    public ResponseEntity<List<TicketDto>> getTicketsByFlight(@RequestParam Long flightId) {
        return new ResponseEntity<>(ticketService.getTicketsByFlight(flightId)
                                    .stream()
                                    .map(TicketDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/searchResult")
    public ResponseEntity<List<SearchDto>> getSearchResult(@RequestParam String departurePoint,
                                                           @RequestParam String destinationPoint,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime departureTime,
                                                           @RequestParam TicketType ticketType) {
        return new ResponseEntity<>(ticketService.getSearchResult(departurePoint,
                destinationPoint, departureTime, ticketType).stream().map(SearchDto::new).toList(), HttpStatus.OK);
    }

    @GetMapping("/getByPurchase")
    public ResponseEntity<List<TicketDto>> getTicketsByPurchase(@RequestParam Long purchaseId) {
        return new ResponseEntity<>(ticketService.getTicketsByPurchase(purchaseId).stream().map(TicketDto::new).toList(), HttpStatus.OK);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<TicketDto>> getTicketsByUser(@RequestParam Long userId) {
        return new ResponseEntity<>(ticketService.getTicketsByUser(userId).stream().map(TicketDto::new).toList(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addTicket(@RequestBody TicketDto ticketDto) {
        ticketService.add(ticketDto.dtoToEntity());
    }

    //привязка к билету услуг (пользователь выбрал перечень услуг, на их основе создались AdditionalFavors и привязались к билету)
    @PostMapping("/addAdditionalFavors")
    public void addAdditionalFavors(@RequestParam Long id,
                                    @RequestBody List<FlightFavorDto> flightFavorsDto) {
        List<FlightFavor> listFavors = new ArrayList<>();

        for(FlightFavorDto flightFavorDto : flightFavorsDto) {
            listFavors.add(flightFavorDto.dtoToEntity());
        }

        ticketService.addAdditionalFavorsToTicket(id, ticketService.createAdditionalFavorsByFlightFavor(id, listFavors));
    }

    @PatchMapping("/edit")
    public void editTicket(@RequestParam Long id,
                           @RequestBody TicketDto ticketDto) {
        ticketService.edit(id, ticketDto.dtoToEntity());
    }

    @DeleteMapping("/delete")
    public void deleteTicket(@RequestBody TicketDto ticketDto) {
        ticketService.delete(ticketDto.dtoToEntity());
    }
}
