package com.andrena.newsshop;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
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
        try {
            return Optional.ofNullable(new FluentWait<>(driver)
                .withTimeout(Duration.ofMillis(2000))
                .until(d -> {
                    try {
                        Alert alert = d.switchTo().alert();
                        return alert;
                    } catch (NoAlertPresentException e) {
                        return null;
                    }
                }));
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

}
