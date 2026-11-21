# Skill2Job — Spring Boot Backend Service

High-performance, secure Spring Boot REST API for the **Student Skill-to-Job Finder System**. The backend manages student authentication, academic profiles, skills inventories, job roles, intelligent multi-skill matching algorithms, automated gap analysis, and verified learning resource recommendations.

---

## 🛠️ Technology Stack & Architecture

- **Language:** Java 17+ (Fully tested on Java 17, 21, and 27)
- **Framework:** Spring Boot 3.3.4
- **Security:** Spring Security 6, BCrypt Password Hashing, JWT (HMAC-SHA256) via `io.jsonwebtoken 0.12.6`, HttpOnly Cookie + Bearer Token dual support
- **Persistence:** Spring Data JPA / Hibernate ORM
- **Database:** MySQL 8.x (Production/Local), H2 In-Memory (Test/Dev)
- **Validation:** Jakarta Bean Validation (Hibernate Validator 8.0)
- **Testing:** JUnit 5 (Jupiter), Mockito (Subclass Mock-Maker), Spring MockMvc
- **Build Tool:** Apache Maven 3.9+

---

## 📋 Prerequisites

1. **JDK 17 or newer:** Ensure `java -version` returns 17 or higher.
2. **Maven 3.9+:** Ensure `mvn -version` is accessible, or use local wrapper/tools path.
3. **MySQL 8.x:** Optional for development (H2 in-memory is included for testing).

---

## 🚀 Quick Setup & Running Locally

### 1. Run Automated Tests
Execute the unit and integration test suite:
```bash
mvn test
```

### 2. Configure Environment (Optional)
By default, the backend connects to `localhost:3306` with database name `student_skill_job_finder` and username `root`. You can customize these using environment variables:
```bash
# Windows PowerShell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/student_skill_job_finder?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:JWT_SECRET="YourSuperSecret256BitKeyForProductionSigning2026!"
```

### 3. Start Spring Boot Server
```bash
mvn spring-boot:run
```
Once started, the backend will listen on:
👉 **`http://localhost:8080/api`**

A default administrative account and initial skills/jobs catalog will automatically be seeded by `DataInitializer`:
- **Default Admin Email:** `admin@skill2job.edu`
- **Default Admin Password:** `Admin@Skill2Job2026!`

---

## 🔌 API Endpoints Reference

Base URL: `http://localhost:8080/api`

### 1. System Health
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | Public | System status and service health check |

---

### 2. Authentication
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` (or `/api/students/register`) | Public | Register new student account and issue JWT cookie/token |
| `POST` | `/api/auth/login` (or `/api/students/login`) | Public | Authenticate user credentials and return JWT |
| `POST` | `/api/auth/logout` | Public | Clears HttpOnly `AuthToken` session cookie |
| `GET` | `/api/auth/me` | Authenticated | Fetch current authenticated user's profile |

---

### 3. Student Profile Management
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/students/profile` | Student | Retrieve logged-in student's full academic profile |
| `PUT` | `/api/students/profile` | Student | Update student profile details (career interests, bio, year) |

---

### 4. Skill Inventory Management
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/skills` | Student | List all skills added to the student's portfolio |
| `POST` | `/api/skills` | Student | Add or update a skill with proficiency level |
| `DELETE` | `/api/skills/{id}` | Student | Remove a skill from student's portfolio by ID |
| `GET` | `/api/skills/catalog` | Public | Browse master list of all available skills |

---

### 5. Job Roles & Intelligent Recommendations
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/jobs` | Public | List all jobs with filters (`category`, `keyword`) |
| `GET` | `/api/jobs/{id}` | Public | Retrieve full job role specification and prerequisites |
| `GET` | `/api/jobs/recommendations` | Student | Calculate intelligent match percentages against student's skills |

---

### 6. Automated Skill Gap Analysis
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/skill-gap?jobId={id}` | Student | Calculate missing vs matched skills with priority score |

---

### 7. Learning Resources
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/learning-resources` | Public | Get verified learning documentation and courses |
| `GET` | `/api/learning-resources?skillId={id}` | Public | Filter resources for a specific skill |

---

