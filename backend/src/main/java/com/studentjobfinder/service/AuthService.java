package com.studentjobfinder.service;

import com.studentjobfinder.dto.AuthResponse;
import com.studentjobfinder.dto.LoginRequest;
import com.studentjobfinder.dto.RegisterRequest;
import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.entity.Role;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.exception.DuplicateResourceException;
import com.studentjobfinder.exception.UnauthorizedException;
import com.studentjobfinder.repository.StudentRepository;
import com.studentjobfinder.security.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        String cleanStudentId = request.getStudentId().trim();

        if (studentRepository.existsByEmail(cleanEmail)) {
            throw new DuplicateResourceException("An account with email " + cleanEmail + " already exists.");
        }

        if (studentRepository.existsByStudentId(cleanStudentId)) {
            throw new DuplicateResourceException("An account with Student ID " + cleanStudentId + " already exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Student student = new Student(
                request.getName().trim(),
                cleanStudentId,
                cleanEmail,
                request.getCollege().trim(),
                request.getDepartment().trim(),
                request.getYear().trim(),
                hashedPassword,
                Role.ROLE_STUDENT
        );

        student.setBio("Computer Science student focusing on modern software engineering.");
        student.setCareerInterests("Software Engineering, Full-Stack Development");

        Student savedStudent = studentRepository.save(student);

        String token = tokenProvider.generateToken(savedStudent.getEmail(), savedStudent.getRole().name());

        return new AuthResponse(
                true,
                token,
                new StudentResponse(savedStudent),
                "Student account successfully registered"
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        Student student = studentRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        String token = tokenProvider.generateToken(student.getEmail(), student.getRole().name());

        return new AuthResponse(
                true,
                token,
                new StudentResponse(student),
                "Login successful"
        );
    }

    @Transactional(readOnly = true)
    public Student getCurrentAuthenticatedStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = auth.getName();
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated student not found"));
    }
}
