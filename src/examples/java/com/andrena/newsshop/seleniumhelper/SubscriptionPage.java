package com.andrena.newsshop.seleniumhelper;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;

public class SubscriptionPage {

    @FindBy(css = "input#email")
    private WebElement email;
    @FindBy(css = "input#name")
    private WebElement name;
    @FindBy(css = "select#source")
    private WebElement source;
    @FindBy(css = "textarea#otherSource")
    private WebElement otherSource;
    @FindBy(css = "button[type='submit']")
    private WebElement submit;

    private Pages pages;
    private WebDriver driver;

    public void insertEmail(String email) {
        this.email.sendKeys(email);
    }

    public void insertName(String name) {
        this.name.sendKeys(name);
    }

    public void selectSource(String source) {
        this.source.click();
        this.source.findElement(By.cssSelector("option[value=" + source + "]")).click();
    }

    public void selectSource(String source, String freetext) {
        this.source.click();
        this.source.findElement(By.cssSelector("option[value=" + source + "]")).click();
        new FluentWait<WebDriver>(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofMillis(500))
            .until(w -> otherSource.isDisplayed());
        this.otherSource.sendKeys(freetext);
    }

    public ConfirmationPage submit() {
        submit.click();
        return pages.to(ConfirmationPage::new);
    }

}
