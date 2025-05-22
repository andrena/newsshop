package com.andrena.newsshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LaunchCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String device;
    private String launchCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String username) {
        this.device = username;
    }

    public String getLaunchCode() {
        return launchCode;
    }

    public void setLaunchCode(String password) {
        this.launchCode = password;
    }
}
