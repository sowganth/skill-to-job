-- ===================================================================
-- Database: student_skill_job_finder
-- MySQL Schema Definition for Student Skill-to-Job Finder System
-- ===================================================================

CREATE DATABASE IF NOT EXISTS student_skill_job_finder
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE student_skill_job_finder;

-- 1. Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    college VARCHAR(200) NOT NULL,
    department VARCHAR(100) NOT NULL,
    year_of_study VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    career_interests TEXT,
    bio TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_email (email),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB;

-- 2. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    normalized_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    INDEX idx_skill_normalized (normalized_name)
) ENGINE=InnoDB;

-- 3. Student-Skills Mapping
CREATE TABLE IF NOT EXISTS student_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency VARCHAR(30) NOT NULL,
    experience_duration VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_skill_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_skill_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_skill UNIQUE (student_id, skill_id)
) ENGINE=InnoDB;

-- 4. Job Roles Table
CREATE TABLE IF NOT EXISTS job_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    company VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    location VARCHAR(120),
    type VARCHAR(80),
    salary_range VARCHAR(100),
    description TEXT NOT NULL,
    responsibilities TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_job_category (category),
    INDEX idx_job_title (title)
) ENGINE=InnoDB;

-- 5. Job-Skills Mapping
CREATE TABLE IF NOT EXISTS job_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_role_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    requirement_type VARCHAR(30) NOT NULL, -- 'REQUIRED' or 'PREFERRED'
    CONSTRAINT fk_job_skill_job FOREIGN KEY (job_role_id) REFERENCES job_roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_skill_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_job_skill UNIQUE (job_role_id, skill_id)
) ENGINE=InnoDB;

-- 6. Learning Resources Table
CREATE TABLE IF NOT EXISTS learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    provider VARCHAR(150) NOT NULL,
    url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL, -- 'Free' or 'Paid'
    level VARCHAR(50) NOT NULL,  -- 'Beginner', 'Intermediate', 'Advanced'
    description TEXT,
    is_sample BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_resource_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    INDEX idx_resource_skill (skill_id)
) ENGINE=InnoDB;
