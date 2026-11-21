package com.studentjobfinder.controller;

import com.studentjobfinder.dto.AddStudentSkillRequest;
import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.dto.StudentSkillResponse;
import com.studentjobfinder.dto.UpdateProfileRequest;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.service.AuthService;
import com.studentjobfinder.service.SkillService;
import com.studentjobfinder.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class StudentController {

    private final StudentService studentService;
    private final SkillService skillService;
    private final AuthService authService;

    public StudentController(StudentService studentService,
                             SkillService skillService,
                             AuthService authService) {
        this.studentService = studentService;
        this.skillService = skillService;
        this.authService = authService;
    }

    @GetMapping({"/api/students/me", "/api/students/profile"})
    public ResponseEntity<Map<String, Object>> getCurrentStudent() {
        StudentResponse student = studentService.getCurrentStudentProfile();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("student", student);
        return ResponseEntity.ok(response);
    }

    @PutMapping({"/api/students/me", "/api/students/profile"})
    public ResponseEntity<Map<String, Object>> updateCurrentStudent(
            @Valid @RequestBody UpdateProfileRequest request) {
        Student current = authService.getCurrentAuthenticatedStudent();
        StudentResponse updated = studentService.updateProfile(current.getId(), request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("student", updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/students/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse student = studentService.getProfile(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/api/students/me/skills")
    public ResponseEntity<List<StudentSkillResponse>> getMySkills() {
        Student current = authService.getCurrentAuthenticatedStudent();
        List<StudentSkillResponse> skills = skillService.getStudentSkills(current.getId());
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/api/students/me/skills")
    public ResponseEntity<StudentSkillResponse> addMySkill(
            @Valid @RequestBody AddStudentSkillRequest request) {
        Student current = authService.getCurrentAuthenticatedStudent();
        StudentSkillResponse response = skillService.addSkillToStudent(current.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/api/students/me/skills/{skillId}")
    public ResponseEntity<Void> deleteMySkill(@PathVariable Long skillId) {
        Student current = authService.getCurrentAuthenticatedStudent();
        skillService.removeSkillFromStudent(current.getId(), skillId);
        return ResponseEntity.noContent().build();
    }
}
