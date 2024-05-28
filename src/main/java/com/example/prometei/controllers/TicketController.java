package com.example.prometei.controllers;

import com.example.prometei.dto.FavorDto.CreateFlightFavorDto;
import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.dto.TicketDtos.SearchViewDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.services.baseServices.TicketService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Получает информацию о билете по его идентификатору.
     *
     * @param ticketId идентификатор билета, закодированный в строку
     * @return ResponseEntity с информацией о билете в виде TicketDto в случае успешного получения,
     *         либо ResponseEntity с пустым телом и статусом NO_CONTENT, если билет не найден
     */
    @GetMapping("/get")
    public ResponseEntity<TicketDto> getTicket(@RequestParam String ticketId) {
        Ticket ticket = ticketService.getById(decryptId(ticketId));
        return ticket == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new TicketDto(ticket), HttpStatus.OK);
    }

    /**
     * Получает список всех билетов.
     *
     * @return ResponseEntity со списком всех билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAll()
                                    .stream()
                                    .map(TicketDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Получает список билетов по идентификатору рейса.
     *
     * @param flightId идентификатор рейса
     * @return ResponseEntity со списком билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/getByFlight")
    public ResponseEntity<List<TicketDto>> getTicketsByFlight(@RequestParam String flightId) {
        return new ResponseEntity<>(ticketService.getTicketsByFlight(decryptId(flightId))
                                    .stream()
                                    .map(TicketDto::new)
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Получает результаты поиска билетов по заданным параметрам.
     *
     * @param departurePoint пункт отправления
     * @param destinationPoint пункт назначения
     * @param departureDate дата отправления
     * @param ticketType тип билета
     * @return ResponseEntity со списком результатов поиска в виде List<SearchViewDto> и статусом OK
     */
    @GetMapping("/searchResult")
    public ResponseEntity<List<SearchViewDto>> getSearchResult(@RequestParam String departurePoint,
                                                               @RequestParam String destinationPoint,
                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                                               @RequestParam TicketType ticketType) {
        return new ResponseEntity<>(ticketService.getSearchResult(departurePoint,
                destinationPoint, departureDate, ticketType).stream().map(SearchViewDto::new).toList(), HttpStatus.OK);
    }

    /**
     * Получает список билетов по идентификатору покупки.
     *
     * @param purchaseId идентификатор покупки
     * @return ResponseEntity со списком билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/getByPurchase")
    public ResponseEntity<List<TicketDto>> getTicketsByPurchase(@RequestParam String purchaseId) {
        return new ResponseEntity<>(ticketService.getTicketsByPurchase(decryptId(purchaseId)).stream().map(TicketDto::new).toList(), HttpStatus.OK);
    }

    /**
     * Получает список билетов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return ResponseEntity со списком билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/getByUser")
    public ResponseEntity<List<TicketDto>> getTicketsByUser(@RequestParam String userId) {
        return new ResponseEntity<>(ticketService.getTicketsByUser(decryptId(userId)).stream().map(TicketDto::new).toList(), HttpStatus.OK);
    }

    /**
     * Получает список дополнительных услуг по идентификатору билета.
     *
     * @param ticketId идентификатор билета
     * @return ResponseEntity со списком дополнительных услуг в виде List<AdditionalFavorDto> и статусом OK
     */
    @GetMapping("/getAdditionalFavors")
    public ResponseEntity<List<AdditionalFavorDto>> getAdditionalFavorsByTicket(@RequestParam String ticketId) {
        return new ResponseEntity<>(ticketService.getAdditionalFavorsByTicket(decryptId(ticketId)).stream().map(AdditionalFavorDto::new).toList(), HttpStatus.OK);
    }

    @Deprecated
    @PostMapping("/create")
    public void addTicket(@RequestBody TicketDto ticketDto) {
        ticketService.add(ticketDto.dtoToEntity());
    }

     /**
     * Добавляет дополнительные услуги к билету. Старые услуги также сохраняются.
     *
     * @param ticketId идентификатор билета
     * @param createFlightFavorDtos список DTO дополнительных услуг для создания
     */
    @Transactional
    @PostMapping("/addAdditionalFavors")
    public void addAdditionalFavors(@RequestParam String ticketId,
                                    @RequestBody List<CreateFlightFavorDto> createFlightFavorDtos) {
        List<FlightFavor> listFavors = new ArrayList<>();

        for(CreateFlightFavorDto createFlightFavorDto : createFlightFavorDtos) {
            listFavors.add(createFlightFavorDto.dtoToEntity());
        }

        ticketService.addAdditionalFavorsToTicket(decryptId(ticketId), ticketService.createAdditionalFavorsByFlightFavor(decryptId(ticketId), listFavors));
    }

    @Deprecated
    @PatchMapping("/edit")
    public void editTicket(@RequestParam String ticketId,
                           @RequestBody TicketDto ticketDto) {
        ticketService.edit(decryptId(ticketId), ticketDto.dtoToEntity());
    }

    @Deprecated
    @DeleteMapping("/delete")
    public void deleteTicket(@RequestBody TicketDto ticketDto) {
        ticketService.delete(ticketDto.dtoToEntity());
    }
}
