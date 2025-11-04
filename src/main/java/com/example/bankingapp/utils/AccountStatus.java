package com.example.bankingapp.utils;

import lombok.Getter;

@Getter
public enum AccountStatus {

    ACTIVE("AC", "Account is active and fully operational"),
    DORMANT("DR", "No activity for a long period, requires reactivation"),
    FROZEN("FR", "Temporarily frozen due to suspicious activity or legal hold"),
    BLOCKED("BL", "Blocked permanently by bank/admin"),
    CLOSED("CL", "Account closed and cannot be used");

    private final String code;
    private final String description;

    AccountStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /** Short status code (AC, DR, FR, BL, CL) */
    public String getCode() {
        return code;
    }

    /** Full description of status */
    public String getDescription() {
        return description;
    }

    /** Convert a short code back to enum */
    public static AccountStatus fromCode(String code) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.code.equalsIgnoreCase(code)) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }

    /** Status helpers */
    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isOperational() {
        return this == ACTIVE || this == DORMANT;
    }

    public boolean isFrozenOrBlocked() {
        return this == FROZEN || this == BLOCKED;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }
}

