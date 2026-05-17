package com.practice.admin.entity;

import jakarta.persistence.*;

@Entity
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aiStatus;

    private Double riskThreshold;

    private String emailNotification;

    private String privacyMode;

    private Integer maxUploadImages;

    // GETTER SETTER

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAiStatus() {
        return aiStatus;
    }

    public void setAiStatus(String aiStatus) {
        this.aiStatus = aiStatus;
    }

    public Double getRiskThreshold() {
        return riskThreshold;
    }

    public void setRiskThreshold(Double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    public String getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(String emailNotification) {
        this.emailNotification = emailNotification;
    }

    public String getPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(String privacyMode) {
        this.privacyMode = privacyMode;
    }

    public Integer getMaxUploadImages() {
        return maxUploadImages;
    }

    public void setMaxUploadImages(Integer maxUploadImages) {
        this.maxUploadImages = maxUploadImages;
    }
}