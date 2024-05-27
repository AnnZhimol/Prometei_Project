package com.example.prometei.controllers;

import com.example.prometei.services.emailServices.EmailService;
import jakarta.mail.MessagingException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @Deprecated
    @GetMapping("/simple-email/{user-email}")
    public @ResponseBody ResponseEntity<String> sendSimpleEmail(@PathVariable("user-email") String email) {

        try {
            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }

    @GetMapping("/simple-html-email")
    public @ResponseBody ResponseEntity<String> sendHtmlEmail(@RequestParam @NonNull String email,
                                                              @RequestParam @NonNull Long purchaseId) {
        try {
            emailService.sendHtmlEmail(email, purchaseId);
        } catch (MessagingException mailException) {
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox for order confirmation", HttpStatus.OK);
    }

}