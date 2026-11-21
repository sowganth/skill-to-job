package com.studentjobfinder.dto;

public class AuthResponse {

    private boolean success;
    private String token;
    private StudentResponse student;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String token, StudentResponse student, String message) {
        this.success = success;
        this.token = token;
        this.student = student;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public StudentResponse getStudent() {
        return student;
    }

    public void setStudent(StudentResponse student) {
        this.student = student;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
