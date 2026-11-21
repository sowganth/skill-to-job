package com.studentjobfinder.service;

import com.studentjobfinder.dto.UpdateProfileRequest;
import com.studentjobfinder.entity.Role;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentSecurityTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AuthService authService;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, authService);
    }

    @Test
    @DisplayName("Should prevent a student from accessing another student's private profile")
    void testStudentDataIsolationRead() {
        // Authenticated student is Student 1
        Student student1 = new Student();
        student1.setId(1L);
        student1.setEmail("student1@college.edu");
        student1.setRole(Role.ROLE_STUDENT);

        when(authService.getCurrentAuthenticatedStudent()).thenReturn(student1);

        // Student 1 attempts to read profile of Student 2
        Long targetStudentId = 2L;

        assertThrows(AccessDeniedException.class, () -> studentService.getProfile(targetStudentId));
        verify(studentRepository, never()).findById(targetStudentId);
    }

    @Test
    @DisplayName("Should prevent a student from modifying another student's profile")
    void testStudentDataIsolationUpdate() {
        // Authenticated student is Student 1
        Student student1 = new Student();
        student1.setId(1L);
        student1.setEmail("student1@college.edu");
        student1.setRole(Role.ROLE_STUDENT);

        when(authService.getCurrentAuthenticatedStudent()).thenReturn(student1);

        UpdateProfileRequest updateReq = new UpdateProfileRequest();
        updateReq.setName("Malicious Update");

        // Student 1 attempts to modify profile of Student 2
        Long targetStudentId = 2L;

        assertThrows(AccessDeniedException.class, () -> studentService.updateProfile(targetStudentId, updateReq));
        verify(studentRepository, never()).save(any(Student.class));
    }
}
