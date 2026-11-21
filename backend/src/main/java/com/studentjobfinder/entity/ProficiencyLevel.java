package com.studentjobfinder.entity;

public enum ProficiencyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    public static ProficiencyLevel fromString(String text) {
        if (text == null) return INTERMEDIATE;
        String cleaned = text.trim().toUpperCase();
        try {
            return ProficiencyLevel.valueOf(cleaned);
        } catch (IllegalArgumentException e) {
            return INTERMEDIATE;
        }
    }
}
