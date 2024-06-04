package com.example.prometei.controllers;

import com.example.prometei.dto.PurchaseDtos.CreatePurchaseDto;
import com.example.prometei.dto.PurchaseDtos.PurchaseDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.dto.UserDtos.EditUserDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final TransformDataService transformDataService;

    public PurchaseController(PurchaseService purchaseService, TransformDataService transformDataService){
        this.purchaseService = purchaseService;
        this.transformDataService = transformDataService;
    }

    /**
     * Получает информацию о покупке по её идентификатору.
     *
     * @param purchaseId идентификатор покупки, которую требуется получить (зашифрованный)
     * @return ResponseEntity с объектом PurchaseDto в случае успешного получения информации о покупке, либо ResponseEntity с кодом NO_CONTENT, если покупка не найдена
     */
    @GetMapping("/get")
    public ResponseEntity<PurchaseDto> getPurchase(@RequestParam String purchaseId) {
        Purchase purchase = purchaseService.getById(decryptId(purchaseId));
        return purchase == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new PurchaseDto(purchase), HttpStatus.OK);
    }

    /**
     * Получает список всех покупок.
     *
     * @return ResponseEntity со списком PurchaseDto всех покупок в случае успешного получения, иначе пустой список.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        return new ResponseEntity<>(purchaseService.getAll()
                .stream().map(PurchaseDto::new).toList(), HttpStatus.OK);
    }

    /**
     * Получает список всех покупок пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи покупки требуется получить
     * @return ResponseEntity со списком PurchaseDto всех покупок пользователя в случае успешного получения, иначе пустой список.
     */
    @GetMapping("/getByUser")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByUser(@RequestParam String userId) {
        return new ResponseEntity<>(purchaseService.getPurchasesByUser(decryptId(userId))
                .stream().map(PurchaseDto::new).toList(), HttpStatus.OK);
    }

    /**
     * Создает новую покупку на основе переданных данных (авторизированный пользователь).
     *
     * @param purchaseDto объект CreatePurchaseDto, содержащий информацию о новой покупке
     */
    @PostMapping("/createAuthUser")
    public void addPurchase(@RequestBody CreatePurchaseDto purchaseDto) {
        purchaseService.createPurchase(purchaseDto.dtoToEntity(),
                                       transformDataService.decryptTicketIds(purchaseDto.getTicketIds()),
                                       transformDataService.transformToUser(purchaseDto.getUser()),
                                       purchaseDto.getPassengers() == null ?
                                               null :
                                               transformDataService.listPassengerDtoToUnAuthUser(purchaseDto.getPassengers()));
    }

    /**
     * Создает новую покупку на основе переданных данных (неавторизированный пользователь).
     *
     * @param purchaseDto объект CreatePurchaseDto, содержащий информацию о новой покупке
     */
    @PostMapping("/createUnAuthUser")
    public void addPurchaseByUnauthUser(@RequestBody CreatePurchaseDto purchaseDto) {
                purchaseService.createPurchaseByUnauthUser(purchaseDto.dtoToEntity(),
                                                           transformDataService.decryptTicketIds(purchaseDto.getTicketIds()),
                                                           transformDataService.transformToUnAuthUser(purchaseDto.getUnauthUser()),
                                                           purchaseDto.getPassengers() == null ?
                                                                   null :
                                                                   transformDataService.listPassengerDtoToUnAuthUser(purchaseDto.getPassengers())
                                                           );
    }

    @Deprecated
    @PostMapping("/addTickets")
    public void addTickets(@RequestParam String purchaseId,
                           @RequestBody List<TicketDto> ticketDtos) {
        List<Ticket> tickets = new ArrayList<>();

        for(TicketDto ticketDto : ticketDtos) {
            tickets.add(ticketDto.dtoToEntity());
        }

        purchaseService.addTicketsToPurchase(decryptId(purchaseId), tickets);
    }

    @Deprecated
    @PostMapping("/addUser")
    public void addUser(@RequestParam String purchaseId,
                        @RequestBody EditUserDto editUserDto) {
        purchaseService.addUserToPurchase(decryptId(purchaseId), transformDataService.transformToUser(editUserDto));
    }

    @Deprecated
    @PatchMapping("/edit")
    public void editPurchase(@RequestParam String purchaseId,
                             @RequestBody PurchaseDto purchaseDto) {
        purchaseService.edit(decryptId(purchaseId), purchaseDto.dtoToEntity());
    }

    @Deprecated
    @DeleteMapping("/delete")
    public void deletePurchase(@RequestBody PurchaseDto purchaseDto) {
        purchaseService.delete(purchaseDto.dtoToEntity());
    }
}
