package com.studentjobfinder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_resources", indexes = {
    @Index(name = "idx_resource_skill", columnList = "skill_id")
})
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 150)
    private String provider;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false, length = 50)
    private String status = "Free";

    @Column(nullable = false, length = 50)
    private String level = "Beginner";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_sample", nullable = false)
    private boolean isSample = false;

    public LearningResource() {
    }

    public LearningResource(Skill skill, String title, String provider, String url,
                            String status, String level, String description, boolean isSample) {
        this.skill = skill;
        this.title = title;
        this.provider = provider;
        this.url = url;
        this.status = status != null ? status : "Free";
        this.level = level != null ? level : "Beginner";
        this.description = description;
        this.isSample = isSample;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
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
