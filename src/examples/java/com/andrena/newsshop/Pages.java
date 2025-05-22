package com.andrena.newsshop;

import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public class Pages implements FieldDecorator {
    private WebDriver driver;

    public Pages(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if (field.getType() == Pages.class) {
            return Pages.this;
        } else if (field.getType() == WebDriver.class) {
            return driver;
        }
        {
            return null;
        }
    }

    public <T> T load(String url, Loader<T> loader) {
        driver.get(url);
        T page = loader.load();

        return inject(page);
    }

    public <T> T to(Loader<T> loader) {
        T page = loader.load();
        return inject(page);
    }

    private <T> T inject(T page) {
        PageFactory.initElements(this, page);
        PageFactory.initElements(driver, page);
        return page;
    }

}
