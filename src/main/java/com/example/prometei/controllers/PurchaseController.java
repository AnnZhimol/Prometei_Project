package com.example.prometei.controllers;

import com.example.prometei.dto.PurchaseDtos.CreatePurchaseDto;
import com.example.prometei.dto.PurchaseDtos.PurchaseDto;
import com.example.prometei.dto.TicketDtos.TicketDto;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.services.baseServices.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService){
        this.purchaseService = purchaseService;
    }

    @GetMapping("/get")
    public ResponseEntity<PurchaseDto> getPurchase(@RequestParam Long id) {
        Purchase purchase = purchaseService.getById(id);
        return purchase == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(new PurchaseDto(purchase), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        return new ResponseEntity<>(purchaseService.getAll()
                .stream().map(PurchaseDto::new).toList(), HttpStatus.OK);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByUser(@RequestParam Long userId) {
        return new ResponseEntity<>(purchaseService.getPurchasesByUser(userId)
                .stream().map(PurchaseDto::new).toList(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addPurchase(@RequestBody CreatePurchaseDto purchaseDto) {
        purchaseService.createPurchase(purchaseDto.dtoToEntity(), purchaseDto.getTicketIds(), purchaseDto.getUserEmail());
    }

    @Deprecated
    @PostMapping("/addTickets")
    public void addTickets(@RequestParam Long id,
                           @RequestBody List<TicketDto> ticketDtos) {
        List<Ticket> tickets = new ArrayList<>();

        for(TicketDto ticketDto : ticketDtos) {
            tickets.add(ticketDto.dtoToEntity());
        }

        purchaseService.addTicketsToPurchase(id, tickets);
    }

    @Deprecated
    @PostMapping("/addUser")
    public void addUser(@RequestParam Long id,
                        @RequestBody UserDto userDto) {
        purchaseService.addUserToPurchase(id, userDto.dtoToEntity());
    }

    @Deprecated
    @PatchMapping("/edit")
    public void editPurchase(@RequestParam Long id,
                             @RequestBody PurchaseDto purchaseDto) {
        purchaseService.edit(id, purchaseDto.dtoToEntity());
    }

    @DeleteMapping("/delete")
    public void deletePurchase(@RequestBody PurchaseDto purchaseDto) {
        purchaseService.delete(purchaseDto.dtoToEntity());
    }
}
