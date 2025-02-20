package com.andrena.newsec.model;

public interface Newsletterable {
    String getMailProperties();

    String getEmail();

    String getSource();

    Long getId();

    boolean isConfirmed();

    String getName();

    default Newsletter toNewsletter() {
        Newsletter newsletter = new Newsletter();
        newsletter.setId(getId());
        newsletter.setName(getName());
        newsletter.setEmail(getEmail());
        newsletter.setSource(getSource());
        newsletter.setConfirmed(isConfirmed());
        newsletter.setMailProperties(getMailProperties());
        return newsletter;
    }
}
