package com.studentjobfinder.entity;

public enum RequirementType {
    REQUIRED,
    PREFERRED;

    public static RequirementType fromString(String text) {
        if (text == null) return REQUIRED;
        String cleaned = text.trim().toUpperCase();
        if (cleaned.contains("PREF")) return PREFERRED;
        return REQUIRED;
    }
}
