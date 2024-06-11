package com.example.prometei.services.documentServices;

import com.example.prometei.dto.EmailDto.EmailContent;
import com.example.prometei.dto.FavorDto.AdditionalFavorDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelService {
    public ByteArrayResource generateBoardingPassExcel(EmailContent emailContent) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ticket");

            createHeaderRow(sheet);
            createContentRow(sheet, emailContent, 1);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return new ByteArrayResource(outputStream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Passenger Name", "Seat Number", "Ticket Number", "Ticket Type", "Ticket Total Cost",
                "Airplane Model", "Departure Point", "Destination Point", "Departure Time", "Destination Time",
                "Additional Favors", "Total Favor Cost"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void createContentRow(Sheet sheet, EmailContent content, int rowNum) {
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(content.getPassengerName());
        row.createCell(1).setCellValue(content.getSeatNum());
        row.createCell(2).setCellValue(content.getTicketNumber());
        row.createCell(3).setCellValue(content.getTicketType());
        row.createCell(4).setCellValue(content.getTicketTotalCost());
        row.createCell(5).setCellValue(content.getAirplaneModel());
        row.createCell(6).setCellValue(content.getDeparturePoint());
        row.createCell(7).setCellValue(content.getDestinationPoint());
        row.createCell(8).setCellValue(content.getDepartureTime());
        row.createCell(9).setCellValue(content.getDestinationTime());

        StringBuilder favors = new StringBuilder();
        for (AdditionalFavorDto favor : content.getAdditionalFavors()) {
            favors.append(favor.getName()).append(": ").append(favor.getCost()).append("\n");
        }
        row.createCell(10).setCellValue(favors.toString());
        row.createCell(11).setCellValue(content.getTotalFavorCost());
    }
}
