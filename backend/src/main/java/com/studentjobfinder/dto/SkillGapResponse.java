package com.studentjobfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillGapResponse {

    private Long jobId;
    private String jobTitle;
    private String company;
    private String category;
    private int matchPercentage;
    private List<String> requiredSkills = new ArrayList<>();
    private List<StudentSkillResponse> possessedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<PrioritizedSkill> prioritizedMissingSkills = new ArrayList<>();
    private List<LearningResourceResponse> learningResources = new ArrayList<>();
    private String disclaimer = "Academic Advisory: Readiness score indicates curriculum alignment and does not guarantee employment or hiring outcomes.";

    public static class PrioritizedSkill {
        private String skillName;
        private String priority; // HIGH, MEDIUM
        private String reason;

        public PrioritizedSkill() {
        }

        public PrioritizedSkill(String skillName, String priority, String reason) {
            this.skillName = skillName;
            this.priority = priority;
            this.reason = reason;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public SkillGapResponse() {
    }

    // Getters and Setters
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<StudentSkillResponse> getPossessedSkills() {
        return possessedSkills;
    }

    public void setPossessedSkills(List<StudentSkillResponse> possessedSkills) {
        this.possessedSkills = possessedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<PrioritizedSkill> getPrioritizedMissingSkills() {
        return prioritizedMissingSkills;
    }

    public void setPrioritizedMissingSkills(List<PrioritizedSkill> prioritizedMissingSkills) {
        this.prioritizedMissingSkills = prioritizedMissingSkills;
    }

    public List<LearningResourceResponse> getLearningResources() {
        return learningResources;
    }

    public void setLearningResources(List<LearningResourceResponse> learningResources) {
        this.learningResources = learningResources;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
