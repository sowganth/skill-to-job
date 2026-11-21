package com.studentjobfinder.service;

import com.studentjobfinder.dto.AuthResponse;
import com.studentjobfinder.dto.LoginRequest;
import com.studentjobfinder.dto.RegisterRequest;
import com.studentjobfinder.entity.Role;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.exception.DuplicateResourceException;
import com.studentjobfinder.exception.UnauthorizedException;
import com.studentjobfinder.repository.StudentRepository;
import com.studentjobfinder.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(studentRepository, passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should successfully register student and hash password with BCrypt")
    void testRegistrationSuccessAndPasswordHashing() {
        RegisterRequest req = new RegisterRequest(
                "Jane Doe", "STU202401", "jane.doe@college.edu",
                "National College", "CSE", "3rd Year", "SecretPass123!"
        );

        when(studentRepository.existsByEmail("jane.doe@college.edu")).thenReturn(false);
        when(studentRepository.existsByStudentId("STU202401")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });
        when(tokenProvider.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Jane Doe", response.getStudent().getName());

        // Verify password was hashed and raw password is NOT stored
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        Student saved = studentCaptor.getValue();

        assertNotEquals("SecretPass123!", saved.getPasswordHash());
        assertTrue(passwordEncoder.matches("SecretPass123!", saved.getPasswordHash()));
        assertEquals(Role.ROLE_STUDENT, saved.getRole());
    }

    @Test
    @DisplayName("Should reject registration when email is duplicate")
    void testDuplicateEmailRejection() {
        RegisterRequest req = new RegisterRequest(
                "Duplicate Student", "STU999", "existing@college.edu",
                "College", "IT", "2nd Year", "Password123"
        );

        when(studentRepository.existsByEmail("existing@college.edu")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(req));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should reject registration when student ID is duplicate")
    void testDuplicateStudentIdRejection() {
        RegisterRequest req = new RegisterRequest(
                "Duplicate ID", "STU_EXISTS", "new@college.edu",
                "College", "IT", "2nd Year", "Password123"
        );

        when(studentRepository.existsByEmail("new@college.edu")).thenReturn(false);
        when(studentRepository.existsByStudentId("STU_EXISTS")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(req));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLoginSuccess() {
        String rawPass = "MyPassword123";
        Student student = new Student("Bob", "STU02", "bob@college.edu", "Col", "CS", "1st",
                passwordEncoder.encode(rawPass), Role.ROLE_STUDENT);
        student.setId(5L);

        when(studentRepository.findByEmail("bob@college.edu")).thenReturn(Optional.of(student));
        when(tokenProvider.generateToken("bob@college.edu", "ROLE_STUDENT")).thenReturn("jwt-bob");

        LoginRequest req = new LoginRequest("bob@college.edu", rawPass);
        AuthResponse res = authService.login(req);

        assertTrue(res.isSuccess());
        assertEquals("jwt-bob", res.getToken());
    }

    @Test
    @DisplayName("Should reject login with incorrect password")
    void testLoginFailureWrongPassword() {
        Student student = new Student("Bob", "STU02", "bob@college.edu", "Col", "CS", "1st",
                passwordEncoder.encode("RealPassword"), Role.ROLE_STUDENT);

        when(studentRepository.findByEmail("bob@college.edu")).thenReturn(Optional.of(student));

        LoginRequest req = new LoginRequest("bob@college.edu", "WrongPassword");
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }
}
