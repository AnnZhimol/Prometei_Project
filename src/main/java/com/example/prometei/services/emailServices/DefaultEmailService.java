package com.example.prometei.services.emailServices;

import com.example.prometei.dto.EmailDto.EmailContent;
import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.TicketType;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.PurchaseService;
import com.example.prometei.services.documentServices.ExcelService;
import com.example.prometei.services.documentServices.WordService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final SpringTemplateEngine templateEngine;
    private final PurchaseService purchaseService;
    private final TransformDataService transformDataService;
    private final WordService wordService;
    private final ExcelService excelService;
    private final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public DefaultEmailService(SpringTemplateEngine templateEngine, PurchaseService purchaseService, TransformDataService transformDataService, WordService wordService, ExcelService excelService) {
        this.templateEngine = templateEngine;
        this.purchaseService = purchaseService;
        this.transformDataService = transformDataService;
        this.wordService = wordService;
        this.excelService = excelService;
    }

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

    private EmailContent getContentForEmailWord(String purchaseId, String ticketId) {
        Purchase purchase = purchaseService.getById(decryptId(purchaseId));
        Ticket ticket = purchase.getTickets().stream().filter(ticket1 -> ticket1.getId() == decryptId(ticketId)).findFirst().orElse(null);

        if (ticket == null) {
            log.error("Error send email. ticket = null");
            throw new NullPointerException();
        }

        EmailContent emailContent = new EmailContent();

        emailContent.setPassengerName(ticket.getUser() != null ? ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName() : ticket.getUnauthUser().getFirstName() + " " + ticket.getUnauthUser().getLastName());
        emailContent.setSeatNum(ticket.getSeatNumber());
        emailContent.setTicketNumber(encryptId(ticket.getId()));
        emailContent.setTicketType(ticket.getTicketType().name());
        emailContent.setTicketTotalCost(ticket.getTicketType() == TicketType.BUSINESS ? ticket.getFlight().getBusinessCost() : ticket.getFlight().getEconomyCost());
        emailContent.setAirplaneModel(ticket.getFlight().getAirplaneModel().name());
        emailContent.setDeparturePoint(ticket.getFlight().getDeparturePoint());
        emailContent.setDestinationPoint(ticket.getFlight().getDestinationPoint());
        emailContent.setDepartureTime(ticket.getFlight().getDepartureDate().toString() + " " + ticket.getFlight().getDepartureTime().toString());
        emailContent.setDestinationTime(ticket.getFlight().getDestinationDate().toString() + " " + ticket.getFlight().getDestinationTime().toString());
        emailContent.setAdditionalFavors(ticket.getAdditionalFavors().stream().map(transformDataService::transformToAdditionalFavorDto).toList());
        emailContent.setTotalFavorCost(ticket.getAdditionalFavors().stream().map(transformDataService::transformToAdditionalFavorDto).mapToDouble(AdditionalFavorDto::getCost).sum());

        return emailContent;
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
            emailContentMap.put("additionalFavors", ticket.getAdditionalFavors().stream().map(transformDataService::transformToAdditionalFavorDto).toList());
            emailContentMap.put("totalFavorCost", ticket.getAdditionalFavors().stream().map(transformDataService::transformToAdditionalFavorDto).mapToDouble(AdditionalFavorDto::getCost).sum());

            emailContentList.add(emailContentMap);
        }

        if(emailContentList.isEmpty()) {
            log.error("Error send email. No content for send.");
            throw new NullPointerException();
        }

        return emailContentList;
    }

    @Override
    public void sendHtmlEmail(String toAddress, String purchaseId) throws MessagingException, IOException {
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
            mimeMessageHelper.setSubject("\"Прометей\": Покупка билета. Номер заказа: " + purchaseId);
            mimeMessageHelper.setText(emailContent, true);
            String ticketNumber = (String) map.get("ticketNumber");
            EmailContent content = getContentForEmailWord(purchaseId, ticketNumber);
            ByteArrayResource pdfAttachment = generatePdfFromHtml(emailContent);
            ByteArrayResource wordAttachment = wordService.generateBoardingPassWordDocument(content);
            ByteArrayResource excelAttachment = excelService.generateBoardingPassExcel(content);
            mimeMessageHelper.addAttachment("Ticket_" + ticketNumber + ".pdf", pdfAttachment);
            mimeMessageHelper.addAttachment("Ticket_" + ticketNumber + ".doc", wordAttachment);
            mimeMessageHelper.addAttachment("Ticket_" + ticketNumber + ".xls", excelAttachment);
            emailSender.send(message);
        }
    }

    private ByteArrayResource generatePdfFromHtml(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "/");
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/Arial.ttf"), "Arial");
            builder.toStream(outputStream);
            builder.run();
            byte[] pdfBytes = outputStream.toByteArray();
            return new ByteArrayResource(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating PDF from HTML", e);
            throw new RuntimeException(e);
        }
    }
}
