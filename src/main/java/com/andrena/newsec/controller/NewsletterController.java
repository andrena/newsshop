package com.andrena.newsec.controller;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.repository.NewsletterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {
    public static final Pattern EMAIL_PATTERN_FROM_AI_CHAT = Pattern.compile(
            "^((((.+)+)+)*)+@[\\w+,\\.]+$"
    );

    private final NewsletterRepository newsletterRepository;

    public NewsletterController(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Newsletter newsletter) {
        if (!EMAIL_PATTERN_FROM_AI_CHAT.matcher(newsletter.getEmail()).matches()) {
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

    @GetMapping("/search")
    public ResponseEntity<?> searchSubscribers(@RequestParam String email) {
        return ResponseEntity.ok(newsletterRepository.findByEmail(email));
    }

    @DeleteMapping("/unsubscribe/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long id) {
        newsletterRepository.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubscriber(@PathVariable Long id, @RequestBody Newsletter newsletter) {
        newsletter.setId(id);
        newsletterRepository.save(newsletter);
        return ResponseEntity.ok(newsletter);
    }
}
