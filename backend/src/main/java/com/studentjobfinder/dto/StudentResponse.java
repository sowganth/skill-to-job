package com.studentjobfinder.dto;

import com.studentjobfinder.entity.Student;

public class StudentResponse {

    private Long id;
    private String name;
    private String studentId;
    private String email;
    private String college;
    private String department;
    private String year;
    private String careerInterests;
    private String bio;
    private String role;

    public StudentResponse() {
    }

    public StudentResponse(Student student) {
        if (student != null) {
            this.id = student.getId();
            this.name = student.getName();
            this.studentId = student.getStudentId();
            this.email = student.getEmail();
            this.college = student.getCollege();
            this.department = student.getDepartment();
            this.year = student.getYearOfStudy();
            this.careerInterests = student.getCareerInterests();
            this.bio = student.getBio();
            this.role = student.getRole() != null ? student.getRole().name() : "ROLE_STUDENT";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCareerInterests() {
        return careerInterests;
    }

    public void setCareerInterests(String careerInterests) {
        this.careerInterests = careerInterests;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
