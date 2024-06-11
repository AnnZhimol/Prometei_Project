package com.example.prometei.controllers;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.dto.FavorDto.FlightFavorDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.models.*;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.services.baseServices.TicketService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@CrossOrigin
@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;
    private final FlightService flightService;
    private final TransformDataService transformDataService;

    public TicketController(TicketService ticketService, FlightService flightService, TransformDataService transformDataService) {
        this.ticketService = ticketService;
        this.flightService = flightService;
        this.transformDataService = transformDataService;
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
                : new ResponseEntity<>(transformDataService.transformToTicketDto(ticket), HttpStatus.OK);
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
                                    .map(transformDataService::transformToTicketDto)
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
                                    .map(transformDataService::transformToTicketDto)
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Получает список билетов по идентификатору покупки.
     *
     * @param purchaseId идентификатор покупки
     * @return ResponseEntity со списком билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/getByPurchase")
    public ResponseEntity<List<TicketDto>> getTicketsByPurchase(@RequestParam String purchaseId) {
        return new ResponseEntity<>(ticketService.getTicketsByPurchase(decryptId(purchaseId)).stream()
                                    .map(transformDataService::transformToTicketDto)
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Получает список билетов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return ResponseEntity со списком билетов в виде List<TicketDto> и статусом OK
     */
    @GetMapping("/getByUser")
    public ResponseEntity<List<TicketDto>> getTicketsByUser(@RequestParam String userId) {
        return new ResponseEntity<>(ticketService.getTicketsByUser(decryptId(userId)).stream()
                                    .map(transformDataService::transformToTicketDto)
                                    .toList(),
                                    HttpStatus.OK);
    }

    /**
     * Получает список дополнительных услуг по идентификатору билета.
     *
     * @param ticketId идентификатор билета
     * @return ResponseEntity со списком дополнительных услуг в виде List<AdditionalFavorDto> и статусом OK
     */
    @GetMapping("/getAdditionalFavors")
    public ResponseEntity<List<AdditionalFavorDto>> getAdditionalFavorsByTicket(@RequestParam String ticketId) {
        return new ResponseEntity<>(ticketService.getAdditionalFavorsByTicket(decryptId(ticketId)).stream().map(transformDataService::transformToAdditionalFavorDto).toList(), HttpStatus.OK);
    }

     /**
     * Добавляет дополнительные услуги к билету. Старые услуги также сохраняются.
     *
     * @param ticketId идентификатор билета
     * @param flightFavorDtos список DTO дополнительных услуг для создания
     */
    @Transactional
    @PostMapping("/addAdditionalFavors")
    public void addAdditionalFavors(@RequestParam String ticketId,
                                    @RequestBody List<FlightFavorDto> flightFavorDtos) {
        List<FlightFavor> listFavors = new ArrayList<>();

        for(FlightFavorDto flightFavorDto : flightFavorDtos) {
            listFavors.add(transformDataService.transformToFlightFavor(flightFavorDto));
        }

        ticketService.addAdditionalFavorsToTicket(decryptId(ticketId), ticketService.createAdditionalFavorsByFlightFavor(decryptId(ticketId), listFavors));
    }

    /**
     * Обрабатывает запрос на возврат билета.
     *
     * @param ticketId Зашифрованный идентификатор билета, который необходимо вернуть.
     */
    @PatchMapping("/returnTicket")
    public void returnTicket(@RequestParam @NonNull String ticketId) {
        ticketService.returnTicket(decryptId(ticketId));
        flightService.updateSeatsCount(ticketService.getById(decryptId(ticketId)).getFlight().getId());
    }
}
