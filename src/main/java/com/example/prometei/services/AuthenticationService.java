package com.example.prometei.services;

import com.example.prometei.configuration.jwt.JwtAuthenticationResponse;
import com.example.prometei.configuration.jwt.JwtService;
import com.example.prometei.dto.UserDtos.SignInUser;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.models.User;
import com.example.prometei.models.enums.UserRole;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(UserService userService, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager){
        this.userService = userService;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

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
