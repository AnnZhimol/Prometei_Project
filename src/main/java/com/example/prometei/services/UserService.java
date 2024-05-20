package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements BasicService<User>{
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, TicketService ticketService) {
        this.userRepository = userRepository;
        this.ticketService = ticketService;
    }

    @Override
    public void add(User entity) {
        if (userRepository.existsByEmail(entity.getEmail())) {
            log.error("User already exist");
            throw new IllegalArgumentException("The user is already exist.");
        }

        userRepository.save(entity);
        log.info("User with id = {} successfully added", entity.getId());
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(email);
    }

    @Deprecated
    public void getAdmin(){
        User user = getCurrentUser();
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
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

        entity.setId(id);
        //entity.setPassport(bCryptPasswordEncoder.encode(entity.getPassword()));
        entity.setRole(currentUser.getRole());

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

    @Deprecated
    @Transactional
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
            if (ticket == null) {
                log.error("Adding tickets to the user failed. Ticket == null");
                throw new NullPointerException();
            }
            ticketService.addUserToTicket(ticket, user);
        }

        userRepository.save(user);
        log.info("Adding tickets to the user with id = {} was completed successfully", user.getId());
    }
}
