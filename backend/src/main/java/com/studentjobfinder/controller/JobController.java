package com.studentjobfinder.controller;

import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.dto.JobRoleResponse;
import com.studentjobfinder.dto.SkillGapResponse;
import com.studentjobfinder.entity.JobRole;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.entity.StudentSkill;
import com.studentjobfinder.repository.StudentSkillRepository;
import com.studentjobfinder.service.AuthService;
import com.studentjobfinder.service.JobMatcherService;
import com.studentjobfinder.service.JobService;
import com.studentjobfinder.service.SkillGapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final JobMatcherService jobMatcherService;
    private final SkillGapService skillGapService;
    private final StudentSkillRepository studentSkillRepository;
    private final AuthService authService;

    public JobController(JobService jobService,
                         JobMatcherService jobMatcherService,
                         SkillGapService skillGapService,
                         StudentSkillRepository studentSkillRepository,
                         AuthService authService) {
        this.jobService = jobService;
        this.jobMatcherService = jobMatcherService;
        this.skillGapService = skillGapService;
        this.studentSkillRepository = studentSkillRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        List<JobRoleResponse> jobs = jobService.getAllJobs(category, keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("jobs", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobById(@PathVariable Long jobId) {
        JobRoleResponse job = jobService.getJobById(jobId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("job", job);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<JobMatchResult>> getJobRecommendations() {
        Student current = authService.getCurrentAuthenticatedStudent();
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentIdWithSkill(current.getId());
        List<JobRole> allJobs = jobService.getAllJobEntities();

        List<JobMatchResult> ranked = jobMatcherService.evaluateAndRankJobs(allJobs, studentSkills);
        return ResponseEntity.ok(ranked);
    }

    @GetMapping("/{jobId}/skill-gap")
    public ResponseEntity<SkillGapResponse> getSkillGap(@PathVariable Long jobId) {
        SkillGapResponse gap = skillGapService.analyzeGapForCurrentStudent(jobId);
        return ResponseEntity.ok(gap);
    }
}
