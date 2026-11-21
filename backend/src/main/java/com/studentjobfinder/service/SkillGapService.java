package com.studentjobfinder.service;

import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.dto.LearningResourceResponse;
import com.studentjobfinder.dto.SkillGapResponse;
import com.studentjobfinder.dto.StudentSkillResponse;
import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.entity.StudentSkill;
import com.studentjobfinder.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillGapService {

    private final JobService jobService;
    private final StudentSkillRepository studentSkillRepository;
    private final JobMatcherService jobMatcherService;
    private final LearningResourceService learningResourceService;
    private final AuthService authService;

    public SkillGapService(JobService jobService,
                           StudentSkillRepository studentSkillRepository,
                           JobMatcherService jobMatcherService,
                           LearningResourceService learningResourceService,
                           AuthService authService) {
        this.jobService = jobService;
        this.studentSkillRepository = studentSkillRepository;
        this.jobMatcherService = jobMatcherService;
        this.learningResourceService = learningResourceService;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public SkillGapResponse analyzeGapForCurrentStudent(Long jobId) {
        Student current = authService.getCurrentAuthenticatedStudent();
        return analyzeGap(current.getId(), jobId);
    }

    @Transactional(readOnly = true)
    public SkillGapResponse analyzeGap(Long studentId, Long jobId) {
        JobRole jobRole = jobService.getJobEntityById(jobId);
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentIdWithSkill(studentId);

        JobMatchResult match = jobMatcherService.evaluateJobMatch(jobRole, studentSkills);

        SkillGapResponse response = new SkillGapResponse();
        response.setJobId(jobRole.getId());
        response.setJobTitle(jobRole.getTitle());
        response.setCompany(jobRole.getCompany());
        response.setCategory(jobRole.getCategory());
        response.setMatchPercentage(match.getMatchPercentage());
        response.setRequiredSkills(match.getRequiredSkills());
        response.setMissingSkills(match.getMissingSkills());

        // Map possessed skills with student proficiency
        List<StudentSkillResponse> possessed = studentSkills.stream()
                .filter(ss -> ss.getSkill() != null && match.getMatchedSkills().contains(ss.getSkill().getName()))
                .map(StudentSkillResponse::new)
                .collect(Collectors.toList());
        response.setPossessedSkills(possessed);

        // Assign Suggested Priority for missing skills (First 2 are HIGH, remaining are MEDIUM)
        List<SkillGapResponse.PrioritizedSkill> prioritized = new ArrayList<>();
        List<String> missing = match.getMissingSkills();
        for (int i = 0; i < missing.size(); i++) {
            String skillName = missing.get(i);
            String priority = (i < 2) ? "HIGH" : "MEDIUM";
            String reason = (i < 2)
                    ? "Core foundational competency essential for technical screening"
                    : "Important auxiliary requirement to enhance overall placement qualification";
            prioritized.add(new SkillGapResponse.PrioritizedSkill(skillName, priority, reason));
        }
        response.setPrioritizedMissingSkills(prioritized);

        // Fetch verified learning resources for missing skills
        List<LearningResourceResponse> resources = learningResourceService.getResourcesForMissingSkills(missing);
        response.setLearningResources(resources);

        return response;
    }
}
