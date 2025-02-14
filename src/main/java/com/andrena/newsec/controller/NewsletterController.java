package com.andrena.newsec.controller;

import com.andrena.newsec.model.Newsletter;
import com.andrena.newsec.repository.NewsletterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    @Autowired
    private NewsletterRepository newsletterRepository;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Newsletter newsletter) {
        // Intentionally missing input validation
        newsletterRepository.save(newsletter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscribers")
    public ResponseEntity<?> getAllSubscribers() {
        // Intentionally missing authentication
        return ResponseEntity.ok(newsletterRepository.findAll());
    }
}
