package com.studentjobfinder.service;

import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.dto.LearningResourceResponse;
import com.studentjobfinder.dto.SkillGapResponse;
import com.studentjobfinder.entity.*;
import com.studentjobfinder.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillGapServiceTest {

    @Mock
    private JobService jobService;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private JobMatcherService jobMatcherService;

    @Mock
    private LearningResourceService learningResourceService;

    @Mock
    private AuthService authService;

    private SkillGapService skillGapService;

    @BeforeEach
    void setUp() {
        skillGapService = new SkillGapService(
                jobService, studentSkillRepository, jobMatcherService,
                learningResourceService, authService
        );
    }

    @Test
    @DisplayName("Should analyze skill gap, prioritize missing skills, and attach learning resources")
    void testAnalyzeSkillGap() {
        Long studentId = 1L;
        Long jobId = 100L;

        JobRole job = new JobRole("Backend Dev", "Nexus", "Backend", "City", "FT", "₹6 LPA", "Desc", "Resp");
        job.setId(jobId);

        Student student = new Student();
        student.setId(studentId);

        Skill java = new Skill("Java", SkillCategory.TECHNICAL);
        Skill docker = new Skill("Docker", SkillCategory.TECHNICAL);

        StudentSkill studentSkill = new StudentSkill(student, java, ProficiencyLevel.ADVANCED, "1 Year");
        List<StudentSkill> studentSkills = Collections.singletonList(studentSkill);

        when(jobService.getJobEntityById(jobId)).thenReturn(job);
        when(studentSkillRepository.findByStudentIdWithSkill(studentId)).thenReturn(studentSkills);

        JobMatchResult matchResult = new JobMatchResult(
                jobId, "Backend Dev", "Nexus", "Backend", "City", "FT", "₹6 LPA", "Desc",
                50,
                Arrays.asList("Java", "Docker"),
                Collections.singletonList("Java"),
                Collections.singletonList("Docker"),
                Collections.emptyList(),
                2
        );

        when(jobMatcherService.evaluateJobMatch(job, studentSkills)).thenReturn(matchResult);

        LearningResource lr = new LearningResource(docker, "Docker Basics", "Docker Docs",
                "https://docs.docker.com", "Free", "Beginner", "Learn docker", false);
        LearningResourceResponse lrDto = new LearningResourceResponse(lr);

        when(learningResourceService.getResourcesForMissingSkills(Collections.singletonList("Docker")))
                .thenReturn(Collections.singletonList(lrDto));

        SkillGapResponse response = skillGapService.analyzeGap(studentId, jobId);

        assertNotNull(response);
        assertEquals(jobId, response.getJobId());
        assertEquals(50, response.getMatchPercentage());
        assertEquals(1, response.getPossessedSkills().size());
        assertEquals("Java", response.getPossessedSkills().get(0).getName());

        assertEquals(1, response.getMissingSkills().size());
        assertEquals("Docker", response.getMissingSkills().get(0));

        // Prioritized list
        assertEquals(1, response.getPrioritizedMissingSkills().size());
        assertEquals("Docker", response.getPrioritizedMissingSkills().get(0).getSkillName());
        assertEquals("HIGH", response.getPrioritizedMissingSkills().get(0).getPriority());

        // Resources attached
        assertEquals(1, response.getLearningResources().size());
        assertEquals("Docker Basics", response.getLearningResources().get(0).getTitle());
    }
}
