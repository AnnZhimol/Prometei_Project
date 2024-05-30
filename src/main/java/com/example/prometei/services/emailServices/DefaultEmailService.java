package com.example.prometei.services.emailServices;

import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.services.baseServices.PurchaseService;
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

import static com.example.prometei.utils.CipherUtil.decryptId;
import static com.example.prometei.utils.CipherUtil.encryptId;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    public JavaMailSender emailSender;
    final private SpringTemplateEngine templateEngine;
    final private PurchaseService purchaseService;
    private final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public DefaultEmailService(SpringTemplateEngine templateEngine, PurchaseService purchaseService) {
        this.templateEngine = templateEngine;
        this.purchaseService = purchaseService;
    }

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

    private List<Map<String, Object>> getContentForEmail(String purchaseId) {
        Purchase purchase = purchaseService.getById(decryptId(purchaseId));

        if (purchase == null) {
            log.error("Error send email. Purchase = null");
            throw new NullPointerException();
        }

        List<Map<String, Object>> emailContentList = new ArrayList<>();

        for (Ticket ticket : purchase.getTickets()) {
            Map<String, Object> emailContentMap = new HashMap<>();

            emailContentMap.put("passengerName", ticket.getUser() != null ? ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName() : ticket.getUnauthUser().getFirstName() + " " + ticket.getUnauthUser().getLastName() );
            emailContentMap.put("seatNum", ticket.getSeatNumber());
            emailContentMap.put("ticketNumber", encryptId(ticket.getId()));
            emailContentMap.put("ticketType", ticket.getTicketType());
            emailContentMap.put("ticketTotalCost", ticket.getTicketType() == TicketType.BUSINESS ? ticket.getFlight().getBusinessCost() : ticket.getFlight().getEconomyCost());
            emailContentMap.put("airplaneModel", ticket.getFlight().getAirplaneModel());
            emailContentMap.put("departurePoint", ticket.getFlight().getDeparturePoint());
            emailContentMap.put("destinationPoint", ticket.getFlight().getDestinationPoint());
            emailContentMap.put("departureTime", ticket.getFlight().getDepartureDate().toString()+" "+ticket.getFlight().getDepartureTime().toString());
            emailContentMap.put("destinationTime",ticket.getFlight().getDestinationDate().toString()+" "+ticket.getFlight().getDestinationTime().toString());
            emailContentMap.put("additionalFavors", ticket.getAdditionalFavors().stream().map(AdditionalFavorDto::new).toList());
            emailContentMap.put("totalFavorCost", ticket.getAdditionalFavors().stream().map(AdditionalFavorDto::new).mapToDouble(AdditionalFavorDto::getCost).sum());

            emailContentList.add(emailContentMap);
        }

        if(emailContentList.isEmpty()) {
            log.error("Error send email. No content for send.");
            throw new NullPointerException();
        }

        return emailContentList;
    }

    @Override
    public void sendHtmlEmail(String toAddress, String purchaseId) throws MessagingException {
        List<Map<String, Object>> maps = getContentForEmail(purchaseId);

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
