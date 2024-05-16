package com.example.prometei.services;

import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import com.example.prometei.repositories.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService implements BasicService<Purchase> {
    PurchaseRepository purchaseRepository;
    TicketService ticketService;
    Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseService(PurchaseRepository purchaseRepository, TicketService ticketService) {
        this.purchaseRepository = purchaseRepository;
        this.ticketService = ticketService;
    }

    @Override
    public void add(Purchase entity) {
        if (entity != null) {
            purchaseRepository.save(entity);
            log.info("Purchase with id = {} successfully added", entity.getId());
        }
        else {
            log.error("Error adding purchase. Purchase = null");
        }
    }

    @Override
    public void delete(Purchase entity) {
        if (entity != null) {
            purchaseRepository.delete(entity);
            log.info("Purchase with id = {} successfully delete", entity.getId());
        }
        else {
            log.error("Error deleting purchase. Purchase = null");
        }
    }

    @Override
    public List<Purchase> getAll() {
        log.info("Get list of purchases");
        return purchaseRepository.findAll();
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all purchases");
        purchaseRepository.deleteAll();
    }

    @Override
    public void edit(Long id, Purchase entity) {
        Purchase currentPurchase = getById(id);

        if (currentPurchase == null) {
            log.error("Purchase with id = {} not found", id);
        }
        else {
            currentPurchase = Purchase.builder()
                    .totalCost(entity.getTotalCost())
                    .paymentMethod(entity.getPaymentMethod())
                    .createDate(entity.getCreateDate())
                    .build();

            purchaseRepository.save(currentPurchase);
            log.info("Purchase with id = {} successfully edit", id);
        }
    }

    @Override
    public Purchase getById(Long id) {
        try {
            Purchase purchase = purchaseRepository.getReferenceById(id);
            log.info("Purchase with id = {} successfully find", id);
            return purchase;
        }
        catch (EntityNotFoundException ex) {
            log.error("Search purchase with id = {} failed", id);
            return null;
        }
    }

    public void addTicketsToPurchase(Purchase purchase, List<Ticket> tickets) {
        if (purchase == null) {
            log.error("Adding tickets to the purchase failed. Purchase == null");
        }
        else if (tickets == null) {
            log.error("Adding tickets to the purchase failed. Tickets == null");
        }
        else {
            purchase.setTickets(tickets);

            for (Ticket ticket : tickets) {
                ticketService.addPurchaseToTicket(ticket, purchase);
            }

            purchaseRepository.save(purchase);
            log.info("Adding tickets to the purchase with id = {} was completed successfully", purchase.getId());
        }
    }

    public void addUserToPurchase(Purchase purchase, User user) {
        if (purchase == null) {
            log.error("Adding user to the purchase failed. Purchase == null");
        }
        else if (user == null) {
            log.error("Adding user to the purchase failed. User == null");
        }
        else {
            purchase.setUser(user);
            purchaseRepository.save(purchase);
            log.info("Adding user to the purchase with id = {} was completed successfully", purchase.getId());
        }
    }
}
