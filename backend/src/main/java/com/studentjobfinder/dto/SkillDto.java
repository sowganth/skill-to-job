package com.studentjobfinder.dto;

import com.studentjobfinder.entity.Skill;

public class SkillDto {

    private Long id;
    private String name;
    private String category;
    private String normalizedName;

    public SkillDto() {
    }

    public SkillDto(Skill skill) {
        if (skill != null) {
            this.id = skill.getId();
            this.name = skill.getName();
            this.category = skill.getCategory() != null ? skill.getCategory().name() : "TECHNICAL";
            this.normalizedName = skill.getNormalizedName();
        }
    }

    public SkillDto(Long id, String name, String category, String normalizedName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.normalizedName = normalizedName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }
}
