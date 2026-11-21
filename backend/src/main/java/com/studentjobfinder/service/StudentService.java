package com.studentjobfinder.service;

import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.dto.UpdateProfileRequest;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.exception.DuplicateResourceException;
import com.studentjobfinder.exception.ResourceNotFoundException;
import com.studentjobfinder.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AuthService authService;

    public StudentService(StudentRepository studentRepository, AuthService authService) {
        this.studentRepository = studentRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public StudentResponse getProfile(Long studentId) {
        Student current = authService.getCurrentAuthenticatedStudent();

        // Enforce student data isolation: students can only view their own profile unless admin
        if (!current.getId().equals(studentId) && !"ROLE_ADMIN".equals(current.getRole().name())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another student's profile.");
        }

        Student target = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found"));

        return new StudentResponse(target);
    }

    @Transactional(readOnly = true)
    public StudentResponse getCurrentStudentProfile() {
        Student current = authService.getCurrentAuthenticatedStudent();
        return new StudentResponse(current);
    }

    @Transactional
    public StudentResponse updateProfile(Long studentId, UpdateProfileRequest request) {
        Student current = authService.getCurrentAuthenticatedStudent();

        // Enforce student data isolation: students cannot modify another student's profile
        if (!current.getId().equals(studentId) && !"ROLE_ADMIN".equals(current.getRole().name())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to modify another student's profile.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            student.setName(request.getName().trim());
        }

        if (request.getCollege() != null && !request.getCollege().isBlank()) {
            student.setCollege(request.getCollege().trim());
        }

        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            student.setDepartment(request.getDepartment().trim());
        }

        if (request.getYear() != null && !request.getYear().isBlank()) {
            student.setYearOfStudy(request.getYear().trim());
        }

        if (request.getCareerInterests() != null) {
            student.setCareerInterests(request.getCareerInterests().trim());
        }

        if (request.getBio() != null) {
            student.setBio(request.getBio().trim());
        }

        // Email check if changed
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(student.getEmail())) {
                if (studentRepository.existsByEmail(newEmail)) {
                    throw new DuplicateResourceException("Email " + newEmail + " is already in use by another account.");
                }
                student.setEmail(newEmail);
            }
        }

        Student updated = studentRepository.save(student);
        return new StudentResponse(updated);
    }
}
