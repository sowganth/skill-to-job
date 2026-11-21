package com.studentjobfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class JobMatchResult {

    private Long id;
    private String title;
    private String company;
    private String category;
    private String location;
    private String type;
    private String salaryRange;
    private String description;
    private int matchPercentage;
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> preferredSkillsMatched = new ArrayList<>();
    private int totalRequiredSkills;
    private String disclaimer = "The match score represents automated curriculum skill-overlap only and is not a guarantee of job qualification or employment.";

    public JobMatchResult() {
    }

    public JobMatchResult(Long id, String title, String company, String category,
                          String location, String type, String salaryRange, String description,
                          int matchPercentage, List<String> requiredSkills, List<String> matchedSkills,
                          List<String> missingSkills, List<String> preferredSkillsMatched,
                          int totalRequiredSkills) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.category = category;
        this.location = location;
        this.type = type;
        this.salaryRange = salaryRange;
        this.description = description;
        this.matchPercentage = matchPercentage;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
        this.preferredSkillsMatched = preferredSkillsMatched != null ? preferredSkillsMatched : new ArrayList<>();
        this.totalRequiredSkills = totalRequiredSkills;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getPreferredSkillsMatched() {
        return preferredSkillsMatched;
    }

    public void setPreferredSkillsMatched(List<String> preferredSkillsMatched) {
        this.preferredSkillsMatched = preferredSkillsMatched;
    }

    public int getTotalRequiredSkills() {
        return totalRequiredSkills;
    }

    public void setTotalRequiredSkills(int totalRequiredSkills) {
        this.totalRequiredSkills = totalRequiredSkills;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
