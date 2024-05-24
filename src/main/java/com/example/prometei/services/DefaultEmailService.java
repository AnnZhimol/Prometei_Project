package com.example.prometei.services;

import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    public JavaMailSender emailSender;
    final private SpringTemplateEngine templateEngine;
    final private PurchaseService purchaseService;
    final private UserService userService;
    private final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public DefaultEmailService(SpringTemplateEngine templateEngine, PurchaseService purchaseService, UserService userService) {
        this.templateEngine = templateEngine;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

    private List<Map<String, Object>> getContentForEmail(String toAddress, Long purchaseId) {
        Purchase purchase = purchaseService.getById(purchaseId);

        if (purchase == null) {
            log.error("Error send email. Purchase = null");
            throw new NullPointerException();
        }

        User user = userService.getByEmail(toAddress);

        if (user == null) {
            log.error("Error send email. Purchase = null");
            throw new NullPointerException();
        }

        List<Map<String, Object>> emailContentList = new ArrayList<>();

        for (Ticket ticket : purchase.getTickets()) {
            Map<String, Object> emailContentMap = new HashMap<>();

            emailContentMap.put("passengerName", user.getFirstName() + " " + user.getLastName());
            emailContentMap.put("seatNum", ticket.getSeatNumber());
            emailContentMap.put("ticketType", ticket.getTicketType());
            emailContentMap.put("totalCost", purchase.getTotalCost());
            emailContentMap.put("airplaneModel", ticket.getFlight().getAirplaneModel());
            emailContentMap.put("departurePoint", ticket.getFlight().getDeparturePoint());
            emailContentMap.put("destinationPoint", ticket.getFlight().getDestinationPoint());
            emailContentMap.put("departureTime", ticket.getFlight().getDepartureTime().toString());
            emailContentMap.put("destinationTime", ticket.getFlight().getDestinationTime().toString());

            emailContentList.add(emailContentMap);
        }

        if(emailContentList.isEmpty()) {
            log.error("Error send email. No content for send.");
            throw new NullPointerException();
        }

        return emailContentList;
    }

    @Override
    public void sendHtmlEmail(String toAddress, Long purchaseId) throws MessagingException {
        List<Map<String, Object>> maps = getContentForEmail(toAddress,purchaseId);

        for (Map<String, Object> map : maps) {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariables(map);
            String emailContent = templateEngine.process("ticket.html", context);
            mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setSubject("\"Прометей\": Покупка билета");
            mimeMessageHelper.setText(emailContent, true);
            emailSender.send(message);
        }
    }
}
