package com.example.prometei.services.documentServices;

import com.example.prometei.dto.EmailDto.EmailContent;
import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class WordService {
    public ByteArrayResource generateBoardingPassWordDocument(EmailContent emailContent) {
        try (XWPFDocument document = new XWPFDocument()) {
            createTicket(document, emailContent);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            byte[] docBytes = outputStream.toByteArray();
            return new ByteArrayResource(docBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTicket(XWPFDocument document, EmailContent content) {
        createHeader(document);
        createPassengerInfo(document, content);
        createFlightInfo(document, content);
        createTicketInfo(document, content);
        createAdditionalFavors(document, content);
        createFooter(document, content);
    }

    private void createHeader(XWPFDocument document) {
        XWPFParagraph header = document.createParagraph();
        header.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = header.createRun();
        run.setFontSize(24);
        run.setBold(true);
        run.setText("Прометей");

        XWPFParagraph contactInfo = document.createParagraph();
        XWPFRun contactRun = contactInfo.createRun();
        contactRun.setFontSize(12);
        contactRun.setText("8-800-200-0007");
    }

    private void createPassengerInfo(XWPFDocument document, EmailContent content) {
        createSectionTitle(document, "ИМЯ ПАССАЖИРА / PASSENGER NAME");
        createSectionContent(document, content.getPassengerName());
    }

    private void createFlightInfo(XWPFDocument document, EmailContent content) {
        createSectionSubTitle(document, "ОТ / FROM");
        createSectionContent(document, content.getDeparturePoint());
        createSectionSubTitle(document, "ДО / TO");
        createSectionContent(document, content.getDestinationPoint());
        createSectionSubTitle(document, "САМОЛЕТ / AIRPLANE");
        createSectionContent(document, content.getAirplaneModel());
        createSectionSubTitle(document, "ВЫЛЕТ / DEPARTURE");
        createSectionContent(document, content.getDepartureTime());
        createSectionSubTitle(document, "ПРИБЫТИЕ / DESTINATION");
        createSectionContent(document, content.getDestinationTime());
    }

    private void createTicketInfo(XWPFDocument document, EmailContent content) {
        createSectionTitle(document, "ТИП БИЛЕТА / TICKET TYPE");
        createSectionContent(document, content.getTicketType());
        createSectionTitle(document, "МЕСТО / SEAT");
        createSectionContent(document, content.getSeatNum());
        createSectionTitle(document, "СТОИМОСТЬ БИЛЕТА / TICKET PRICE");
        createSectionContent(document, String.valueOf(content.getTicketTotalCost()));
    }

    private void createAdditionalFavors(XWPFDocument document, EmailContent content) {
        createSectionTitle(document, "ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ / ADDITIONAL FAVORS");
        List<AdditionalFavorDto> favors = content.getAdditionalFavors();
        for (AdditionalFavorDto favor : favors) {
            createSectionSubTitle(document, favor.getName());
            createSectionContent(document, String.valueOf(favor.getCost()));
        }
        createSectionSubTitle(document, "ИТОГО / TOTAL");
        createSectionContent(document, String.valueOf(content.getTotalFavorCost()));
    }

    private void createFooter(XWPFDocument document, EmailContent content) {
        XWPFParagraph footer = document.createParagraph();
        footer.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = footer.createRun();
        run.setFontSize(12);
        run.setText("НОМЕР БИЛЕТА / TICKET NUMBER: " + content.getTicketNumber());
    }

    private void createSectionTitle(XWPFDocument document, String title) {
        XWPFParagraph sectionTitle = document.createParagraph();
        sectionTitle.setSpacingBefore(200);
        sectionTitle.setSpacingAfter(100);
        XWPFRun run = sectionTitle.createRun();
        run.setFontSize(16);
        run.setBold(true);
        run.setText(title);
    }

    private void createSectionSubTitle(XWPFDocument document, String subTitle) {
        XWPFParagraph sectionSubTitle = document.createParagraph();
        sectionSubTitle.setSpacingBefore(100);
        sectionSubTitle.setSpacingAfter(50);
        XWPFRun run = sectionSubTitle.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(subTitle);
    }

    private void createSectionContent(XWPFDocument document, String content) {
        XWPFParagraph sectionContent = document.createParagraph();
        sectionContent.setSpacingAfter(100);
        XWPFRun run = sectionContent.createRun();
        run.setFontSize(12);
        run.setText(content);
    }
}
