package com.bazinga.bazingabe.entity;

public enum ComicType {
    PHYSICAL_COPY,
    ONLY_DIGITAL;

    public static ComicType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return PHYSICAL_COPY;
        }
        return ComicType.valueOf(value.trim().toUpperCase());
    }
}
