package com.andrena.newsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class NewsecApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsecApplication.class, args);
    }

}
