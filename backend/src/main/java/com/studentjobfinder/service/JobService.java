package com.studentjobfinder.service;

import com.studentjobfinder.dto.CreateJobRequest;
import com.studentjobfinder.dto.JobRoleResponse;
import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.JobSkill;
import com.studentjobfinder.entity.RequirementType;
import com.studentjobfinder.entity.Skill;
import com.studentjobfinder.entity.SkillCategory;
import com.studentjobfinder.exception.ResourceNotFoundException;
import com.studentjobfinder.repository.JobRoleRepository;
import com.studentjobfinder.repository.JobSkillRepository;
import com.studentjobfinder.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRoleRepository jobRoleRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRoleRepository jobRoleRepository,
                      JobSkillRepository jobSkillRepository,
                      SkillRepository skillRepository) {
        this.jobRoleRepository = jobRoleRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<JobRoleResponse> getAllJobs(String category, String keyword) {
        List<JobRole> list;
        if (keyword != null && !keyword.isBlank()) {
            list = jobRoleRepository.searchJobs(keyword.trim());
        } else if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            list = jobRoleRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            list = jobRoleRepository.findAllWithSkills();
        }

        return list.stream().map(JobRoleResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobRoleResponse getJobById(Long id) {
        JobRole jobRole = jobRoleRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job role with ID " + id + " not found"));
        return new JobRoleResponse(jobRole);
    }

    @Transactional(readOnly = true)
    public JobRole getJobEntityById(Long id) {
        return jobRoleRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job role with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<JobRole> getAllJobEntities() {
        return jobRoleRepository.findAllWithSkills();
    }

    @Transactional
    public JobRoleResponse createJob(CreateJobRequest request) {
        JobRole jobRole = new JobRole(
                request.getTitle().trim(),
                request.getCompany().trim(),
                request.getCategory().trim(),
                request.getLocation() != null ? request.getLocation().trim() : "Hybrid",
                request.getType() != null ? request.getType().trim() : "Full-Time",
                request.getSalaryRange() != null ? request.getSalaryRange().trim() : "Competitive",
                request.getDescription().trim(),
                request.getResponsibilities()
        );

        JobRole saved = jobRoleRepository.save(jobRole);

        // Attach required skills
        if (request.getRequiredSkills() != null) {
            for (String skillName : request.getRequiredSkills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = findOrCreateSkill(skillName.trim());
                    jobRole.addSkill(skill, RequirementType.REQUIRED);
                }
            }
        }

        // Attach preferred skills
        if (request.getPreferredSkills() != null) {
            for (String skillName : request.getPreferredSkills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = findOrCreateSkill(skillName.trim());
                    jobRole.addSkill(skill, RequirementType.PREFERRED);
                }
            }
        }

        saved = jobRoleRepository.save(jobRole);
        return new JobRoleResponse(saved);
    }

    @Transactional
    public JobRoleResponse updateJob(Long id, CreateJobRequest request) {
        JobRole jobRole = jobRoleRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job role with ID " + id + " not found"));

        jobRole.setTitle(request.getTitle().trim());
        jobRole.setCompany(request.getCompany().trim());
        jobRole.setCategory(request.getCategory().trim());
        if (request.getLocation() != null) jobRole.setLocation(request.getLocation().trim());
        if (request.getType() != null) jobRole.setType(request.getType().trim());
        if (request.getSalaryRange() != null) jobRole.setSalaryRange(request.getSalaryRange().trim());
        jobRole.setDescription(request.getDescription().trim());
        if (request.getResponsibilities() != null) jobRole.setResponsibilities(request.getResponsibilities());

        // Clear existing job skills and re-populate
        jobRole.getJobSkills().clear();

        if (request.getRequiredSkills() != null) {
            for (String skillName : request.getRequiredSkills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = findOrCreateSkill(skillName.trim());
                    jobRole.addSkill(skill, RequirementType.REQUIRED);
                }
            }
        }

        if (request.getPreferredSkills() != null) {
            for (String skillName : request.getPreferredSkills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = findOrCreateSkill(skillName.trim());
                    jobRole.addSkill(skill, RequirementType.PREFERRED);
                }
            }
        }

        JobRole updated = jobRoleRepository.save(jobRole);
        return new JobRoleResponse(updated);
    }

    @Transactional
    public void deleteJob(Long id) {
        JobRole jobRole = jobRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job role with ID " + id + " not found"));
        jobRoleRepository.delete(jobRole);
    }

    private Skill findOrCreateSkill(String name) {
        String normalized = Skill.normalize(name);
        return skillRepository.findByNormalizedName(normalized)
                .orElseGet(() -> skillRepository.save(new Skill(name, SkillCategory.TECHNICAL)));
    }
}
