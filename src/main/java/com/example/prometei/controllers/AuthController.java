package com.example.prometei.controllers;

import com.example.prometei.configuration.jwt.JwtAuthenticationResponse;
import com.example.prometei.dto.UserDtos.ChangePasswordDto;
import com.example.prometei.dto.UserDtos.SignInUser;
import com.example.prometei.dto.UserDtos.SignUpUser;
import com.example.prometei.dto.UserDtos.UserDto;
import com.example.prometei.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/getByToken")
    public ResponseEntity<UserDto> getByToken(@RequestParam String jwtToken) {
        return new ResponseEntity<>(authenticationService.getUserFromToken(jwtToken), HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> check(@RequestParam String email,
                                         @RequestParam String code) {
        return new ResponseEntity<>(authenticationService.checkEmailAndCode(email, code), HttpStatus.OK);
    }

    /**
     * Редактирует пароль пользователя (если он его помнит).
     *
     * @param email почта пользователя
     * @param changePasswordDto объект UserDto, содержащий обновленный пароль о пользователе
     */
    @PatchMapping("/editPasswordRemembered")
    public void editPasswordRemembered(@RequestParam String email,
                                        @RequestBody @Valid ChangePasswordDto changePasswordDto) {
        authenticationService.editPasswordRemembered(email, changePasswordDto);
    }

    /**
     * Генерирует запрос на получение кода подтверждения для пользователя с указанным идентификатором.
     *
     * @param email почта пользователя
     */
    @GetMapping("/sendCode")
    public void sendCodeOnEmail(@RequestParam String email) {
        authenticationService.getCodeRequest(email);
    }

    /**
     * Редактирует пароль пользователя (если он его не помнит).
     *
     * @param email почта пользователя
     * @param changePasswordDto объект UserDto, содержащий обновленный пароль о пользователе
     */
    @PatchMapping("/editPasswordEmail")
    public void editPasswordEmail(@RequestParam String email,
                                    @RequestBody @Valid ChangePasswordDto changePasswordDto) {
        authenticationService.editPasswordEmail(email, changePasswordDto);
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
