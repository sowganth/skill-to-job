# Student Skill-to-Job Finder System (Skill2Job)
**College Java Mini Project — Modern Responsive Frontend**

An intelligent, student-centric career platform bridging the gap between college curricula and industry job requirements. The frontend quantifies skill alignment, performs automated gap analysis, and provides verified learning pathways to prepare students for campus placements and entry-level IT roles.

---

## 🛠️ Technology Stack

- **Frontend:** Vanilla HTML5, CSS3 (Custom Design System), and Modern Vanilla JavaScript (ES6+).
- **Styling Architecture:** Clean SaaS aesthetics with White/Light-Gray backgrounds (`#f8fafc`, `#ffffff`), Blue & Purple accents (`#2563eb`, `#4f46e5`, `#7c3aed`), double-bezel cards, and responsive fluid grids.
- **Backend (Planned/Target):** Java 17+ with Spring Boot (RESTful API controllers, Spring Data JPA).
- **Database (Target):** MySQL 8.x.
- **Strict Compliance:** Zero frontend framework dependencies (No React, Angular, Vue, TypeScript, or Tailwind).

---

## 📁 Frontend Directory Structure

```
frontend/
├── index.html                  # Page 1: Public Landing Page & Student Overview
├── register.html               # Page 2: Student Registration Form & Validations
├── login.html                  # Page 3: Student Login & Demo Account Autofill
├── dashboard.html              # Page 4: Student Metric Dashboard & Match Summaries
├── profile.html                # Page 5: Academic Profile Management
├── skills.html                 # Page 6: Skills Inventory & Proficiency Tracking
├── jobs.html                   # Page 7: Searchable Job Recommendations & Filters
├── job-details.html            # Page 8: In-Depth Role Specifications & Match Gauges
├── skill-gap.html              # Page 9: Comparative Skill Gap Matrix & Priorities
├── learning-resources.html     # Page 10: Verified Documentation & Curated Roadmaps
├── css/
│   ├── style.css               # Core design tokens, typography, buttons, hero, footer, toasts
│   ├── dashboard.css           # SaaS sidebar layout, metric counters, double-bezel cards, tables
│   └── responsive.css          # Mobile drawer navigation, collapsible grids, and touch targets
└── js/
    ├── config.js               # API base URL (http://localhost:8080/api), safe HTML escape, fallbacks
    ├── auth.js                 # JWT token manager, protected route guards (no plaintext passwords)
    ├── main.js                 # Global toasts, modal handlers, match calculation engine, status pill
    ├── dashboard.js            # Dashboard real-time stats, role rankings, and bridge skill calculation
    ├── profile.js              # Academic profile loading and PUT updates
    ├── skills.js               # Skill catalog quick-picks, search, filtering, and CRUD operations
    ├── jobs.js                 # Multi-criteria job filtering, match percentages, and sorting
    ├── skill-gap.js            # Interactive comparative matrix, learning priority allocation
    └── learning-resources.js   # Filterable directory of verified official documentation links
```

---

## 🚀 How to Run and Preview the Frontend

No build steps or npm installations are necessary! You can preview the application using any of the following methods:

### Method 1: Python Built-in Web Server (Recommended)
Open a terminal in the project directory and run:
```bash
# Navigate to the frontend directory
cd frontend

# Run HTTP server (Python 3)
python -m http.server 3000
```
Then open your browser and navigate to:
👉 **[http://localhost:3000](http://localhost:3000)**

### Method 2: Node / npx serve
```bash
npx serve frontend
```

### Method 3: VS Code / IDE Live Server
Right-click `frontend/index.html` inside VS Code and select **"Open with Live Server"**.

### Method 4: Direct Browser Execution
You can also directly open `frontend/index.html` by double-clicking it in Windows File Explorer. All navigation links and assets use relative paths.

## 🚀 How to Run the Complete System

### 1. Start the Java Spring Boot Backend (Port 8080)
Open a terminal in the `backend/` directory:
```bash
# Run tests to verify build
mvn test -f backend/pom.xml

# Start the Spring Boot application
mvn spring-boot:run -f backend/pom.xml
```
The REST API will start at **`http://localhost:8080/api`**. Initial skills and jobs will automatically seed into the database.

---

### 2. Preview the Frontend (Port 3000 / 5500)
Open a second terminal in the `frontend/` directory:
```bash
cd frontend
python -m http.server 3000
```
Then open your browser at:
👉 **[http://localhost:3000](http://localhost:3000)** (or via Live Server at `http://localhost:5500`)

The frontend will detect the live backend on port 8080 and display the green **"Backend Online"** badge.

---

## 🔌 Spring Boot Backend REST APIs

The Spring Boot backend listens on **`http://localhost:8080/api`** (configured in [frontend/js/config.js](frontend/js/config.js)). For complete documentation and example JSON requests, see [backend/README.md](backend/README.md).

The following REST endpoints are integrated in the frontend:

### 1. Authentication & Student Registration
| Method | Endpoint | Request Body | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/students/register` | `{ name, studentId, email, college, department, year, password }` | Creates a new student record. Hashes password using BCrypt. |
| `POST` | `/api/students/login` | `{ email, password }` | Authenticates student and returns `{ token, student }`. |

### 2. Student Profile
| Method | Endpoint | Request Body / Query | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/students/profile` | Header: `Authorization: Bearer <token>` | Returns current student's academic and career information. |
| `PUT` | `/api/students/profile` | `{ name, studentId, email, college, department, year, careerInterests, bio }` | Updates student profile details. |

### 3. Skills Management
| Method | Endpoint | Request Body / Query | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/skills` | Header: `Authorization: Bearer <token>` | Retrieves student's logged skills: `[{ id, name, category, proficiency, experience }]`. |
| `POST` | `/api/skills` | `{ name, category, proficiency, experience }` | Adds a new skill to student's portfolio. |
| `DELETE` | `/api/skills/{id}` | Path variable `id` | Removes a skill from the student's portfolio. |

### 4. Job Vacancies & Recommendations
| Method | Endpoint | Request Body / Query | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/jobs` | Optional query: `?category=...&keyword=...` | Returns all available job specifications with `requiredSkills` array. |
| `GET` | `/api/jobs/{id}` | Path variable `id` | Returns complete details, responsibilities, and prerequisites for a single job. |

### 5. Health Check
| Method | Endpoint | Request Body / Query | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | None | Returns status 200 OK to indicate backend is online. |

---

## 🛡️ Security, Privacy & Compliance Highlights

1. **No Plaintext Passwords:** Passwords submitted during registration and login are never stored in `localStorage`, `sessionStorage`, or cookies.
2. **XSS Protection:** All user-controlled and API-supplied strings are rendered safely using textContent or sanitized with an `escapeHtml()` utility before injection into DOM structures.
3. **Session Expiry Handling:** HTTP 401 Unauthorized responses trigger session cleanup and redirect to `login.html?sessionExpired=true`.
4. **Development Fallback Mode:** When the Spring Boot backend server is not running on port 8080, the frontend automatically falls back to sample student data and displays a transparent notice banner. Once the Spring Boot application is started, the indicator automatically switches to **"Backend Online"**.
5. **Academic Disclaimer:** The skill-gap analysis includes an explicit compliance disclaimer clarifying that match metrics indicate curriculum alignment and do not constitute an employment guarantee.
