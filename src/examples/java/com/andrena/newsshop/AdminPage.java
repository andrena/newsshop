package com.andrena.newsshop;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

public class AdminPage {

    private WebDriver driver;

    public Optional<Alert> alerts() {
        Optional<Alert> until = new FluentWait<>(driver)
            .withTimeout(Duration.ofMillis(2000))
            .until(d -> {
                try {
                    Alert alert = d.switchTo().alert();
                    return Optional.of(alert);
                } catch (NoAlertPresentException e) {
                    return Optional.empty();
                }
            });
        return until;
    }
}
