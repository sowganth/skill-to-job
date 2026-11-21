package com.studentjobfinder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_skills", uniqueConstraints = {
    @UniqueConstraint(name = "uq_job_skill", columnNames = {"job_role_id", "skill_id"})
})
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_role_id", nullable = false)
    private JobRole jobRole;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_type", nullable = false, length = 30)
    private RequirementType requirementType = RequirementType.REQUIRED;

    public JobSkill() {
    }

    public JobSkill(JobRole jobRole, Skill skill, RequirementType requirementType) {
        this.jobRole = jobRole;
        this.skill = skill;
        this.requirementType = requirementType != null ? requirementType : RequirementType.REQUIRED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
    }
}
