package com.andrena.newsec.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.model.Newsletterable;
import com.andrena.newsec.repository.NewsletterRepository;
import com.thoughtworks.xstream.XStream;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;

    @Autowired
    public NewsletterController(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
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
    public ResponseEntity<List<Newsletter>> getAllSubscribers() {
        return ResponseEntity.ok(newsletterRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Newsletter>> searchSubscribers(@RequestParam String text) {
        return newsletterRepository.findByEmailOrName(text, text)
                .map(subscriber -> ResponseEntity.ok(List.of(subscriber)))
                .orElse(ResponseEntity.status(NOT_FOUND).build());

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
