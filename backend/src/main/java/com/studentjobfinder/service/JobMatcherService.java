package com.studentjobfinder.service;

import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.JobSkill;
import com.studentjobfinder.entity.RequirementType;
import com.studentjobfinder.entity.Skill;
import com.studentjobfinder.entity.StudentSkill;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating mathematical skills overlap between a candidate student
 * and prospective job vacancies.
 *
 * Evaluation Formula:
 * matchPercentage = (matchedRequiredSkills / totalRequiredSkills) * 100
 *
 * Note: The match percentage is a quantitative skills-overlap indicator designed for academic
 * orientation and does not guarantee job eligibility, interview callbacks, or hiring outcomes.
 */
@Service
public class JobMatcherService {

    /**
     * Compute match evaluation for a single job against student skills.
     */
    public JobMatchResult evaluateJobMatch(JobRole jobRole, List<StudentSkill> studentSkills) {
        if (jobRole == null) {
            throw new IllegalArgumentException("JobRole cannot be null");
        }

        // Normalize student skill names
        Set<String> studentNormalizedSkills = (studentSkills != null ? studentSkills : Collections.<StudentSkill>emptyList())
                .stream()
                .filter(ss -> ss != null && ss.getSkill() != null)
                .map(ss -> normalizeSkillName(ss.getSkill().getName()))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<String> requiredSkillNames = new ArrayList<>();
        List<String> matchedRequired = new ArrayList<>();
        List<String> missingRequired = new ArrayList<>();
        List<String> matchedPreferred = new ArrayList<>();

        if (jobRole.getJobSkills() != null) {
            for (JobSkill js : jobRole.getJobSkills()) {
                if (js == null || js.getSkill() == null) continue;

                String originalName = js.getSkill().getName();
                String normalized = normalizeSkillName(originalName);

                if (js.getRequirementType() == RequirementType.REQUIRED) {
                    requiredSkillNames.add(originalName);
                    if (studentNormalizedSkills.contains(normalized)) {
                        matchedRequired.add(originalName);
                    } else {
                        missingRequired.add(originalName);
                    }
                } else if (js.getRequirementType() == RequirementType.PREFERRED) {
                    if (studentNormalizedSkills.contains(normalized)) {
                        matchedPreferred.add(originalName);
                    }
                }
            }
        }

        int totalRequired = requiredSkillNames.size();
        int matchPercentage;

        // Safe handling of jobs with zero required skills
        if (totalRequired == 0) {
            matchPercentage = 100;
        } else {
            // Strictly calculate percentage based on required skills without mixing preferred skills
            matchPercentage = Math.round(((float) matchedRequired.size() / totalRequired) * 100);
        }

        return new JobMatchResult(
                jobRole.getId(),
                jobRole.getTitle(),
                jobRole.getCompany(),
                jobRole.getCategory(),
                jobRole.getLocation(),
                jobRole.getType(),
                jobRole.getSalaryRange(),
                jobRole.getDescription(),
                matchPercentage,
                requiredSkillNames,
                matchedRequired,
                missingRequired,
                matchedPreferred,
                totalRequired
        );
    }

    /**
     * Evaluate a list of jobs, sorting the resulting matches descending by match percentage.
     */
    public List<JobMatchResult> evaluateAndRankJobs(List<JobRole> jobs, List<StudentSkill> studentSkills) {
        if (jobs == null) {
            return Collections.emptyList();
        }

        List<JobMatchResult> results = new ArrayList<>();
        for (JobRole job : jobs) {
            results.add(evaluateJobMatch(job, studentSkills));
        }

        // Sort by match percentage descending, then title alphabetically
        results.sort(Comparator
                .comparingInt(JobMatchResult::getMatchPercentage).reversed()
                .thenComparing(JobMatchResult::getTitle, String.CASE_INSENSITIVE_ORDER));

        return results;
    }

    /**
     * Consistent normalization for skill matching: ignores case and surrounding whitespace.
     */
    public String normalizeSkillName(String name) {
        return Skill.normalize(name);
    }
}
