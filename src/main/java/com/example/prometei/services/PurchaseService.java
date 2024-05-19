package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService implements BasicService<Purchase> {
    private final PurchaseRepository purchaseRepository;
    private final TicketService ticketService;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseService(PurchaseRepository purchaseRepository, TicketService ticketService, UserService userService) {
        this.purchaseRepository = purchaseRepository;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @Override
    public void add(Purchase entity) {
        if (entity != null) {
            purchaseRepository.save(entity);
            log.info("Purchase with id = {} successfully added", entity.getId());
        }
        else {
            log.error("Error adding purchase. Purchase = null");
            throw new NullPointerException();
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
            throw new NullPointerException();
        }
    }

    @Override
    public List<Purchase> getAll() {
        log.info("Get list of purchases");
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchasesByUser(User user) {
        log.info("Get list of purchases by user with id = {}", user.getId());
        return purchaseRepository.findPurchasesByUser(user);
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
            throw new EntityNotFoundException();
        }
        else {
            currentPurchase = Purchase.builder()
                    .id(currentPurchase.getId())
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
        Purchase purchase = purchaseRepository.findById(id).orElse(null);

        if (purchase != null) {
            log.info("Purchase with id = {} successfully find", id);
            return purchase;
        }
        else {
            log.error("Search purchase with id = {} failed", id);
            return null;
        }
    }

    public void addTicketsToPurchase(Purchase purchase, List<Ticket> tickets) {
        if (purchase == null) {
            log.error("Adding tickets to the purchase failed. Purchase == null");
            throw new NullPointerException();
        }
        else if (tickets == null) {
            log.error("Adding tickets to the purchase failed. Tickets == null");
            throw new NullPointerException();
        }
        else {
            purchase.setTickets(tickets);

            Double cost = 0.0;
            for (Ticket ticket : tickets) {
                ticketService.addPurchaseToTicket(ticket, purchase);

                if (ticket.getTicketType() == TicketType.BUSINESS)
                    cost+=ticket.getFlight().getBusinessCost();
                if (ticket.getTicketType() == TicketType.ECONOMIC)
                    cost+=ticket.getFlight().getEconomyCost();

                for (AdditionalFavor additionalFavor : ticket.getAdditionalFavors()) {
                    cost += additionalFavor.getFlightFavor().getCost();
                }
            }

            purchase.setTotalCost(cost);
            purchaseRepository.save(purchase);
            log.info("Adding tickets to the purchase with id = {} was completed successfully", purchase.getId());
        }
    }

    public void addUserToPurchase(Purchase purchase, User user) {
        if (purchase == null) {
            log.error("Adding user to the purchase failed. Purchase == null");
            throw new NullPointerException();
        }
        else if (user == null) {
            log.error("Adding user to the purchase failed. User == null");
            throw new NullPointerException();
        }
        else {
            purchase.setUser(user);
            for(Ticket ticket : purchase.getTickets()) {
                ticket.setUser(user);
                ticketService.edit(ticket.getId(), ticket);
            }
            user.getPurchases().add(purchase);
            userService.edit(user.getId(), user);
            purchaseRepository.save(purchase);
            log.info("Adding user to the purchase with id = {} was completed successfully", purchase.getId());
        }
    }
}
