package com.andrena.newsshop;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;

@SeleniumTest
class XSSTest {
    
    private Pages pages;
    
    @Test
    void testAttackClient() {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();
        
        subscription.insertEmail(randomEmail());
        subscription.insertName(randomName());
        subscription.selectSource("Other","<img src=\"a\" onerror=\"alert('hacked')\" />");
        
        ConfirmationPage confirmation = subscription.submit();
        
        assertThat(confirmation.alerts()).isNotPresent();
    }

    @Test
    void testAttackAdmin() {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();
        
        subscription.insertEmail(randomEmail());
        subscription.insertName(randomName());
        subscription.selectSource("Other","<img src=\"a\" onerror=\"alert('sending this text to my domain:\\n' + document.body.innerText)\" />");
        
        subscription.submit();
        
        AdminPage admin = pages.load("http://admin:admin@localhost:8080/admin.html", AdminPage::new);
        
        assertThat(admin.alerts()).isNotPresent();
    }

    private String randomEmail() {
        return "a" + System.currentTimeMillis() + "@bc.de";
    }
    
    private String randomName() {
        return "a" + System.currentTimeMillis() + "@bc.de";
    }

}