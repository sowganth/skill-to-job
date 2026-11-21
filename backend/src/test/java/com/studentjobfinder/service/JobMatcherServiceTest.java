package com.studentjobfinder.service;

import com.studentjobfinder.dto.JobMatchResult;
import com.studentjobfinder.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobMatcherServiceTest {

    private JobMatcherService jobMatcherService;

    @BeforeEach
    void setUp() {
        jobMatcherService = new JobMatcherService();
    }

    @Test
    @DisplayName("Should correctly calculate match percentage and separate matched vs missing skills")
    void testExactMatchCalculation() {
        // Job requires: Java, Spring Boot, SQL, Git (4 skills)
        JobRole job = new JobRole("Junior Java Dev", "Acme", "Backend", "Remote", "Full-Time", "₹6 LPA", "Desc", "Resp");
        Skill java = new Skill("Java", SkillCategory.TECHNICAL);
        Skill spring = new Skill("Spring Boot", SkillCategory.TECHNICAL);
        Skill sql = new Skill("SQL", SkillCategory.TECHNICAL);
        Skill git = new Skill("Git", SkillCategory.TECHNICAL);

        job.addSkill(java, RequirementType.REQUIRED);
        job.addSkill(spring, RequirementType.REQUIRED);
        job.addSkill(sql, RequirementType.REQUIRED);
        job.addSkill(git, RequirementType.REQUIRED);

        // Student has: Java, SQL (2 of 4)
        Student student = new Student();
        List<StudentSkill> studentSkills = Arrays.asList(
                new StudentSkill(student, java, ProficiencyLevel.ADVANCED, "1 Year"),
                new StudentSkill(student, sql, ProficiencyLevel.INTERMEDIATE, "6 Months")
        );

        JobMatchResult result = jobMatcherService.evaluateJobMatch(job, studentSkills);

        // 2 / 4 * 100 = 50%
        assertEquals(50, result.getMatchPercentage());
        assertEquals(2, result.getMatchedSkills().size());
        assertTrue(result.getMatchedSkills().contains("Java"));
        assertTrue(result.getMatchedSkills().contains("SQL"));

        // Missing skills
        assertEquals(2, result.getMissingSkills().size());
        assertTrue(result.getMissingSkills().contains("Spring Boot"));
        assertTrue(result.getMissingSkills().contains("Git"));
    }

    @Test
    @DisplayName("Should safely handle job with zero required skills and return 100%")
    void testEmptyRequiredSkillsHandling() {
        JobRole job = new JobRole("Trainee", "TechCorp", "General", "Remote", "Internship", "Stipend", "Desc", "Resp");
        // No skills attached

        Student student = new Student();
        List<StudentSkill> studentSkills = Collections.emptyList();

        JobMatchResult result = jobMatcherService.evaluateJobMatch(job, studentSkills);

        assertEquals(100, result.getMatchPercentage());
        assertEquals(0, result.getTotalRequiredSkills());
        assertTrue(result.getMatchedSkills().isEmpty());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    @Test
    @DisplayName("Should match skills ignoring case and leading/trailing whitespace")
    void testSkillNormalizationMatching() {
        JobRole job = new JobRole("Developer", "Corp", "Backend", "City", "FT", "Pay", "Desc", "Resp");
        Skill req1 = new Skill("  jAvA  ", SkillCategory.TECHNICAL);
        Skill req2 = new Skill("spring boot", SkillCategory.TECHNICAL);
        job.addSkill(req1, RequirementType.REQUIRED);
        job.addSkill(req2, RequirementType.REQUIRED);

        Student student = new Student();
        // Student skill has different casing and spaces
        Skill stuSkill1 = new Skill("JAVA", SkillCategory.TECHNICAL);
        Skill stuSkill2 = new Skill("  Spring Boot  ", SkillCategory.TECHNICAL);

        List<StudentSkill> studentSkills = Arrays.asList(
                new StudentSkill(student, stuSkill1, ProficiencyLevel.ADVANCED, "1 Year"),
                new StudentSkill(student, stuSkill2, ProficiencyLevel.INTERMEDIATE, "1 Year")
        );

        JobMatchResult result = jobMatcherService.evaluateJobMatch(job, studentSkills);

        assertEquals(100, result.getMatchPercentage());
        assertEquals(2, result.getMatchedSkills().size());
        assertEquals(0, result.getMissingSkills().size());
    }

    @Test
    @DisplayName("Should NOT mix preferred skills into primary required match score")
    void testPreferredSkillsExcludedFromRequiredScore() {
        JobRole job = new JobRole("Full Stack Dev", "Corp", "Full Stack", "City", "FT", "Pay", "Desc", "Resp");
        Skill reqJava = new Skill("Java", SkillCategory.TECHNICAL);
        Skill prefDocker = new Skill("Docker", SkillCategory.TECHNICAL);

        job.addSkill(reqJava, RequirementType.REQUIRED);
        job.addSkill(prefDocker, RequirementType.PREFERRED);

        // Student has only preferred skill (Docker), but missing required (Java)
        Student student = new Student();
        List<StudentSkill> studentSkills = Collections.singletonList(
                new StudentSkill(student, prefDocker, ProficiencyLevel.BEGINNER, "< 6 Months")
        );

        JobMatchResult result = jobMatcherService.evaluateJobMatch(job, studentSkills);

        // Required match score is 0 / 1 = 0%
        assertEquals(0, result.getMatchPercentage());
        assertEquals(1, result.getMissingSkills().size());
        assertTrue(result.getMissingSkills().contains("Java"));

        // Preferred skills matched should be recorded separately
        assertEquals(1, result.getPreferredSkillsMatched().size());
        assertTrue(result.getPreferredSkillsMatched().contains("Docker"));
    }

    @Test
    @DisplayName("Should sort jobs descending by match percentage")
    void testRankingSortOrder() {
        JobRole highMatchJob = new JobRole("Backend Dev", "A", "Backend", "C", "F", "P", "D", "R");
        Skill s1 = new Skill("Java", SkillCategory.TECHNICAL);
        highMatchJob.addSkill(s1, RequirementType.REQUIRED);

        JobRole lowMatchJob = new JobRole("Data Scientist", "B", "Data", "C", "F", "P", "D", "R");
        Skill s2 = new Skill("Python", SkillCategory.TECHNICAL);
        Skill s3 = new Skill("Pandas", SkillCategory.TECHNICAL);
        lowMatchJob.addSkill(s2, RequirementType.REQUIRED);
        lowMatchJob.addSkill(s3, RequirementType.REQUIRED);

        Student student = new Student();
        List<StudentSkill> studentSkills = Collections.singletonList(
                new StudentSkill(student, s1, ProficiencyLevel.ADVANCED, "1 Year")
        );

        List<JobMatchResult> ranked = jobMatcherService.evaluateAndRankJobs(
                Arrays.asList(lowMatchJob, highMatchJob), studentSkills);

        assertEquals(2, ranked.size());
        // First should be highMatchJob with 100%
        assertEquals("Backend Dev", ranked.get(0).getTitle());
        assertEquals(100, ranked.get(0).getMatchPercentage());

        // Second should be lowMatchJob with 0%
        assertEquals("Data Scientist", ranked.get(1).getTitle());
        assertEquals(0, ranked.get(1).getMatchPercentage());
    }
}
