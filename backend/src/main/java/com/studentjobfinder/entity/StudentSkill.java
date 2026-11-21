package com.studentjobfinder.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_skills", uniqueConstraints = {
    @UniqueConstraint(name = "uq_student_skill", columnNames = {"student_id", "skill_id"})
})
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProficiencyLevel proficiency = ProficiencyLevel.INTERMEDIATE;

    @Column(name = "experience_duration", length = 50)
    private String experienceDuration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StudentSkill() {
    }

    public StudentSkill(Student student, Skill skill, ProficiencyLevel proficiency, String experienceDuration) {
        this.student = student;
        this.skill = skill;
        this.proficiency = proficiency != null ? proficiency : ProficiencyLevel.INTERMEDIATE;
        this.experienceDuration = experienceDuration;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getProficiency() {
        return proficiency;
    }

    public void setProficiency(ProficiencyLevel proficiency) {
        this.proficiency = proficiency;
    }

    public String getExperienceDuration() {
        return experienceDuration;
    }

    public void setExperienceDuration(String experienceDuration) {
        this.experienceDuration = experienceDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
