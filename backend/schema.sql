-- ===================================================================
-- Student Skill-to-Job Finder System (Skill2Job)
-- MySQL 8.x Database Schema (Reference DDL)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS student_skill_job_finder
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE student_skill_job_finder;

-- 1. Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    college VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    year_of_study VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    bio TEXT,
    career_interests VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Skills Catalog Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Student Skills (Many-to-Many mapping with proficiency)
CREATE TABLE IF NOT EXISTS student_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency VARCHAR(30) NOT NULL DEFAULT 'BEGINNER',
    experience VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_skills_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_skill UNIQUE (student_id, skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Job Roles Table
CREATE TABLE IF NOT EXISTS job_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    company VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    location VARCHAR(100) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    salary VARCHAR(80),
    description TEXT NOT NULL,
    responsibilities TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Job Required Skills Table
CREATE TABLE IF NOT EXISTS job_required_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    requirement_type VARCHAR(30) NOT NULL DEFAULT 'REQUIRED',
    CONSTRAINT fk_job_skills_job FOREIGN KEY (job_id) REFERENCES job_roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_job_skill UNIQUE (job_id, skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Learning Resources Table
CREATE TABLE IF NOT EXISTS learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL,
    is_free BOOLEAN NOT NULL DEFAULT TRUE,
    level VARCHAR(30) NOT NULL DEFAULT 'BEGINNER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_learning_resource_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
