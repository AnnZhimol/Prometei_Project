package com.example.prometei.services;

import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import com.example.prometei.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements BasicService<User> {
    UserRepository userRepository;
    TicketService ticketService;
    PurchaseService purchaseService;
    Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PurchaseService purchaseService, TicketService ticketService) {
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.purchaseService = purchaseService;
    }

    @Override
    public void add(User entity) {
        if (entity != null) {
            userRepository.save(entity);
            log.info("User with id = {} successfully added", entity.getId());
        }
        else {
            log.error("Error adding user. User = null");
        }
    }

    @Override
    public void delete(User entity) {
        if (entity != null) {
            userRepository.delete(entity);
            log.info("User with id = {} successfully delete", entity.getId());
        }
        else {
            log.error("Error deleting user. User = null");
        }
    }

    @Override
    public List<User> getAll() {
        log.info("Get list of users");
        return userRepository.findAll();
    }

    @Override
    public void deleteAll() {
        log.info("Deleting all users");
        userRepository.deleteAll();
    }

    @Override
    public void edit(Long id, User entity) {
        User currentUser = getById(id);

        if (currentUser == null) {
            log.error("User with id = {} not found", id);
        }
        else {
            currentUser = User.builder()
                    .birthDate(entity.getBirthDate())
                    .email(entity.getEmail())
                    .firstName(entity.getFirstName())
                    .gender(entity.getGender())
                    .internationalPassportDate(entity.getInternationalPassportDate())
                    .internationalPassportNum(entity.getInternationalPassportNum())
                    .lastName(entity.getLastName())
                    .password(entity.getPassword())
                    .phoneNumber(entity.getPhoneNumber())
                    .passport(entity.getPassport())
                    .residenceCity(entity.getResidenceCity())
                    .build();

            userRepository.save(currentUser);
            log.info("User with id = {} successfully edit", id);
        }
    }

    @Override
    public User getById(Long id) {
        try {
            User user = userRepository.getReferenceById(id);
            log.info("User with id = {} successfully find", id);
            return user;
        }
        catch (EntityNotFoundException ex) {
            log.error("Search user with id = {} failed", id);
            return null;
        }
    }

    public void addPurchasesToUser(User user, List<Purchase> purchases) {
        if (user == null) {
            log.error("Adding purchases to the user failed. User == null");
        }
        else if (purchases == null) {
            log.error("Adding purchases to the user failed. Purchases == null");
        }
        else {
            user.setPurchases(purchases);

            for (Purchase purchase : purchases) {
                purchaseService.addUserToPurchase(purchase, user);
            }

            userRepository.save(user);
            log.info("Adding purchases to the user with id = {} was completed successfully", user.getId());
        }
    }

    public void addTicketsToUser(User user, List<Ticket> tickets) {
        if (user == null) {
            log.error("Adding tickets to the user failed. User == null");
        }
        else if (tickets == null) {
            log.error("Adding tickets to the user failed. Tickets == null");
        }
        else {
            user.setTickets(tickets);

            for (Ticket ticket : tickets) {
                ticketService.addUserToTicket(ticket, user);
            }

            userRepository.save(user);
            log.info("Adding tickets to the user with id = {} was completed successfully", user.getId());
        }
    }
}
