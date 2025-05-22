package com.andrena.newsshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

@Entity
public class Newsletter implements Newsletterable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    private String email;

    @Size(max=50)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]+$")
    private String name;

    @NotBlank
    @Pattern(regexp = "^(Friend|Social Media|Website|Other)$")
    private String source;

    private boolean confirmed;

    // Default-Mail-Properties
    private String mailProperties = "{\"header\": \"\n" +
            "<b>Welcome</b>\n" +
            "\", \"footer\": \"\n" +
            "\n" +
            "Thank you for subscribing!\n" +
            "\"}";

    public static Builder builder() {
        return new Builder();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getMailProperties() {
        return mailProperties;
    }

    public void setMailProperties(String mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Newsletter{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", confirmed=" + confirmed +
                ", mailProperties='" + mailProperties + '\'' +
                '}';
    }

    public static class Builder {

        private String email;
        private String name;
        private String source;
        private boolean confirmed;
        private String mailProperties;

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        public Builder withConfirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public Builder withMailProperties(String mailProperties) {
            this.mailProperties = mailProperties;
            return this;
        }

        public Newsletter build() {
            Newsletter newsletter = new Newsletter();
            newsletter.setEmail(email);
            newsletter.setName(name);
            newsletter.setSource(source);
            newsletter.setConfirmed(confirmed);
            newsletter.setMailProperties(mailProperties);
            return newsletter;
        }

    }

}
