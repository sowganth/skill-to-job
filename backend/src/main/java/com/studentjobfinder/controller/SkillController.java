package com.studentjobfinder.controller;

import com.studentjobfinder.dto.AddStudentSkillRequest;
import com.studentjobfinder.dto.SkillDto;
import com.studentjobfinder.dto.StudentSkillResponse;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.service.AuthService;
import com.studentjobfinder.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final AuthService authService;

    public SkillController(SkillService skillService, AuthService authService) {
        this.skillService = skillService;
        this.authService = authService;
    }

    /**
     * List all catalogue skills or student skills depending on caller context
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSkills(
            @RequestParam(value = "scope", required = false) String scope) {
        Map<String, Object> response = new HashMap<>();

        // If authenticated and scope is not explicitly 'catalogue', return student's skills for frontend compatibility
        try {
            Student current = authService.getCurrentAuthenticatedStudent();
            if ("catalogue".equalsIgnoreCase(scope)) {
                List<SkillDto> catalogue = skillService.getAllAvailableSkills();
                response.put("success", true);
                response.put("skills", catalogue);
            } else {
                List<StudentSkillResponse> mySkills = skillService.getStudentSkills(current.getId());
                response.put("success", true);
                response.put("skills", mySkills);
            }
        } catch (Exception e) {
            // Unauthenticated: return global catalogue
            List<SkillDto> catalogue = skillService.getAllAvailableSkills();
            response.put("success", true);
            response.put("skills", catalogue);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/catalogue")
    public ResponseEntity<List<SkillDto>> getAllAvailableSkills() {
        return ResponseEntity.ok(skillService.getAllAvailableSkills());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addSkill(
            @Valid @RequestBody AddStudentSkillRequest request) {
        Student current = authService.getCurrentAuthenticatedStudent();
        StudentSkillResponse added = skillService.addSkillToStudent(current.getId(), request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Skill successfully added");
        response.put("skill", added);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable Long id) {
        Student current = authService.getCurrentAuthenticatedStudent();
        skillService.removeSkillFromStudent(current.getId(), id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Skill removed");
        return ResponseEntity.ok(response);
    }
}
