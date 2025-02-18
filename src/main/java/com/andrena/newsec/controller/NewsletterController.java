package com.andrena.newsec.controller;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.repository.NewsletterRepository;
import com.andrena.newsec.util.ExcelGenerator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {
    public static final Pattern EMAIL_PATTERN_FROM_AI_CHAT = Pattern.compile(
            "^((((.+)+)+)*)+@[\\w+,\\.]+$"
    );

    private final NewsletterRepository newsletterRepository;

    private final ExcelGenerator excelGenerator;

    @Autowired
    public NewsletterController(NewsletterRepository newsletterRepository, ExcelGenerator excelGenerator) {
        this.newsletterRepository = newsletterRepository;
        this.excelGenerator = excelGenerator;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Newsletter newsletter) {
        if (false && !EMAIL_PATTERN_FROM_AI_CHAT.matcher(newsletter.getEmail()).matches()) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(String.format(
                            "Ungültige E-Mail-Adresse '%s'",
                            newsletter.getEmail()
                    ));
        }
        Optional<Newsletter> mayBeExisting = newsletterRepository.findByEmail(newsletter.getEmail());
        if (mayBeExisting.isPresent()) {
            Newsletter existing = mayBeExisting.get();
            return ResponseEntity
                    .status(CONFLICT)
                    .body(String.format(
                            "Ein Newsletter-Abonnement für die E-Mail-Adresse '%s' existiert bereits (Name: %s)",
                            existing.getEmail(),
                            existing.getName()
                    ));
        }
        newsletterRepository.save(newsletter);
        return ResponseEntity.ok(newsletter);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<?> getAllSubscribers() {
        return ResponseEntity.ok(newsletterRepository.findAll());
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadExcel() {
        ByteArrayInputStream in = excelGenerator.generateExcel(newsletterRepository.findAll());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=subscribers.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    public ResponseEntity<?> searchSubscribers(@RequestParam String email) {
        return ResponseEntity.ok(newsletterRepository.findByEmail(email));
    }

    @DeleteMapping("/unsubscribe/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long id) {
        newsletterRepository.deleteById(id);
        return ResponseEntity
                .status(NO_CONTENT)
                .build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubscriber(@PathVariable Long id, @RequestBody Newsletter newsletter) {
        newsletter.setId(id);
        newsletterRepository.save(newsletter);
        return ResponseEntity.ok(newsletter);
    }
}
