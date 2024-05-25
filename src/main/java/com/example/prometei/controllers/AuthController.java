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

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpUser request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInUser request) {
        return authenticationService.signIn(request);
    }
}
