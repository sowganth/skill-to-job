package com.studentjobfinder.dto;

import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.JobSkill;
import com.studentjobfinder.entity.RequirementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobRoleResponse {

    private Long id;
    private String title;
    private String company;
    private String category;
    private String location;
    private String type;
    private String salaryRange;
    private String description;
    private List<String> responsibilities = new ArrayList<>();
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();

    public JobRoleResponse() {
    }

    public JobRoleResponse(JobRole jobRole) {
        if (jobRole != null) {
            this.id = jobRole.getId();
            this.title = jobRole.getTitle();
            this.company = jobRole.getCompany();
            this.category = jobRole.getCategory();
            this.location = jobRole.getLocation();
            this.type = jobRole.getType();
            this.salaryRange = jobRole.getSalaryRange();
            this.description = jobRole.getDescription();

            if (jobRole.getResponsibilities() != null && !jobRole.getResponsibilities().isBlank()) {
                String[] parts = jobRole.getResponsibilities().split("\n");
                for (String part : parts) {
                    String trimmed = part.trim().replaceFirst("^[-*•]\\s*", "");
                    if (!trimmed.isEmpty()) {
                        this.responsibilities.add(trimmed);
                    }
                }
            }

            if (jobRole.getJobSkills() != null) {
                for (JobSkill js : jobRole.getJobSkills()) {
                    if (js.getSkill() != null) {
                        if (js.getRequirementType() == RequirementType.REQUIRED) {
                            this.requiredSkills.add(js.getSkill().getName());
                        } else {
                            this.preferredSkills.add(js.getSkill().getName());
                        }
                    }
                }
            }
        }
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

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }
}
