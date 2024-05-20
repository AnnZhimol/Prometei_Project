package com.example.prometei.controllers;

import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import com.example.prometei.services.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService){
        this.purchaseService = purchaseService;
    }

    @GetMapping("/get")
    public ResponseEntity<Purchase> getPurchase(@RequestParam Long id) {
        Purchase purchase = purchaseService.getById(id);
        return purchase == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(purchase, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        return new ResponseEntity<>(purchaseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<Purchase>> getPurchasesByUser(@RequestBody User user) {
        return new ResponseEntity<>(purchaseService.getPurchasesByUser(user), HttpStatus.OK);
    }

    @PostMapping("/create")
    public void addPurchase(@RequestBody Purchase purchase) {
        purchaseService.add(purchase);
    }

    @PostMapping("/addTickets")
    public void addTickets(@RequestParam Long id,
                           @RequestBody List<Ticket> tickets) {
        purchaseService.addTicketsToPurchase(id, tickets);
    }

    @PostMapping("/addUser")
    public void addUser(@RequestParam Long id,
                        @RequestBody User user) {
        purchaseService.addUserToPurchase(id, user);
    }

    @PatchMapping("/edit")
    public void editPurchase(@RequestParam Long id,
                             @RequestBody Purchase purchase) {
        purchaseService.edit(id, purchase);
    }

    @DeleteMapping("/delete")
    public void deletePurchase(@RequestBody Purchase purchase) {
        purchaseService.delete(purchase);
    }
}
