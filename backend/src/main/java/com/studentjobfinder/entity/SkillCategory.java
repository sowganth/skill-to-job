package com.studentjobfinder.entity;

public enum SkillCategory {
    TECHNICAL,
    SOFT_SKILL;

    public static SkillCategory fromString(String text) {
        if (text == null) return TECHNICAL;
        String cleaned = text.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        if (cleaned.contains("SOFT")) return SOFT_SKILL;
        return TECHNICAL;
    }
}
