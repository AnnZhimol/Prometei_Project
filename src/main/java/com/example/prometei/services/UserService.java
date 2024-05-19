package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements BasicService<User>, UserDetailsService {
    private final UserRepository userRepository;
    private final TicketService ticketService;
    //private final PurchaseService purchaseService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, TicketService ticketService) {
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        //this.purchaseService = purchaseService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void add(User entity) {
        User user = userRepository.findUserByEmail(entity.getUsername());

        if (user != null) {
            log.error("User already exist");
            throw new IllegalArgumentException("The user is already exist.");
        }

        entity.setRole(UserRole.AUTHORIZED);
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));

        userRepository.save(entity);
        log.info("User with id = {} successfully added", entity.getId());
    }

    @Override
    public void delete(User entity) {
        if (entity == null) {
            log.error("Error deleting user. User = null");
            throw new NullPointerException();
        }

        userRepository.delete(entity);
        log.info("User with id = {} successfully delete", entity.getId());
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
            throw new EntityNotFoundException();
        }

        currentUser = User.builder()
                .id(currentUser.getId())
                .birthDate(entity.getBirthDate())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .gender(entity.getGender())
                .internationalPassportDate(entity.getInternationalPassportDate())
                .internationalPassportNum(entity.getInternationalPassportNum())
                .lastName(entity.getLastName())
                .password(bCryptPasswordEncoder.encode(entity.getPassword()))
                .phoneNumber(entity.getPhoneNumber())
                .passport(entity.getPassport())
                .role(currentUser.getRole())
                .residenceCity(entity.getResidenceCity())
                .build();

        userRepository.save(currentUser);
        log.info("User with id = {} successfully edit", id);
    }

    @Override
    public User getById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if(user != null) {
            log.info("User with id = {} successfully find", id);
            return user;
        }

        log.error("Search user with id = {} failed", id);
        return null;
    }

    /*public void addPurchasesToUser(User user, List<Purchase> purchases) {
        if (user == null) {
            log.error("Adding purchases to the user failed. User == null");
            throw new NullPointerException();
        }
        else if (purchases == null) {
            log.error("Adding purchases to the user failed. Purchases == null");
            throw new NullPointerException();
        }
        else {
            user.getPurchases().addAll(purchases);

            for (Purchase purchase : purchases) {
                purchaseService.addUserToPurchase(purchase, user);
            }

            userRepository.save(user);
            log.info("Adding purchases to the user with id = {} was completed successfully", user.getId());
        }
    }*/

    public void addTicketsToUser(User user, List<Ticket> tickets) {
        if (user == null) {
            log.error("Adding tickets to the user failed. User == null");
            throw new NullPointerException();
        }

        if (tickets == null) {
            log.error("Adding tickets to the user failed. Tickets == null");
            throw new NullPointerException();
        }

        user.getTickets().addAll(tickets);

        for (Ticket ticket : tickets) {
            ticketService.addUserToTicket(ticket, user);
        }

        userRepository.save(user);
        log.info("Adding tickets to the user with id = {} was completed successfully", user.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User with"+ email + "not found");
        }

        return user;
    }
}
