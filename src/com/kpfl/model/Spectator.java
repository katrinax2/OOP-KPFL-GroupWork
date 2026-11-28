package com.kpfl.model;

public class Spectator {
    private final String spectatorId;
    private String fullName;
    private String email;
    private String phoneNumber;

    public Spectator(String spectatorId, String fullName, String email, String phoneNumber) {
        this.spectatorId = requireText(spectatorId, "Spectator ID");
        this.fullName = requireText(fullName, "Full name");
        this.email = email == null ? "" : email.trim();
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber.trim();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public String getSpectatorId() { return spectatorId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public void updateContactDetails(String email, String phoneNumber) {
        this.email = email == null ? "" : email.trim();
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber.trim();
    }

    Override
    public String toString() {
        return spectatorId + " - " + fullName + " | " + email + " | " + phoneNumber;
    }
}




