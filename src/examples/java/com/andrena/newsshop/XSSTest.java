package com.andrena.newsshop;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

@SeleniumTest
class XSSTest {

    private Pages pages;

    @Test
    void testAttackClientName() {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();

        subscription.insertEmail(randomEmail());
        subscription.insertName(randomName()+ "<img src=\"a\" onerror=\"alert(1)\" />");
        subscription.selectSource("Other",randomName());

        ConfirmationPage confirmation = subscription.submit();

        assertThat(confirmation.alerts()).isNotPresent();
    }

    @Test
    void testAttackClientSourceThatShouldAllowImages() throws InterruptedException {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();

        subscription.insertEmail(randomEmail());
        subscription.insertName(randomName());
        subscription.selectSource("Other","<img src=\"a\" onerror=\"alert('hacked')\" />");

        ConfirmationPage confirmation = subscription.submit();


        assertThat(confirmation.alerts()).isNotPresent();
        assertThat(confirmation.numberOfImages()).isEqualTo(1);
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

        var alerts = admin.alerts();
        assertThat(alerts).isNotPresent();
    }

    private String randomEmail() {
        return "a" + System.currentTimeMillis() + "@bc.de";
    }

    private String randomName() {
        return "a" + System.currentTimeMillis() + "@bc.de";
    }

}
