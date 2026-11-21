package com.studentjobfinder.dto;

import com.studentjobfinder.entity.LearningResource;

public class LearningResourceResponse {

    private Long id;
    private Long skillId;
    private String skill;
    private String title;
    private String provider;
    private String url;
    private String status;
    private String level;
    private String description;
    private boolean isSample;

    public LearningResourceResponse() {
    }

    public LearningResourceResponse(LearningResource lr) {
        if (lr != null) {
            this.id = lr.getId();
            if (lr.getSkill() != null) {
                this.skillId = lr.getSkill().getId();
                this.skill = lr.getSkill().getName();
            }
            this.title = lr.getTitle();
            this.provider = lr.getProvider();
            this.url = lr.getUrl();
            this.status = lr.getStatus();
            this.level = lr.getLevel();
            this.description = lr.getDescription();
            this.isSample = lr.isSample();
        }
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

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSample() {
        return isSample;
    }

    public void setSample(boolean sample) {
        isSample = sample;
    }
}
