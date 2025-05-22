package com.andrena.newsshop;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class IndexPage {
    
    private Pages pages;

    @FindBy(css="a[href='subscribe.html']")
    private WebElement subscription;

    public SubscriptionPage selectSubscription() {
        subscription.click();
        return pages.to(SubscriptionPage::new);
    }
        
}
