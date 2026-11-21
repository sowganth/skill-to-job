package com.studentjobfinder.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_normalized", columnList = "normalized_name")
})
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "normalized_name", nullable = false, unique = true, length = 100)
    private String normalizedName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SkillCategory category = SkillCategory.TECHNICAL;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningResource> learningResources = new ArrayList<>();

    public Skill() {
    }

    public Skill(String name, SkillCategory category) {
        this.name = name != null ? name.trim() : "";
        this.normalizedName = normalize(this.name);
        this.category = category != null ? category : SkillCategory.TECHNICAL;
    }

    public static String normalize(String skillName) {
        if (skillName == null) return "";
        return skillName.trim().toLowerCase();
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        if (this.name != null) {
            this.name = this.name.trim();
            this.normalizedName = normalize(this.name);
        }
    }

    // Getters and Setters
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
        this.normalizedName = normalize(name);
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public List<LearningResource> getLearningResources() {
        return learningResources;
    }

    public void setLearningResources(List<LearningResource> learningResources) {
        this.learningResources = learningResources;
    }
}