### 8. Admin Job Management
| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/admin/jobs` | Admin | Create a new job listing with required skills |
| `PUT` | `/api/admin/jobs/{id}` | Admin | Update job listing details and requirements |
| `DELETE` | `/api/admin/jobs/{id}` | Admin | Delete a job role listing |

---

## 📝 Example Requests & Responses

### 1. Student Registration
**Request:**
```http
POST /api/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "name": "Alex Morgan",
  "studentId": "STU2026042",
  "email": "alex.morgan@college.edu",
  "college": "National Institute of Engineering",
  "department": "Computer Science & Engineering",
  "yearOfStudy": "3rd Year",
  "password": "Password@123"
}
```

**Response (`201 Created`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "student": {
    "id": 1,
    "name": "Alex Morgan",
    "studentId": "STU2026042",
    "email": "alex.morgan@college.edu",
    "college": "National Institute of Engineering",
    "department": "Computer Science & Engineering",
    "yearOfStudy": "3rd Year",
    "role": "STUDENT",
    "careerInterests": null,
    "bio": null
  }
}
```

---

### 2. Student Login
**Request:**
```http
POST /api/auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "email": "alex.morgan@college.edu",
  "password": "Password@123"
}
```

**Response (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "student": {
    "id": 1,
    "name": "Alex Morgan",
    "studentId": "STU2026042",
    "email": "alex.morgan@college.edu",
    "role": "STUDENT"
  }
}
```

---

### 3. Add Skill to Student Profile
**Request:**
```http
POST /api/skills HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Spring Boot",
  "category": "TECHNICAL",
  "proficiency": "INTERMEDIATE",
  "experience": "1 Year"
}
```

**Response (`201 Created`):**
```json
{
  "id": 1,
  "skillId": 2,
  "name": "Spring Boot",
  "category": "TECHNICAL",
  "proficiency": "INTERMEDIATE",
  "experience": "1 Year"
}
```

---

### 4. Job Recommendations with Match Percentage
**Request:**
```http
GET /api/jobs/recommendations HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response (`200 OK`):**
```json
[
  {
    "job": {
      "id": 1,
      "title": "Junior Java Developer",
      "company": "Infosys",
      "category": "Software Engineering",
      "location": "Bangalore / Hybrid",
      "jobType": "Full-Time",
      "salary": "₹6.5 - ₹8.5 LPA"
    },
    "matchPercentage": 75,
    "matchedSkills": [
      { "id": 1, "name": "Java" },
      { "id": 2, "name": "Spring Boot" },
      { "id": 3, "name": "SQL" }
    ],
    "missingSkills": [
      { "id": 4, "name": "Git" }
    ],
    "missingSkillCount": 1
  }
]
```

---

### 5. Automated Skill Gap Analysis
**Request:**
```http
GET /api/skill-gap?jobId=1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response (`200 OK`):**
```json
{
  "jobId": 1,
  "jobTitle": "Junior Java Developer",
  "company": "Infosys",
  "overallMatchPercentage": 75,
  "matchedSkills": [
    { "name": "Java", "proficiency": "ADVANCED" },
    { "name": "Spring Boot", "proficiency": "INTERMEDIATE" },
    { "name": "SQL", "proficiency": "INTERMEDIATE" }
  ],
  "missingSkills": [
    {
      "name": "Git",
      "importance": "HIGH",
      "priority": 1,
      "learningResources": [
        {
          "title": "Git Documentation & Pro Git Book",
          "provider": "Git SCM",
          "url": "https://git-scm.com/doc",
          "isFree": true,
          "level": "BEGINNER"
        }
      ]
    }
  ]
}
```

---

## 🧪 Automated Test Suite

All 16 unit and integration test cases run automatically with:
```bash
mvn test
```

### Test Coverage Highlights:
1. **`AuthServiceTest`**: Validates registration flow, duplicate email rejection, duplicate student ID detection, BCrypt password hashing verification, and authentication with invalid passwords.
2. **`JobMatcherServiceTest`**: Tests match calculation percentage algorithms, handling of 0-skill jobs, ranking/sorting, case-insensitive normalization, and separation of missing vs matched skills.
3. **`SkillGapServiceTest`**: Evaluates gap matrix generation and priority weighting.
4. **`StudentSecurityTest`**: Ensures student data isolation — preventing unauthorized students from viewing or mutating another student's profile.
5. **`AuthControllerTest`**: Verifies mock MVC request binding, validation constraints (HTTP 400), cookie generation on registration, and the `/api/health` JSON check.
