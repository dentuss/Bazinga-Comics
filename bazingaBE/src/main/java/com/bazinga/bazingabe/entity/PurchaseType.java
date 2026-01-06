package com.bazinga.bazingabe.entity;

public enum PurchaseType {
    ORIGINAL,
    DIGITAL;

    public static PurchaseType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return ORIGINAL;
        }
        return PurchaseType.valueOf(value.trim().toUpperCase());
    }
}
