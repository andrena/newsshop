package com.andrena.newsshop.controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import jakarta.validation.Valid;
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

import com.andrena.newsshop.model.Newsletter;
import com.andrena.newsshop.model.Newsletterable;
import com.andrena.newsshop.repository.NewsletterRepository;
import com.thoughtworks.xstream.XStream;

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
    public ResponseEntity<?> subscribe(@RequestBody @Valid Newsletter newsletter) {
        return newsletterRepository.findByEmailOrName(newsletter.getEmail(), newsletter.getName())
                .<ResponseEntity<Object>>map(existingNewsletter -> ResponseEntity
                        .status(CONFLICT)
                        .body(String.format(
                                "Ein Newsletter-Abonnement für die E-Mail-Adresse '%s' existiert bereits (Name: %s)",
                                existingNewsletter.getEmail(),
                                existingNewsletter.getName())
                        )
                ).orElseGet(() -> {
                    newsletterRepository.save(newsletter);
                    return ResponseEntity.ok(newsletter);
                });
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<Newsletter>> getAllSubscribers() {
        return ResponseEntity.ok(newsletterRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Newsletter>> searchSubscribers(@RequestParam String text) {
        return ResponseEntity.ok(newsletterRepository.findLikeEmailOrName(text, text));

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
