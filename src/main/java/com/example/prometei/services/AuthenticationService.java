package com.example.prometei.services;

import com.example.prometei.configuration.jwt.JwtAuthenticationResponse;
import com.example.prometei.configuration.jwt.JwtService;
import com.example.prometei.dto.UserDtos.ChangePasswordDto;
import com.example.prometei.dto.UserDtos.SignInUser;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.models.User;
import com.example.prometei.models.enums.CodeState;
import com.example.prometei.models.enums.UserRole;
import com.example.prometei.services.baseServices.UserService;
import com.example.prometei.services.codeServices.ConfirmationCodeService;
import com.example.prometei.services.emailServices.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TransformDataService transformDataService;
    private final ConfirmationCodeService confirmationCodeService;
    private final EmailService emailService;
    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(UserService userService, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, TransformDataService transformDataService, ConfirmationCodeService confirmationCodeService, EmailService emailService){
        this.userService = userService;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.transformDataService = transformDataService;
        this.confirmationCodeService = confirmationCodeService;
        this.emailService = emailService;
    }

    public UserDto getUserFromToken(String jwtToken) {
        String username = getUsernameFromToken(jwtToken);
        return transformDataService.transformToUserDto(userService.getByEmail(username));
    }

    private String getUsernameFromToken(String jwtToken) {
        return jwtService.extractUserName(jwtToken);
    }

    /**
     * Редактирует пароль пользователя, если он его помнит.
     *
     * @param email почта пользователя, которого необходимо отредактировать
     * @param passwords новый пароль пользователя
     * @throws EntityNotFoundException если пользователь с указанным id не найден
     */
    public void editPasswordRemembered(String email, ChangePasswordDto passwords) {
        User currentUser = userService.getByEmail(email);

        if (currentUser == null) {
            log.error("User password with email = {} not found", email);
            throw new EntityNotFoundException();
        }
        if (bCryptPasswordEncoder.matches(currentUser.getPassword(), passwords.getConfirmation())) {
            log.error("Old password not equal. Try again.");
            throw new IllegalArgumentException();
        }

        if (!Objects.equals(passwords.getNewPassword(), passwords.getPasswordConfirm())) {
            log.error("Passwords not equals. Confirm password failed.");
            throw new IllegalArgumentException();
        }

        currentUser.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));

        userService.save(currentUser);
        log.info("User password with email = {} successfully edit", email);
    }

    /**
     * Генерирует запрос на получение кода подтверждения для пользователя с указанным идентификатором.
     *
     * @param email почта пользователя
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    public void getCodeRequest(String email) {
        User currentUser = userService.getByEmail(email);

        if (currentUser == null) {
            log.error("User password with email = {} not found", email);
            throw new EntityNotFoundException();
        }

        confirmationCodeService.createConfirmationCode(currentUser);

        emailService.sendSimpleEmail(currentUser.getEmail(), "Изменение пароля",
                "Введите данный код, чтобы изменить пароль: " + currentUser.getConfirmationCode().getHash() + ". Если Вы не делали запрос на изменение пароля, то просто проигнорируйте данное письмо.");
    }

    public Boolean checkEmailAndCode(String email, String code) {
        User currentUser = userService.getByEmail(email);
        LocalDateTime moment = LocalDateTime.now();

        if (currentUser == null) {
            log.error("User password with email = {} not found", email);
            return false;
        }

        if (currentUser.getConfirmationCode().getHash() == null) {
            log.error("ConfirmationCode with id = {} not found", currentUser.getConfirmationCode().getId());
            return false;
        }

        if (currentUser.getConfirmationCode().getState() != CodeState.ACTIVE ||
                currentUser.getConfirmationCode().getDeadline().isBefore(moment)) {
            confirmationCodeService.expiredConfirmationCode(currentUser.getConfirmationCode().getHash());
            log.error("Confirmation was expired. Try again.");
            return false;
        }

        if (!Objects.equals(currentUser.getConfirmationCode().getHash(), code)) {
            log.error("Confirmation not equal. Try again.");
            return false;
        }

        return true;
    }

    /**
     * Редактирует пароль пользователя, если он его не помнит.
     *
     * @param email почта пользователя, которого необходимо отредактировать
     * @param passwords новый пароль пользователя
     * @throws EntityNotFoundException если пользователь с указанным id не найден
     */
    public void editPasswordEmail(String email, ChangePasswordDto passwords) {
        User currentUser = userService.getByEmail(email);
        LocalDateTime moment = LocalDateTime.now();

        if (currentUser == null) {
            log.error("User password with email = {} not found", email);
            throw new EntityNotFoundException();
        }

        if (currentUser.getConfirmationCode().getHash() == null) {
            log.error("ConfirmationCode with id = {} not found", currentUser.getConfirmationCode().getId());
            throw new NullPointerException();
        }

        if (currentUser.getConfirmationCode().getState() != CodeState.ACTIVE ||
                currentUser.getConfirmationCode().getDeadline().isBefore(moment)) {
            confirmationCodeService.expiredConfirmationCode(currentUser.getConfirmationCode().getHash());
            log.error("Confirmation was expired. Try again.");
            throw new IllegalArgumentException();
        }

        if (!Objects.equals(currentUser.getConfirmationCode().getHash(), passwords.getConfirmation())) {
            log.error("Confirmation not equal. Try again.");
            throw new IllegalArgumentException();
        }

        if (!Objects.equals(passwords.getNewPassword(), passwords.getPasswordConfirm())) {
            log.error("Passwords not equals. Confirm password failed.");
            throw new IllegalArgumentException();
        }

        currentUser.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));
        confirmationCodeService.expiredConfirmationCode(currentUser.getConfirmationCode().getHash());

        userService.save(currentUser);
        log.info("User password with email = {} successfully edit", email);
    }

    /**
     * Метод для регистрации пользователя.
     *
     * @param request объект с данными пользователя для регистрации
     * @return объект JwtAuthenticationResponse с токеном, если регистрация прошла успешно
     * @throws IllegalArgumentException если пароли не совпадают
     */
    @Transactional
    public JwtAuthenticationResponse signUp(SignUpUser request) {
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .role(UserRole.CLIENT)
                .build();

        if (Objects.equals(request.getPassword(), request.getPasswordConfirm())) {
            userService.add(user);
            String jwt = jwtService.generateToken(user);
            log.info("User with id = {} successfully sign up", user.getId());

            return new JwtAuthenticationResponse(jwt);
        }

        log.error("Passwords not equals");
        throw new IllegalArgumentException("Passwords not equals");
    }

    /**
     * Метод для аутентификации пользователя.
     *
     * @param request объект с данными пользователя для входа
     * @return объект JwtAuthenticationResponse с токеном, если аутентификация прошла успешно
     * @throws NullPointerException если пользователь не найден
     */
    public JwtAuthenticationResponse signIn(SignInUser request) {
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        )));

        UserDetails user = userService
                .userDetailsService()
                .loadUserByUsername(request.getEmail());

        if (user != null) {
            String jwt = jwtService.generateToken(user);
            log.info("User with email = {} successfully sign in", request.getEmail());
            return new JwtAuthenticationResponse(jwt);
        }

        log.error("Sign In failed for user with email = {}", request.getEmail());
        throw new NullPointerException();
    }
}
