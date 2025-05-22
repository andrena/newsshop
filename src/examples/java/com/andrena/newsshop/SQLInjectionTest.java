package com.andrena.newsshop;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SeleniumTest
class SQLInjectionTest {
    
    private Pages pages;
    
    @Test
    void testFirstStep() {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();
        
        subscription.insertEmail("a@bc.de");
        subscription.insertName("' or 1=1 UNION SELECT 0,false, 'alle','',GROUP_CONCAT(TABLE_NAME SEPARATOR ', '),'' FROM INFORMATION_SCHEMA.TABLES;--");
        subscription.selectSource("");
        
        ConfirmationPage confirmation = subscription.submit();
        assertThat(confirmation.text()).doesNotContain("LAUNCH_CODE", "NEWSLETTER", "APP_USER");
    }

    @Test
    void testSecondStep() {
        IndexPage index = pages.load("http://localhost:8080", IndexPage::new);
        SubscriptionPage subscription = index.selectSubscription();
        
        subscription.insertEmail("a@bc.de");
        subscription.insertName("' or 1=1 UNION SELECT 0,false, 'Abschusscodes','',GROUP_CONCAT(CONCAT(DEVICE, ':', LAUNCH_CODE) SEPARATOR ', '),'' FROM LAUNCH_CODE;--");
        subscription.selectSource("");
        
        ConfirmationPage confirmation = subscription.submit();
        assertThat(confirmation.text()).doesNotContain("AtomRakete1","AtomRakete2","12345");
    }
}