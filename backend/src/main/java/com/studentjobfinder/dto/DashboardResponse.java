package com.studentjobfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {

    private StudentResponse student;
    private int totalSkillsCount;
    private int matchingJobsCount;
    private JobMatchResult topJobMatch;
    private List<JobMatchResult> recommendedJobs = new ArrayList<>();
    private List<StudentSkillResponse> recentSkills = new ArrayList<>();
    private List<BridgeSkillRecommendation> recommendedSkillsToLearn = new ArrayList<>();
    private int profileCompletionPercentage;

    public static class BridgeSkillRecommendation {
        private String skillName;
        private int targetRoleCount;
        private String category;

        public BridgeSkillRecommendation() {
        }

        public BridgeSkillRecommendation(String skillName, int targetRoleCount, String category) {
            this.skillName = skillName;
            this.targetRoleCount = targetRoleCount;
            this.category = category;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public int getTargetRoleCount() {
            return targetRoleCount;
        }

        public void setTargetRoleCount(int targetRoleCount) {
            this.targetRoleCount = targetRoleCount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public DashboardResponse() {
    }

    // Getters and Setters
    public StudentResponse getStudent() {
        return student;
    }

    public void setStudent(StudentResponse student) {
        this.student = student;
    }

    public int getTotalSkillsCount() {
        return totalSkillsCount;
    }

    public void setTotalSkillsCount(int totalSkillsCount) {
        this.totalSkillsCount = totalSkillsCount;
    }

    public int getMatchingJobsCount() {
        return matchingJobsCount;
    }

    public void setMatchingJobsCount(int matchingJobsCount) {
        this.matchingJobsCount = matchingJobsCount;
    }

    public JobMatchResult getTopJobMatch() {
        return topJobMatch;
    }

    public void setTopJobMatch(JobMatchResult topJobMatch) {
        this.topJobMatch = topJobMatch;
    }

    public List<JobMatchResult> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(List<JobMatchResult> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }

    public List<StudentSkillResponse> getRecentSkills() {
        return recentSkills;
    }

    public void setRecentSkills(List<StudentSkillResponse> recentSkills) {
        this.recentSkills = recentSkills;
    }

    public List<BridgeSkillRecommendation> getRecommendedSkillsToLearn() {
        return recommendedSkillsToLearn;
    }

    public void setRecommendedSkillsToLearn(List<BridgeSkillRecommendation> recommendedSkillsToLearn) {
        this.recommendedSkillsToLearn = recommendedSkillsToLearn;
    }

    public int getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(int profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }
}
