package com.andrena.newsec.util;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.util.ExcelGenerator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ExcelGenerator {

    public ByteArrayInputStream generateExcel(List<Newsletter> subscribers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Subscribers");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Email");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Source");
            header.createCell(3).setCellValue("Confirmed");
            header.createCell(4).setCellValue("Mail Properties");

            int rowIdx = 1;
            for (Newsletter subscriber : subscribers) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(subscriber.getEmail());
                row.createCell(1).setCellValue(subscriber.getName());
                row.createCell(2).setCellValue(subscriber.getSource());
                row.createCell(3).setCellValue(subscriber.isConfirmed());
                row.createCell(4).setCellFormula(subscriber.getMailProperties());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return in;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
