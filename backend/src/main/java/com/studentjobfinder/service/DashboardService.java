package com.studentjobfinder.service;

import com.studentjobfinder.dto.DashboardResponse;
import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.dto.StudentSkillResponse;
import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.entity.StudentSkill;
import com.studentjobfinder.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AuthService authService;
    private final StudentSkillRepository studentSkillRepository;
    private final JobService jobService;
    private final JobMatcherService jobMatcherService;

    public DashboardService(AuthService authService,
                            StudentSkillRepository studentSkillRepository,
                            JobService jobService,
                            JobMatcherService jobMatcherService) {
        this.authService = authService;
        this.studentSkillRepository = studentSkillRepository;
        this.jobService = jobService;
        this.jobMatcherService = jobMatcherService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        Student current = authService.getCurrentAuthenticatedStudent();
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentIdWithSkill(current.getId());
        List<JobRole> allJobs = jobService.getAllJobEntities();

        // 1. Evaluate job matches
        List<JobMatchResult> rankedJobs = jobMatcherService.evaluateAndRankJobs(allJobs, studentSkills);

        DashboardResponse response = new DashboardResponse();
        response.setStudent(new StudentResponse(current));
        response.setTotalSkillsCount(studentSkills.size());

        // Count matching jobs with >= 60% match
        long matchingCount = rankedJobs.stream().filter(j -> j.getMatchPercentage() >= 60).count();
        response.setMatchingJobsCount((int) matchingCount);

        if (!rankedJobs.isEmpty()) {
            response.setTopJobMatch(rankedJobs.get(0));
            // Top 3 recommended
            response.setRecommendedJobs(rankedJobs.stream().limit(3).collect(Collectors.toList()));
        }

        // Recent skills (up to 6)
        List<StudentSkillResponse> recent = studentSkills.stream()
                .sorted(Comparator.comparing(StudentSkill::getId).reversed())
                .limit(6)
                .map(StudentSkillResponse::new)
                .collect(Collectors.toList());
        response.setRecentSkills(recent);

        // Identify high-impact bridge skills (missing skills across top 4 candidates)
        Map<String, Integer> missingFrequency = new HashMap<>();
        rankedJobs.stream().limit(4).forEach(job -> {
            for (String missing : job.getMissingSkills()) {
                missingFrequency.put(missing, missingFrequency.getOrDefault(missing, 0) + 1);
            }
        });

        List<DashboardResponse.BridgeSkillRecommendation> bridgeSkills = missingFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(4)
                .map(entry -> new DashboardResponse.BridgeSkillRecommendation(entry.getKey(), entry.getValue(), "Bridge Gap"))
                .collect(Collectors.toList());
        response.setRecommendedSkillsToLearn(bridgeSkills);

        // Profile completion percentage
        int score = 30; // base registered account
        if (current.getStudentId() != null && !current.getStudentId().isBlank()) score += 10;
        if (current.getDepartment() != null && !current.getDepartment().isBlank()) score += 10;
        if (current.getYearOfStudy() != null && !current.getYearOfStudy().isBlank()) score += 10;
        if (current.getCollege() != null && !current.getCollege().isBlank()) score += 10;
        if (current.getBio() != null && !current.getBio().isBlank()) score += 10;
        if (studentSkills.size() >= 3) score += 10;
        if (studentSkills.size() >= 6) score += 10;
        response.setProfileCompletionPercentage(Math.min(score, 100));

        return response;
    }
}
