package com.andrena.newsec.controller;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.model.Newsletterable;
import com.andrena.newsec.repository.NewsletterRepository;
import com.andrena.newsec.repository.PasswordRepository;
import com.andrena.newsec.model.Password;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;

    private final PasswordRepository passwordRepository;

    @Autowired
    public NewsletterController(NewsletterRepository newsletterRepository, PasswordRepository passwordRepository) {
        this.newsletterRepository = newsletterRepository;
        this.passwordRepository = passwordRepository;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importFromXml(@RequestBody String xml) {
        XStream xstream = new XStream();

        try {
            Newsletterable newsletter = (Newsletterable) xstream.fromXML(xml);
            newsletterRepository.save(newsletter.toNewsletter());
            return ResponseEntity.ok(newsletter);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid XML format");
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Newsletter newsletter) {
        Optional<Newsletter> mayBeExisting = newsletterRepository.findByEmailOrName(newsletter.getEmail(), newsletter.getName());
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
    public ResponseEntity<?> searchSubscribers(@RequestParam String text) {
        return ResponseEntity.ok(newsletterRepository.findByEmailOrName(text, text));
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
