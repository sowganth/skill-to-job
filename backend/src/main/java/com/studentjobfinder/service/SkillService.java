package com.studentjobfinder.service;

import com.studentjobfinder.dto.AddStudentSkillRequest;
import com.studentjobfinder.dto.SkillDto;
import com.studentjobfinder.dto.StudentSkillResponse;
import com.studentjobfinder.entity.ProficiencyLevel;
import com.studentjobfinder.entity.Skill;
import com.studentjobfinder.entity.SkillCategory;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.entity.StudentSkill;
import com.studentjobfinder.exception.DuplicateResourceException;
import com.studentjobfinder.exception.ResourceNotFoundException;
import com.studentjobfinder.repository.SkillRepository;
import com.studentjobfinder.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final AuthService authService;

    public SkillService(SkillRepository skillRepository,
                        StudentSkillRepository studentSkillRepository,
                        AuthService authService) {
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getAllAvailableSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc()
                .stream()
                .map(SkillDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getStudentSkills(Long studentId) {
        Student current = authService.getCurrentAuthenticatedStudent();
        if (!current.getId().equals(studentId) && !"ROLE_ADMIN".equals(current.getRole().name())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another student's skills.");
        }

        return studentSkillRepository.findByStudentIdWithSkill(studentId)
                .stream()
                .map(StudentSkillResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentSkillResponse addSkillToStudent(Long studentId, AddStudentSkillRequest request) {
        Student current = authService.getCurrentAuthenticatedStudent();
        if (!current.getId().equals(studentId) && !"ROLE_ADMIN".equals(current.getRole().name())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to modify another student's skills.");
        }

        String rawName = request.getName().trim();
        String normalized = Skill.normalize(rawName);

        // Find or create skill in global catalogue
        Skill skill = skillRepository.findByNormalizedName(normalized).orElseGet(() -> {
            SkillCategory cat = SkillCategory.fromString(request.getCategory());
            return skillRepository.save(new Skill(rawName, cat));
        });

        // Check if student already has this skill (Duplicate prevention)
        if (studentSkillRepository.existsByStudentIdAndSkillId(studentId, skill.getId())) {
            throw new DuplicateResourceException("Skill '" + skill.getName() + "' is already assigned to your profile.");
        }

        ProficiencyLevel proficiency = ProficiencyLevel.fromString(request.getProficiency());
        String experience = request.getExperience() != null ? request.getExperience().trim() : "6 - 12 Months";

        StudentSkill studentSkill = new StudentSkill(current, skill, proficiency, experience);
        StudentSkill saved = studentSkillRepository.save(studentSkill);

        return new StudentSkillResponse(saved);
    }

    @Transactional
    public void removeSkillFromStudent(Long studentId, Long skillId) {
        Student current = authService.getCurrentAuthenticatedStudent();
        if (!current.getId().equals(studentId) && !"ROLE_ADMIN".equals(current.getRole().name())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to modify another student's skills.");
        }

        StudentSkill studentSkill = studentSkillRepository.findByStudentIdAndSkillId(studentId, skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill assignment not found for ID: " + skillId));

        studentSkillRepository.delete(studentSkill);
    }
}
