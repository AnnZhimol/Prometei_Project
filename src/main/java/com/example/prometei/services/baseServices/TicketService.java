package com.example.prometei.services.baseServices;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.models.*;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.repositories.AdditionalFavorRepository;
import com.example.prometei.repositories.TicketRepository;
import com.example.prometei.services.TransformDataService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService implements BasicService<Ticket> {
    private final TicketRepository ticketRepository;
    private final AdditionalFavorRepository additionalFavorRepository;
    private final TransformDataService transformDataService;
    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    public TicketService(TicketRepository ticketRepository, AdditionalFavorRepository additionalFavorRepository, TransformDataService transformDataService){
        this.ticketRepository = ticketRepository;
        this.additionalFavorRepository = additionalFavorRepository;
        this.transformDataService = transformDataService;
    }

    /**
     * Добавляет билет в базу данных.
     *
     * @param entity билет, который необходимо добавить
     * @throws NullPointerException если билет равен null
     */
    @Override
    public void add(Ticket entity) {
        if (entity == null) {
            log.error("Error adding ticket. Ticket = null");
            throw new NullPointerException();
        }

        ticketRepository.save(entity);
        log.info("Ticket with id = {} successfully added", entity.getId());
    }

    /**
     * Удаляет билет из базы данных.
     *
     * @param entity билет, который необходимо удалить
     * @throws NullPointerException если билет равен null
     */
    @Override
    public void delete(Ticket entity) {
        if (entity == null) {
            log.error("Error deleting ticket. Ticket = null");
            throw new NullPointerException();
        }

        ticketRepository.delete(entity);
        log.info("Ticket with id = {} successfully delete", entity.getId());
    }

    /**
     * Получает список всех билетов.
     *
     * @return список всех билетов
     */
    @Override
    public List<Ticket> getAll() {
        log.info("Get list of tickets");
        return ticketRepository.findAll();
    }

    /**
     * Получает отсортированный список билетов по заданному рейсу.
     *
     * @param id идентификатор рейса, по которому необходимо получить список билетов
     * @return отсортированный список билетов
     */
    public List<Ticket> getTicketsByFlight(Long id) {
        log.info("Get list of sorted tickets by flight with id = {}", id);
        return ticketRepository.findTicketsByFlight(id);
    }

    /**
     * Получает отсортированный список билетов по заданному пользователю.
     *
     * @param id идентификатор пользователя, по которому необходимо получить список билетов
     * @return отсортированный список билетов
     */
    public List<Ticket> getTicketsByUser(Long id) {
        log.info("Get list of sorted tickets by user with id = {}", id);
        return ticketRepository.findTicketsByUser(id);
    }

    /**
     * Получает отсортированный список билетов по заданной покупке.
     *
     * @param id идентификатор покупки, по которой необходимо получить список билетов
     * @return отсортированный список билетов
     */
    public List<Ticket> getTicketsByPurchase(Long id) {
        log.info("Get list of sorted tickets purchase with id = {}", id);
        return ticketRepository.findTicketsByPurchase(id);
    }

    /**
     * Удаляет все билеты из базы данных.
     */
    @Override
    public void deleteAll() {
        log.info("Deleting all tickets");
        ticketRepository.deleteAll();
    }

    /**
     * Редактирует информацию о билете по заданному идентификатору.
     *
     * @param id идентификатор билета, который необходимо отредактировать
     * @param entity информация о билете для редактирования
     * @throws EntityNotFoundException если билет с заданным id не найден
     */
    @Override
    public void edit(Long id, Ticket entity) {
        Ticket currentTicket = getById(id);

        if (currentTicket == null) {
            log.error("Ticket with id = {} not found", id);
            throw new EntityNotFoundException();
        }

        entity.setId(id);
        entity.setFlight(currentTicket.getFlight());

        ticketRepository.save(entity);
        log.info("Ticket with id = {} successfully edit", id);
    }

    /**
     * Получает информацию о билете по заданному идентификатору.
     *
     * @param id идентификатор билета, который необходимо найти
     * @return информация о билете
     */
    @Override
    public Ticket getById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if (ticket != null) {
            log.info("Ticket with id = {} successfully find", id);
            return ticket;
        }

        log.error("Search ticket with id = {} failed", id);
        return null;
    }

    /**
     * Добавляет информацию о рейсе к билету.
     *
     * @param ticket билет, к которому необходимо добавить рейс
     * @param flight рейс, который необходимо добавить
     */
    public void addFlightToTicket(Ticket ticket, Flight flight) {
        if (flight == null) {
            log.error("Adding flight to the ticket failed. Flight == null");
            throw new NullPointerException();
        }

        if (ticket == null) {
            log.error("Adding flight to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        ticket.setFlight(flight);
        ticketRepository.save(ticket);
        log.info("Adding flight to the ticket with id = {} was completed successfully", ticket.getId());
    }

    /**
     * Добавляет информацию о покупке к билету.
     *
     * @param ticket билет, к которому необходимо добавить покупку
     * @param purchase покупка, которую необходимо добавить
     */
    public void addPurchaseToTicket(Ticket ticket, Purchase purchase) {
        if (purchase == null) {
            log.error("Adding purchase to the ticket failed. Purchase == null");
            throw new NullPointerException();
        }

        if (ticket == null) {
            log.error("Adding purchase to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        ticket.setPurchase(purchase);
        ticketRepository.save(ticket);
        log.info("Adding purchase to the ticket with id = {} was completed successfully", ticket.getId());
    }

    /**
     * Добавляет информацию о пользователе к билету.
     *
     * @param ticket билет, к которому необходимо добавить пользователя
     * @param user пользователь, которого необходимо добавить
     */
    public void addUserToTicket(Ticket ticket, User user) {
        if (ticket == null) {
            log.error("Adding user to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        if (user == null) {
            log.error("Adding user to the ticket failed. User == null");
            throw new NullPointerException();
        }

        ticket.setUser(user);
        ticketRepository.save(ticket);
        log.info("Adding user to the ticket with id = {} was completed successfully", ticket.getId());
    }

    /**
     * Добавляет информацию об услуге на рейсе к выбранной услуге.
     *
     * @param additionalFavor выбранная услуга, к которой необходимо добавить услугу рейса
     * @param flightFavor услуга на рейсе, которую необходимо добавить
     */
    public void addFlightFavorToAdditionalFavor(AdditionalFavor additionalFavor, FlightFavor flightFavor) {
        if (additionalFavor == null) {
            log.error("Adding flightFavor to the additionalFavor failed. AdditionalFavor == null");
            throw new NullPointerException();
        }

        if (flightFavor == null) {
            log.error("Adding flightFavor to the additionalFavor failed. FlightFavor == null");
            throw new NullPointerException();
        }

        additionalFavor.setFlightFavor(flightFavor);
        additionalFavorRepository.save(additionalFavor);
        log.info("Adding flightFavor to the additionalFavor with id = {} was completed successfully", additionalFavor.getId());
    }

    /**
     * Возвращает билет, если соблюдены условия возврата.
     *
     * @param ticketId идентификатор билета, который необходимо вернуть
     * @throws IllegalArgumentException если условия возврата не соблюдены
     */
    @Transactional
    public void returnTicket(Long ticketId) {
        Ticket ticket = getById(ticketId);

        if (ticket == null) {
            log.error("Return the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        LocalDateTime dateNow = LocalDateTime.now();
        Duration duration =Duration.between(dateNow, ticket.getFlight().getDepartureDate().atTime(ticket.getFlight().getDepartureTime()));
        boolean favorExist = false;

        for (AdditionalFavor additionalFavor : ticket.getAdditionalFavors()) {
            if (Objects.equals(additionalFavor.getFlightFavor().getName(), "Возврат билета")) {
                favorExist = true;
                break;
            }
        }

        if (duration.toHours() >= 24 && favorExist) {
            returnTicket(ticket);
            log.info("Ticket with id = {} returned successfully", ticket.getId());
        } else {
            log.error("Ticket with id = {} returning failed. See conditions.", ticket.getId());
            throw new IllegalArgumentException();
        }
    }

    private void returnTicket(Ticket ticket) {
        Purchase purchase = ticket.getPurchase();
        double scale = Math.pow(10, 2);

        double costFavors = ticket.getAdditionalFavors()
                .stream()
                .map(transformDataService::transformToAdditionalFavorDto)
                .mapToDouble(AdditionalFavorDto::getCost)
                .sum();

        Double costFlight = ticket.getTicketType() == TicketType.BUSINESS ?
                ticket.getFlight().getBusinessCost() :
                ticket.getFlight().getEconomyCost();

        double totalCost = (Math.ceil(purchase.getTotalCost() * scale) / scale) - (Math.ceil(costFavors * scale) / scale) - (Math.ceil(costFlight * scale) / scale);
        if (totalCost < 1)
            totalCost =0.0;

        purchase.setTotalCost(totalCost);

        ticket.setUnauthUser(null);
        ticket.setUser(null);

        for (AdditionalFavor additionalFavor : ticket.getAdditionalFavors()) {
            additionalFavor.setTicket(null);
            additionalFavor.setFlightFavor(null);
            additionalFavorRepository.deleteNull();
        }
        ticket.setAdditionalFavors(Collections.emptyList());

        ticket.setPurchase(purchase);
        ticketRepository.save(ticket);

        ticket.setPurchase(null);
        ticketRepository.save(ticket);
    }

    /**
     * Осуществляет возврат билетов.
     *
     * @param tickets список билетов для возврата
     * @throws NullPointerException если список билетов равен null
     */
    @Transactional
    public void returnTickets(List<Ticket> tickets) {
        if (tickets == null) {
            log.error("Return the tickets failed. Tickets == null");
            throw new NullPointerException();
        }

        for (Ticket ticket : tickets) {
            returnTicket(ticket);
        }
    }

    /**
     * Создает список выбранных услуг на основе списка услуг рейса.
     *
     * @param id идентификатор билета
     * @param flightFavors список услуг рейса, на основе которых создаются выбранные услуги
     * @return список созданных выбранных услуг
     */
    @Transactional
    public List<AdditionalFavor> createAdditionalFavorsByFlightFavor(Long id, List<FlightFavor> flightFavors) {
        if (flightFavors == null) {
            log.error("Creating additionalFavor by flightFavor failed. FlightFavors == null");
            throw new NullPointerException();
        }

        Ticket ticket = getById(id);

        if (ticket == null) {
            log.error("Creating additionalFavor by flightFavor failed. Ticket == null");
            throw new NullPointerException();
        }

        List<AdditionalFavor> additionalFavorList = new ArrayList<>();

        for (FlightFavor flightFavor : flightFavors) {
            if (flightFavor == null) {
                log.error("Creating additionalFavor by flightFavor failed. FlightFavor == null");
                throw new NullPointerException();
            }

            if (ticket.getFlight().getFlightFavors().stream().anyMatch(x -> x.getId() == flightFavor.getId())) {
                AdditionalFavor additionalFavor = AdditionalFavor.builder()
                        .flightFavor(flightFavor)
                        .build();

                additionalFavorList.add(additionalFavor);
                additionalFavorRepository.save(additionalFavor);
            }
        }

        log.info("Creating additionalFavors by the flightFavors was completed successfully");
        return additionalFavorList;
    }

    private boolean isDuplicate(Set<String> existingFavorNames, String name) {
        boolean condition = name.contains("Выбор места в салоне") || name.contains("Выбор места у окна") || name.contains("Выбор места с увеличенным пространством для ног");
        return (existingFavorNames.contains("Выбор места в салоне") && condition) ||
                (existingFavorNames.contains("Интернет на борту (1 час)") && name.contains("Интернет на борту (весь полет)")) ||
                (existingFavorNames.contains("Интернет на борту (весь полет)") && (name.contains("Интернет на борту (весь полет)") || name.contains("Интернет на борту (1 час)") )) ||
                (existingFavorNames.contains("Приоритетная посадка") && name.contains("Приоритетная посадка")) ||
                (existingFavorNames.contains("Выбор места у окна") && condition) ||
                (existingFavorNames.contains("Выбор места с увеличенным пространством для ног") && condition) ||
                (existingFavorNames.contains("Возврат билета") && name.contains("Возврат билета"));
    }

    /**
     * Добавляет список выбранных услуг к билету по его идентификатору.
     *
     * @param id идентификатор билета, к которому добавляются выбранные услуги
     * @param additionalFavors список выбранных услуг для добавления
     */
    @Transactional
    public void addAdditionalFavorsToTicket(Long id, List<AdditionalFavor> additionalFavors) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if (ticket == null) {
            log.error("Adding additionalFavors to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        if (additionalFavors == null) {
            log.error("Adding additionalFavors to the ticket failed. AdditionalFavors == null");
            throw new NullPointerException();
        }

        List<AdditionalFavor> existingFavors = additionalFavorRepository.findAdditionalFavorsByTicket(id);
        Set<String> existingFavorNames = existingFavors.stream()
                .map(favor -> favor.getFlightFavor().getName())
                .collect(Collectors.toSet());

        Set<String> newFavorNames = new HashSet<>();

        for (AdditionalFavor additionalFavor : additionalFavors) {
            if (additionalFavor == null) {
                log.error("Adding additionalFavors to the ticket failed. AdditionalFavor == null");
                throw new NullPointerException();
            }

            String favorName = additionalFavor.getFlightFavor().getName();

            if (isDuplicate(existingFavorNames, favorName) || isDuplicate(newFavorNames, favorName)) {
                log.error("Adding additionalFavors to the ticket failed. AdditionalFavor {} duplicates an existing favor or another favor in the list.", favorName);
                throw new IllegalArgumentException("AdditionalFavor cannot duplicate existing or another favor in the list: " + favorName);
            }
            
            newFavorNames.add(favorName);
        }

        for (AdditionalFavor additionalFavor : additionalFavors) {
            additionalFavor.setTicket(ticket);
            additionalFavorRepository.save(additionalFavor);
        }

        ticket.setAdditionalFavors(additionalFavors);
        ticketRepository.save(ticket);
        log.info("Adding additionalFavors to the ticket with id = {} was completed successfully", ticket.getId());
    }

    /**
     * Возвращает список услуг, связанных с указанным билетом.
     *
     * @param id идентификатор билета
     * @return список услуг для указанного билета
     * @throws NullPointerException если билет с указанным идентификатором не найден
     */
    public List<AdditionalFavor> getAdditionalFavorsByTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if (ticket == null) {
            log.error("Adding additionalFavors to the ticket failed. Ticket == null");
            throw new NullPointerException();
        }

        return additionalFavorRepository.findAdditionalFavorsByTicket(id);
    }
}
