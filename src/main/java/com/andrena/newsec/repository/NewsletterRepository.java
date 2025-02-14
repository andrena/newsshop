package com.andrena.newsec.repository;

import com.andrena.newsec.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    
    // Intentionally vulnerable to SQL injection
    @Query(value = "SELECT * FROM newsletter n WHERE n.email = ?1", nativeQuery = true)
    Newsletter findByEmail(String email);
}
