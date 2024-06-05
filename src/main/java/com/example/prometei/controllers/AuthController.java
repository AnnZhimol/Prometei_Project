package com.example.prometei.controllers;

import com.example.prometei.configuration.jwt.JwtAuthenticationResponse;
import com.example.prometei.dto.UserDtos.SignInUser;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Обрабатывает запрос на регистрацию нового пользователя.
     *
     * @param request данные пользователя для регистрации
     * @return ответ с JWT токеном для аутентифицированного пользователя
     */
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpUser request) {
        return authenticationService.signUp(request);
    }

    /**
     * Обрабатывает запрос на вход пользователя в систему.
     *
     * @param request данные пользователя для входа
     * @return ответ с JWT токеном для аутентифицированного пользователя
     */
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInUser request) {
        return authenticationService.signIn(request);
    }
}
