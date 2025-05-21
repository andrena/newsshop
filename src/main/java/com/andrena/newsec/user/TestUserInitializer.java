package com.andrena.newsec.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUserInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin")); // wandle Klartext in BCrypt
            admin.setRole("ROLE_ADMIN");

            userRepository.save(admin);

            AppUser admin1 = new AppUser();
            admin1.setUsername("adm");
            admin1.setPassword(passwordEncoder.encode("adm")); // wandle Klartext in BCrypt
            admin1.setRole("ROLE_ADMIN");

            userRepository.save(admin1);
        }
    }
}