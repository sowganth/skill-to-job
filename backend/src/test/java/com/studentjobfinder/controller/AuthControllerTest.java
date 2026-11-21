package com.studentjobfinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentjobfinder.dto.AuthResponse;
import com.studentjobfinder.dto.RegisterRequest;
import com.studentjobfinder.dto.StudentResponse;
import com.studentjobfinder.exception.GlobalExceptionHandler;
import com.studentjobfinder.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return 201 Created and set cookie on valid student registration")
    void testRegisterValidStudent() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Alice Wonder", "STU101", "alice@college.edu",
                "College of Tech", "CSE", "2nd Year", "SecurePass123"
        );

        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setName("Alice Wonder");
        studentResponse.setEmail("alice@college.edu");

        AuthResponse authResponse = new AuthResponse(true, "jwt-sample-token", studentResponse, "Success");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-sample-token"))
                .andExpect(jsonPath("$.student.name").value("Alice Wonder"))
                .andExpect(cookie().exists("AuthToken"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when mandatory registration fields are blank")
    void testRegisterValidationFailure() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        // All fields blank / invalid

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("Should return 200 OK from health endpoint")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
