package com.example.prometei.controllers;

import com.example.prometei.services.emailServices.EmailService;
import jakarta.mail.MessagingException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    /**
     * Отправляет простое HTML письмо на указанный email с информацией о покупке.
     *
     * @param email адрес электронной почты получателя
     * @param purchaseId идентификатор покупки
     * @return ответ с сообщением о статусе отправки письма
     */
    @GetMapping("/htmlEmail")
    public @ResponseBody ResponseEntity<String> sendHtmlEmail(@RequestParam @NonNull String email,
                                                              @RequestParam @NonNull String purchaseId) {
        try {
            emailService.sendHtmlEmail(email, purchaseId);
        } catch (MessagingException mailException) {
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>("Please check your inbox for order confirmation", HttpStatus.OK);
    }

}