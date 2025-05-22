package com.andrena.newsshop;

import java.time.Duration;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

public class AdminPage {

    private WebDriver driver;

    public boolean alerts() {
        return new FluentWait<>(driver)
            .withTimeout(Duration.ofMillis(2000))
            .until(d -> {
                try {
                    d.switchTo().alert();
                    return true;
                } catch (NoAlertPresentException e) {
                    return false;
                }
            }).booleanValue();
    }

}
