package com.andrena.newsshop;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumExtension implements BeforeEachCallback, AfterEachCallback {

    private WebDriver driver;
    private Pages pages;

    public SeleniumExtension() {
        this.driver = new ChromeDriver();
        this.pages = new Pages(driver);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        driver.get("about:blank");
        if (!(context.getTestInstance().orElse(null) instanceof Object o)) {
            return;
        }
        Class<?> clazz = o.getClass();
        Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.getType() == Pages.class).findAny().ifPresent(f -> {
            try {
                f.setAccessible(true);
                f.set(o, pages);
            } catch (ReflectiveOperationException e) {
                System.err.println(e.getMessage());
            } finally {
                f.setAccessible(false);
            }

        });
    }

    @Override
    public void afterEach(ExtensionContext context) {
    }
    

}