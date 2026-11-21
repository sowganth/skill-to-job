package com.studentjobfinder.dto;

import jakarta.validation.constraints.NotBlank;

public class AddStudentSkillRequest {

    private Long skillId;

    @NotBlank(message = "Skill name is required")
    private String name;

    private String category;

    @NotBlank(message = "Proficiency level is required (Beginner, Intermediate, Advanced)")
    private String proficiency;

    private String experience;

    public AddStudentSkillRequest() {
    }

    public AddStudentSkillRequest(String name, String category, String proficiency, String experience) {
        this.name = name;
        this.category = category;
        this.proficiency = proficiency;
        this.experience = experience;
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
