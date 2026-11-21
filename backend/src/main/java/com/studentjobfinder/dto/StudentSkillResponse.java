package com.studentjobfinder.dto;

import com.studentjobfinder.entity.StudentSkill;

public class StudentSkillResponse {

    private Long id;
    private Long skillId;
    private String name;
    private String category;
    private String proficiency;
    private String experience;

    public StudentSkillResponse() {
    }

    public StudentSkillResponse(StudentSkill ss) {
        if (ss != null) {
            this.id = ss.getId();
            if (ss.getSkill() != null) {
                this.skillId = ss.getSkill().getId();
                this.name = ss.getSkill().getName();
                this.category = ss.getSkill().getCategory() != null ? ss.getSkill().getCategory().name() : "TECHNICAL";
            }
            this.proficiency = ss.getProficiency() != null ? capitalize(ss.getProficiency().name()) : "Intermediate";
            this.experience = ss.getExperienceDuration();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
