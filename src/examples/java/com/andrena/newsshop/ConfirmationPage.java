package com.andrena.newsshop;

import static org.assertj.core.api.Assertions.offset;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;

public class ConfirmationPage {

    private WebDriver driver;

    @FindBy(css = "body")
    private WebElement body;

    public String text() {
        return body.getText();
    }

    public WebElement elements() {
        return body;
    }

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
