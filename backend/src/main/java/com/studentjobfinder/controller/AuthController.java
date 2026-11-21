package com.studentjobfinder.controller;

import com.studentjobfinder.dto.AuthResponse;
import com.studentjobfinder.dto.LoginRequest;
import com.studentjobfinder.dto.RegisterRequest;
import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.entity.Student;
import com.studentjobfinder.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/api/auth/register", "/api/students/register"})
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setAuthCookie(response, authResponse.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping({"/api/auth/login", "/api/students/login"})
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAuthCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AuthToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<StudentResponse> getAuthenticatedUser() {
        Student current = authService.getCurrentAuthenticatedStudent();
        return ResponseEntity.ok(new StudentResponse(current));
    }

    @GetMapping(value = "/api/health", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\",\"service\":\"Skill2Job Backend\"}");
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        if (token != null) {
            Cookie cookie = new Cookie("AuthToken", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 24 hours
            response.addCookie(cookie);
        }
    }
}
