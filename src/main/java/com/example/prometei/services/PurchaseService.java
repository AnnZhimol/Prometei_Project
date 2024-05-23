package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    /**
     * Добавляет покупку в базу данных.
     *
     * @param entity объект покупки для добавления
     * @throws NullPointerException если переданный объект покупки равен null
     */
    @Override
    public void add(Purchase entity) {
        if (entity != null) {
            entity.setCreateDate(LocalDate.now());
            purchaseRepository.save(entity);
            log.info("Purchase with id = {} successfully added", entity.getId());
        }

        log.error("Error adding purchase. Purchase = null");
        throw new NullPointerException();
    }

    /**
     * Создает покупку в базе данных с пользователем и списком билетов.
     *
     * @param purchase объект покупки для добавления
     * @param ticketIds перечень билетов
     * @param userEmail пользователь
     */
    @Transactional
    public void createPurchase(Purchase purchase, long[] ticketIds, String userEmail) {
        if (purchase == null) {
            log.error("Error create purchase. Purchase = null");
            throw new NullPointerException();
        }
        if (ticketIds == null) {
            log.error("Error create purchase. ticketIds = null");
            throw new NullPointerException();
        }
        if (userEmail == null) {
            log.error("Error create purchase. userEmail = null");
            throw new NullPointerException();
        }

        List<Ticket> tickets = new ArrayList<>();
        purchase = purchaseRepository.save(purchase);
        purchase.setCreateDate(LocalDate.now());

        for(long id : ticketIds) {
            if (ticketService.getById(id).getPurchase() == null) {
                tickets.add(ticketService.getById(id));
            }
            else {
                log.error("Error create purchase. Ticket already buyed.");
                throw new IllegalArgumentException();
            }
        }

        addTicketsToPurchase(purchase.getId(), tickets);
        addUserToPurchase(purchase.getId(), userService.getByEmail(userEmail));
        purchaseRepository.save(purchase);
        log.info("Purchase with id = {} successfully saved", purchase.getId());
    }

    /**
     * Удаляет покупку из базы данных.
     *
     * @param entity объект покупки для удаления
     * @throws NullPointerException если переданный объект покупки равен null
     */
    @Override
    public void delete(Purchase entity) {
        if (entity == null) {
            log.error("Error deleting purchase. Purchase = null");
            throw new NullPointerException();
        }

        purchaseRepository.delete(entity);
        log.info("Purchase with id = {} successfully delete", entity.getId());
    }

    /**
     * Получает список всех покупок.
     *
     * @return список всех покупок
     */
    @Override
    public List<Purchase> getAll() {
        log.info("Get list of purchases");
        return purchaseRepository.findAll();
    }

    /**
     * Получает список покупок по пользователю.
     *
     * @param id объект пользователя
     * @return список покупок пользователя
     */
    public List<Purchase> getPurchasesByUser(Long id) {
        log.info("Get list of purchases by user with id = {}", id);
        return purchaseRepository.findPurchasesByUser(id);
    }

    /**
     * Удаляет все покупки из базы данных.
     */
    @Override
    public void deleteAll() {
        log.info("Deleting all purchases");
        purchaseRepository.deleteAll();
    }

    /**
     * Редактирует существующую покупку в базе данных.
     *
     * @param id идентификатор покупки, которую необходимо отредактировать
     * @param entity объект покупки с обновленными данными
     * @throws EntityNotFoundException если покупка с указанным id не найдена
     */
    @Override
    public void edit(Long id, Purchase entity) {
        Purchase currentPurchase = getById(id);

        if (currentPurchase == null) {
            log.error("Purchase with id = {} not found", id);
            throw new EntityNotFoundException();
        }

        entity.setId(id);

        purchaseRepository.save(currentPurchase);
        log.info("Purchase with id = {} successfully edit", id);
    }

    /**
     * Получает покупку по идентификатору.
     *
     * @param id идентификатор покупки для поиска
     * @return найденный объект покупки или null, если покупка не найдена
     */
    @Override
    public Purchase getById(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);

        if (purchase != null) {
            log.info("Purchase with id = {} successfully find", id);
            return purchase;
        }

        log.error("Search purchase with id = {} failed", id);
        return null;
    }

    /**
     * Добавляет билеты к существующей покупке в базе данных. Считает итоговую сумму покупки
     *
     * @param id идентификатор покупки, к которой необходимо добавить билеты
     * @param tickets список билетов для добавления
     * @throws NullPointerException если покупка или список билетов равны null
     */
    @Transactional
    public void addTicketsToPurchase(Long id, List<Ticket> tickets) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);

        if (purchase == null) {
            log.error("Adding tickets to the purchase failed. Purchase == null");
            throw new NullPointerException();
        }

        if (tickets == null) {
            log.error("Adding tickets to the purchase failed. Tickets == null");
            throw new NullPointerException();
        }

        purchase.setTickets(tickets);

        Double cost = 0.0;
        for (Ticket ticket : tickets) {
            if (ticket == null) {
                log.error("Adding tickets to the purchase failed. Ticket == null");
                throw new NullPointerException();
            }

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

    /**
     * Добавляет пользователя к покупке.
     *
     * @param id идентификатор покупки, к которой необходимо добавить пользователя
     * @param user пользователь, который добавляется к покупке
     * @throws NullPointerException если покупка или пользователь равны null
     */
    @Transactional
    public void addUserToPurchase(Long id, User user) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);

        if (purchase == null) {
            log.error("Adding user to the purchase failed. Purchase == null");
            throw new NullPointerException();
        }

        if (user == null) {
            log.error("Adding user to the purchase failed. User == null");
            throw new NullPointerException();
        }

        purchase.setUser(user);

        for(Ticket ticket : purchase.getTickets()) {
            if (ticket == null) {
                log.error("Adding user to tickets failed. Ticket == null");
                throw new NullPointerException();
            }
            ticket.setUser(user);
            ticketService.edit(ticket.getId(), ticket);
        }

        user.getPurchases().add(purchase);
        userService.edit(user.getId(), user);
        purchaseRepository.save(purchase);

        log.info("Adding user to the purchase with id = {} was completed successfully", purchase.getId());
    }
}
