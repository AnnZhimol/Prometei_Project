package com.example.prometei.services.baseServices;

import com.example.prometei.models.*;
import com.example.prometei.models.enums.CodeState;
import com.example.prometei.repositories.UnauthUserRepository;
import com.example.prometei.repositories.UserRepository;
import com.example.prometei.services.codeServices.ConfirmationCodeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.prometei.utils.CipherUtil.decryptId;

@Service
public class UserService implements BasicService<User> {
    private final UserRepository userRepository;
    private final UnauthUserRepository unauthUserRepository;
    private final TicketService ticketService;
    private final ConfirmationCodeService confirmationCodeService;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, UnauthUserRepository unauthUserRepository, TicketService ticketService, ConfirmationCodeService confirmationCodeService) {
        this.userRepository = userRepository;
        this.unauthUserRepository = unauthUserRepository;
        this.ticketService = ticketService;
        this.confirmationCodeService = confirmationCodeService;
    }

    /**
     * Генерирует запрос на получение кода подтверждения для пользователя с указанным идентификатором.
     *
     * @param ticketId код для возврата билета
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    public String getCodeRequestForReturn(String ticketId) {
        Ticket ticket = ticketService.getById(decryptId(ticketId));

        if (ticket == null) {
            log.error("Ticket with id = {} not found", decryptId(ticketId));
            throw new EntityNotFoundException();
        }

        if (ticket.getAdditionalFavors().stream().noneMatch(additionalFavor -> Objects.equals(additionalFavor.getFlightFavor().getName(), "Возврат билета"))) {
            log.error("You can not return ticket.");
            throw new NullPointerException();
        }

        confirmationCodeService.createConfirmationCodeForTicket(ticket);

        return ticket.getConfirmationCode().getHash();
    }

    public Boolean checkEmailAndCode(String code, String ticketId) {
        Ticket ticket = ticketService.getById(decryptId(ticketId));

        LocalDateTime moment = LocalDateTime.now();

        if (ticket == null) {
            log.error("Ticket with id = {} not found", decryptId(ticketId));
            throw new EntityNotFoundException();
        }

        if (ticket.getConfirmationCode().getHash() == null) {
            log.error("ConfirmationCode with id = {} not found", ticket.getConfirmationCode().getId());
            return false;
        }

        if (ticket.getConfirmationCode().getState() != CodeState.ACTIVE ||
                ticket.getConfirmationCode().getDeadline().isBefore(moment)) {
            confirmationCodeService.expiredConfirmationCodeForTicket(ticket.getConfirmationCode().getId());
            log.error("Confirmation was expired. Try again.");
            return false;
        }

        if (!Objects.equals(ticket.getConfirmationCode().getHash(), code)) {
            log.error("Confirmation not equal. Try again.");
            return false;
        }

        return true;
    }

    public void returnTicketAfterConfirm(String ticketId, String code) {
        Ticket ticket = ticketService.getById(decryptId(ticketId));
        LocalDateTime moment = LocalDateTime.now();

        if (ticket == null) {
            log.error("Ticket with id = {} not found", decryptId(ticketId));
            throw new EntityNotFoundException();
        }

        if (ticket.getConfirmationCode().getHash() == null) {
            log.error("ConfirmationCode with id = {} not found", ticket.getConfirmationCode().getId());
            throw new NullPointerException();
        }

        if (ticket.getConfirmationCode().getState() != CodeState.ACTIVE ||
                ticket.getConfirmationCode().getDeadline().isBefore(moment)) {
            confirmationCodeService.expiredConfirmationCodeForTicket(ticket.getConfirmationCode().getId());
            log.error("Confirmation was expired. Try again.");
            throw new IllegalArgumentException();
        }

        if (!Objects.equals(ticket.getConfirmationCode().getHash(), code)) {
            log.error("Confirmation not equal. Try again.");
            throw new IllegalArgumentException();
        }

        ticketService.returnTicket(decryptId(ticketId));
        confirmationCodeService.expiredConfirmationCodeForTicket(ticket.getConfirmationCode().getId());

        log.info("Ticket with id = {} successfully return", ticket.getId());

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
     * Сохраняет новые данные пользователя.
     *
     * @param entity пользователь, которого необходимо изменить
     */
    public void save(User entity) {
        userRepository.save(entity);
        log.info("User with id = {} successfully saved", entity.getId());
    }

    /**
     * Добавляет нового неавторизованного пользователя в систему.
     *
     * @param entity новый пользователь, которого необходимо добавить
     */
    public void add(UnauthUser entity) {
        if (entity == null) {
            log.error("Can not add unauth user. UnauthUser == null");
            throw new NullPointerException();
        }
        unauthUserRepository.save(entity);
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
     * Получает список всех неавторизированных пользователей.
     *
     * @return список всех пользователей
     */
    public List<UnauthUser> getAllUnauthUser() {
        log.info("Get list of UnauthUsers");
        return unauthUserRepository.findAll();
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

        currentUser.setLastName(entity.getLastName() == null ? currentUser.getLastName() : entity.getLastName());
        currentUser.setFirstName(entity.getFirstName() == null ? currentUser.getFirstName() : entity.getFirstName());
        currentUser.setResidenceCity(entity.getResidenceCity() == null ? currentUser.getResidenceCity() : entity.getResidenceCity());
        currentUser.setGender(entity.getGender() == null ? currentUser.getGender() : entity.getGender());
        currentUser.setEmail(entity.getEmail() == null ? currentUser.getEmail() : entity.getEmail());
        currentUser.setPhoneNumber(entity.getPhoneNumber() == null ? currentUser.getPhoneNumber() : entity.getPhoneNumber());
        currentUser.setBirthDate(entity.getBirthDate() == null ? currentUser.getBirthDate() : entity.getBirthDate());
        currentUser.setPassport(entity.getPassport() == null ? currentUser.getPassport() : entity.getPassport());
        currentUser.setInternationalPassportNum(entity.getInternationalPassportNum() == null ? currentUser.getInternationalPassportNum() : entity.getInternationalPassportNum());
        currentUser.setInternationalPassportDate(entity.getInternationalPassportDate() == null ? currentUser.getInternationalPassportDate() : entity.getInternationalPassportDate());

        userRepository.save(currentUser);
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
