package com.example.prometei.services.baseServices;

import com.example.prometei.dto.UserDtos.PassengerDto;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.repositories.PurchaseRepository;
import com.example.prometei.services.codeServices.PaymentService;
import com.example.prometei.services.TransformDataService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService implements BasicService<Purchase> {
    private final PurchaseRepository purchaseRepository;
    private final TicketService ticketService;
    private final UserService userService;
    private final TransformDataService transformDataService;
    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseService(PurchaseRepository purchaseRepository, TicketService ticketService, UserService userService, TransformDataService transformDataService, PaymentService paymentService) {
        this.purchaseRepository = purchaseRepository;
        this.ticketService = ticketService;
        this.userService = userService;
        this.transformDataService = transformDataService;
        this.paymentService = paymentService;
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
            entity.setCreateDate(LocalDateTime.now());
            purchaseRepository.save(entity);
            log.info("Purchase with id = {} successfully added", entity.getId());
        }

        log.error("Error adding purchase. Purchase = null");
        throw new NullPointerException();
    }

    @Transactional
    private List<Ticket> getTicketsAndEditPurchase(Purchase purchase,
                                                   long[] tickets) {
        List<Ticket> findTickets = new ArrayList<>();

        purchase = purchaseRepository.save(purchase);
        purchase.setCreateDate(LocalDateTime.now());

        for (long ticket : tickets) {
            Ticket ticketTemp = ticketService.getById(ticket);

            if (ticketTemp.getPurchase() == null) {
                findTickets.add(ticketTemp);
                Flight flight = ticketTemp.getFlight();

                if (ticketTemp.getTicketType() == TicketType.BUSINESS) {
                    flight.setBusinessSeats(flight.getBusinessSeats() - 1);
                } else if (ticketTemp.getTicketType() == TicketType.ECONOMIC) {
                    flight.setEconomSeats(flight.getEconomSeats() - 1);
                }
            } else {
                log.error("Error create purchase. Ticket already bought.");
                throw new IllegalArgumentException("Ticket already bought.");
            }
        }

        return findTickets;
    }

    /**
     * Создает покупку на основе переданных данных о покупке, билетах и пассажирах.
     *
     * @param purchase объект покупки
     * @param tickets массив идентификаторов билетов
     * @param user данные авторизованного или неавторизованного пользователя
     * @param passengers список данных пассажиров (может быть null)
     * @return строка с результатом создания покупки
     */
    public String createPurchase(Purchase purchase,
                               long[] tickets,
                               PassengerDto user,
                               @Nullable List<UnauthUser> passengers,
                               Boolean isAuth) {
        if (isAuth) {
            return createPurchaseByAuthUser(purchase, tickets, transformDataService.transformToUser(user), passengers);
        } else {
            return createPurchaseByUnauthUser(purchase, tickets, transformDataService.transformToUnAuthUser(user), passengers);
        }
    }

    /**
     * Создает покупку в базе данных с авторизированным пользователем и списком билетов.
     *
     * @param purchase объект покупки для добавления
     * @param tickets перечень билетов
     * @param user пользователь
     */
    @Transactional
    public String createPurchaseByAuthUser(Purchase purchase,
                               long[] tickets,
                               User user,
                               @Nullable List<UnauthUser> passengers) {
        if (purchase == null) {
            log.error("Error create purchase. Purchase = null");
            throw new NullPointerException();
        }

        if (tickets == null) {
            log.error("Error create purchase. ticketIds = null");
            throw new NullPointerException();
        }

        if (user == null) {
            log.error("Error create purchase. userEmail = null");
            throw new NullPointerException();
        }

        List<Ticket> findTickets = getTicketsAndEditPurchase(purchase, tickets);

        if (passengers != null) {
            for (UnauthUser passenger : passengers) {
                userService.add(passenger);
            }
        }

        user = userService.getByEmail(user.getEmail());
        userService.save(user);

        if (passengers != null) {
            int totalParticipants = passengers.size() + 1;
            int groupSize = findTickets.size() / totalParticipants;

            if (groupSize == 0) {
                groupSize = 1;
            }

            for (int i = 0; i < findTickets.size(); i++) {
                Ticket ticket = findTickets.get(i);

                if (i < groupSize) {
                    ticket.setUser(user);
                } else {
                    int passengerIndex = (i / groupSize) - 1;

                    if (passengerIndex < passengers.size()) {
                        UnauthUser passenger = passengers.get(passengerIndex);
                        ticket.setUnauthUser(passenger);
                    } else {
                        ticket.setUser(user);
                    }
                }
            }
        } else {
            for (Ticket ticket : findTickets) {
                ticket.setUser(user);
            }
        }

        addTicketsToPurchase(purchase.getId(), findTickets);
        addUserToPurchase(purchase.getId(), userService.getByEmail(user.getEmail()));

        purchaseRepository.save(purchase);
        paymentService.createPayment(purchase);
        log.info("Purchase with id = {} successfully saved", purchase.getId());
        return purchase.getPayment().getHash();
    }

    /**
     * Создает покупку в базе данных с неавторизированным пользователем и списком билетов.
     *
     * @param purchase объект покупки для добавления
     * @param tickets перечень билетов
     * @param unauthUser пользователь
     */
    @Transactional
    public String createPurchaseByUnauthUser(Purchase purchase,
                                           long[] tickets,
                                           UnauthUser unauthUser,
                                           @Nullable List<UnauthUser> passengers) {
        if (purchase == null) {
            log.error("Error create purchase. Purchase = null");
            throw new NullPointerException();
        }

        if (tickets == null) {
            log.error("Error create purchase. ticketIds = null");
            throw new NullPointerException();
        }

        if (unauthUser == null) {
            log.error("Error create purchase. authUser = null");
            throw new NullPointerException();
        }

        List<Ticket> findTickets = getTicketsAndEditPurchase(purchase, tickets);

        userService.add(unauthUser);
        if (passengers != null) {
            for (UnauthUser passenger : passengers) {
                userService.add(passenger);
            }
        }

        if (passengers != null) {
            int totalParticipants = passengers.size() + 1;
            int groupSize = findTickets.size() / totalParticipants;

            if (groupSize == 0) {
                groupSize = 1;
            }

            for (int i = 0; i < findTickets.size(); i++) {
                Ticket ticket = findTickets.get(i);

                if (i < groupSize) {
                    ticket.setUnauthUser(unauthUser);
                } else {
                    int passengerIndex = (i / groupSize) - 1;

                    if (passengerIndex < passengers.size()) {
                        UnauthUser passenger = passengers.get(passengerIndex);
                        ticket.setUnauthUser(passenger);
                    } else {
                        ticket.setUnauthUser(unauthUser);
                    }
                }
            }
        } else {
            for (Ticket ticket : findTickets) {
                ticket.setUnauthUser(unauthUser);
            }
        }

        addTicketsToPurchase(purchase.getId(), findTickets);
        addUserToPurchase(purchase.getId(), unauthUser);

        purchaseRepository.save(purchase);
        paymentService.createPayment(purchase);
        log.info("Purchase with id = {} successfully saved", purchase.getId());
        return purchase.getPayment().getHash();
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

        userService.edit(user.getId(), user);
        purchaseRepository.save(purchase);

        log.info("Adding user to the purchase with id = {} was completed successfully", purchase.getId());
    }

    /**
     * Добавляет неавторизированного пользователя к покупке.
     *
     * @param id идентификатор покупки, к которой необходимо добавить пользователя
     * @param user пользователь, который добавляется к покупке
     * @throws NullPointerException если покупка или пользователь равны null
     */
    @Transactional
    public void addUserToPurchase(Long id, UnauthUser user) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);

        if (purchase == null) {
            log.error("Adding user to the purchase failed. Purchase == null");
            throw new NullPointerException();
        }

        if (user == null) {
            log.error("Adding user to the purchase failed. User == null");
            throw new NullPointerException();
        }

        purchase.setUnauthUser(user);

        purchaseRepository.save(purchase);

        log.info("Adding user to the purchase with id = {} was completed successfully", purchase.getId());
    }
}
