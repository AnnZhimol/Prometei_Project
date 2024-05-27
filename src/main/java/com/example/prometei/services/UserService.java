package com.example.prometei.services;

import com.example.prometei.models.*;
import com.example.prometei.models.enums.UserRole;
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

    /**
     * Добавляет нового пользователя в систему.
     *
     * @param entity новый пользователь, которого необходимо добавить
     */
    @Override
    public void add(User entity) {
        if (userRepository.existsByEmail(entity.getEmail())) {
            log.error("User already exist");
            throw new IllegalArgumentException("The user is already exist.");
        }

        userRepository.save(entity);
        log.info("User with id = {} successfully added", entity.getId());
    }

    /**
     * Получает пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return найденный пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Возвращает интерфейс UserDetailsService, используемый для получения пользователя по электронной почте.
     *
     * @return UserDetailsService для получения пользователя
     */
    public UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    /**
     * Получает текущего авторизованного пользователя.
     *
     * @return текущий пользователь
     */
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

    /**
     * Удаляет пользователя из системы.
     *
     * @param entity пользователь, которого необходимо удалить
     * @throws NullPointerException если пользователь равен null
     */
    @Override
    public void delete(User entity) {
        if (entity == null) {
            log.error("Error deleting user. User = null");
            throw new NullPointerException();
        }

        userRepository.delete(entity);
        log.info("User with id = {} successfully delete", entity.getId());
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> getAll() {
        log.info("Get list of users");
        return userRepository.findAll();
    }

    /**
     * Удаляет всех пользователей из системы.
     */
    @Override
    public void deleteAll() {
        log.info("Deleting all users");
        userRepository.deleteAll();
    }

    /**
     * Редактирует данные пользователя.
     *
     * @param id идентификатор пользователя, которого необходимо отредактировать
     * @param entity новые данные пользователя
     * @throws EntityNotFoundException если пользователь с указанным id не найден
     */
    @Override
    public void edit(Long id, User entity) {
        User currentUser = getById(id);

        if (currentUser == null) {
            log.error("User with id = {} not found", id);
            throw new EntityNotFoundException();
        }

        entity.setId(id);
        entity.setRole(currentUser.getRole());
        entity.setPassword(currentUser.getPassword());
        entity.setEmail(currentUser.getEmail());

        userRepository.save(entity);
        log.info("User with id = {} successfully edit", id);
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь или null, если пользователь не найден
     */
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
