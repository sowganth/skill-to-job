package com.studentjobfinder.controller;

import com.studentjobfinder.dto.CreateJobRequest;
import com.studentjobfinder.dto.JobRoleResponse;
import com.studentjobfinder.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final JobService jobService;

    public AdminJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobRoleResponse>> getAllJobsForAdmin() {
        return ResponseEntity.ok(jobService.getAllJobs(null, null));
    }

    @PostMapping
    public ResponseEntity<JobRoleResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        JobRoleResponse created = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobRoleResponse> updateJob(@PathVariable Long jobId,
                                                     @Valid @RequestBody CreateJobRequest request) {
        JobRoleResponse updated = jobService.updateJob(jobId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}
