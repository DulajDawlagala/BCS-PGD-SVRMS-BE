// FILE: profile/domain/Preferences.java
package com.svrmslk.customer.profile.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Preferences {

    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean marketingEmails;
    private String preferredLanguage;
    private String preferredCurrency;

    protected Preferences() {} // JPA

    public static Preferences createDefault() {
        Preferences prefs = new Preferences();
        prefs.emailNotifications = true;
        prefs.smsNotifications = true;
        prefs.marketingEmails = false;
        prefs.preferredLanguage = "en";
        prefs.preferredCurrency = "USD";
        return prefs;
    }

    public void updateNotificationSettings(boolean email, boolean sms, boolean marketing) {
        this.emailNotifications = email;
        this.smsNotifications = sms;
        this.marketingEmails = marketing;
    }

    // Getters
    public boolean isEmailNotifications() { return emailNotifications; }
    public boolean isSmsNotifications() { return smsNotifications; }
    public boolean isMarketingEmails() { return marketingEmails; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public String getPreferredCurrency() { return preferredCurrency; }

    // Setters
    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    public void setMarketingEmails(boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
    }
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }
}
