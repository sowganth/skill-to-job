package com.studentjobfinder.config;

import com.studentjobfinder.entity.*;
import com.studentjobfinder.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final JobRoleRepository jobRoleRepository;
    private final LearningResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@skill2job.edu}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@Skill2Job2026!}")
    private String adminPassword;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    public DataInitializer(StudentRepository studentRepository,
                           SkillRepository skillRepository,
                           StudentSkillRepository studentSkillRepository,
                           JobRoleRepository jobRoleRepository,
                           LearningResourceRepository resourceRepository,
                           PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.resourceRepository = resourceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminUser();
        Map<String, Skill> skillsMap = seedSkills();
        Student demoStudent = seedDemoStudent();
        seedStudentSkills(demoStudent, skillsMap);
        seedJobRoles(skillsMap);
        seedLearningResources(skillsMap);
    }

    private void seedAdminUser() {
        if (!studentRepository.existsByEmail(adminEmail)) {
            Student admin = new Student(
                    adminName,
                    "ADMIN001",
                    adminEmail,
                    "Skill2Job Administration",
                    "Computer Science",
                    "Faculty / Admin",
                    passwordEncoder.encode(adminPassword),
                    Role.ROLE_ADMIN
            );
            admin.setBio("System Administrator for the Skill2Job Campus Career Platform.");
            studentRepository.save(admin);
        }
    }

    private Student seedDemoStudent() {
        String demoEmail = "rahul.sharma@college.edu";
        return studentRepository.findByEmail(demoEmail).orElseGet(() -> {
            Student student = new Student(
                    "Rahul Sharma",
                    "STU2024CS089",
                    demoEmail,
                    "National Institute of Engineering & Technology",
                    "Computer Science & Engineering",
                    "3rd Year",
                    passwordEncoder.encode("DemoPass123!"),
                    Role.ROLE_STUDENT
            );
            student.setBio("Aspiring Full-Stack & Java Backend Engineer with a strong passion for microservices, cloud systems, and scalable architecture.");
            student.setCareerInterests("Java Backend, Cloud Systems, Full-Stack Development");
            return studentRepository.save(student);
        });
    }

    private Map<String, Skill> seedSkills() {
        Map<String, Skill> map = new HashMap<>();

        String[][] initialSkills = {
                {"Java", "TECHNICAL"},
                {"Spring Boot", "TECHNICAL"},
                {"SQL", "TECHNICAL"},
                {"HTML", "TECHNICAL"},
                {"CSS", "TECHNICAL"},
                {"JavaScript", "TECHNICAL"},
                {"Git", "TECHNICAL"},
                {"GitHub", "TECHNICAL"},
                {"Python", "TECHNICAL"},
                {"C", "TECHNICAL"},
                {"C++", "TECHNICAL"},
                {"Data Structures", "TECHNICAL"},
                {"Docker", "TECHNICAL"},
                {"Linux", "TECHNICAL"},
                {"React", "TECHNICAL"},
                {"REST APIs", "TECHNICAL"},
                {"Communication", "SOFT_SKILL"},
                {"Problem Solving", "SOFT_SKILL"},
                {"Team Collaboration", "SOFT_SKILL"}
        };

        for (String[] def : initialSkills) {
            String name = def[0];
            SkillCategory cat = SkillCategory.valueOf(def[1]);
            String normalized = Skill.normalize(name);

            Skill skill = skillRepository.findByNormalizedName(normalized).orElseGet(() ->
                    skillRepository.save(new Skill(name, cat))
            );
            map.put(normalized, skill);
        }

        return map;
    }

    private void seedStudentSkills(Student student, Map<String, Skill> skillsMap) {
        if (student == null || studentSkillRepository.countByStudentId(student.getId()) > 0) {
            return;
        }

        String[][] studentInitial = {
                {"java", "ADVANCED", "1 - 2 Years"},
                {"spring boot", "INTERMEDIATE", "6 - 12 Months"},
                {"sql", "INTERMEDIATE", "1 - 2 Years"},
                {"html", "ADVANCED", "2+ Years"},
                {"css", "INTERMEDIATE", "1 - 2 Years"},
                {"javascript", "INTERMEDIATE", "1 - 2 Years"},
                {"git", "INTERMEDIATE", "1 - 2 Years"},
                {"communication", "ADVANCED", "2+ Years"},
                {"problem solving", "ADVANCED", "2+ Years"}
        };

        for (String[] def : studentInitial) {
            Skill skill = skillsMap.get(def[0]);
            if (skill != null) {
                ProficiencyLevel prof = ProficiencyLevel.valueOf(def[1]);
                studentSkillRepository.save(new StudentSkill(student, skill, prof, def[2]));
            }
        }
    }

    private void seedJobRoles(Map<String, Skill> skillsMap) {
        if (jobRoleRepository.count() > 0) return;

        // Job 1
        JobRole job1 = new JobRole(
                "Junior Java Backend Developer",
                "Nexus Infotech Solutions",
                "Backend",
                "Bangalore / Hybrid",
                "Full-Time (Entry Level)",
                "₹5,50,000 - ₹7,50,000 / year",
                "We are seeking an enthusiastic Junior Java Developer to join our core banking microservices team. You will build resilient REST APIs, design database schemas, and participate in peer code reviews.",
                "Design, develop, and maintain RESTful web services in Spring Boot\nOptimize database queries and schema designs using MySQL/PostgreSQL\nCollaborate with front-end engineers to integrate user-facing features\nParticipate in agile sprints, daily standups, and design reviews"
        );
        jobRoleRepository.save(job1);
        attachSkill(job1, skillsMap.get("java"), RequirementType.REQUIRED);
        attachSkill(job1, skillsMap.get("spring boot"), RequirementType.REQUIRED);
        attachSkill(job1, skillsMap.get("sql"), RequirementType.REQUIRED);
        attachSkill(job1, skillsMap.get("git"), RequirementType.REQUIRED);
        attachSkill(job1, skillsMap.get("data structures"), RequirementType.REQUIRED);
        attachSkill(job1, skillsMap.get("docker"), RequirementType.PREFERRED);
        attachSkill(job1, skillsMap.get("rest apis"), RequirementType.PREFERRED);
        jobRoleRepository.save(job1);

        // Job 2
        JobRole job2 = new JobRole(
                "Full Stack Java Engineer (Graduate)",
                "AuraCloud Technologies",
                "Full Stack",
                "Hyderabad / On-site",
                "Full-Time (Campus Placement)",
                "₹6,00,000 - ₹8,20,000 / year",
                "Join our product engineering division to craft modern enterprise SaaS applications. You will work across the stack with Java Spring Boot backend and responsive modern web interfaces.",
                "Develop end-to-end features spanning database, backend APIs, and web frontend\nEnsure high code quality through automated unit and integration tests\nDiagnose and troubleshoot application bottlenecks and bugs"
        );
        jobRoleRepository.save(job2);
        attachSkill(job2, skillsMap.get("java"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("spring boot"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("javascript"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("html"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("css"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("sql"), RequirementType.REQUIRED);
        attachSkill(job2, skillsMap.get("react"), RequirementType.PREFERRED);
        attachSkill(job2, skillsMap.get("github"), RequirementType.PREFERRED);
        jobRoleRepository.save(job2);

        // Job 3
        JobRole job3 = new JobRole(
                "Associate Software Engineer - Python & Data",
                "QuantVantage Analytics",
                "Data Science",
                "Pune / Remote",
                "Full-Time",
                "₹6,50,000 - ₹9,00,000 / year",
                "Looking for fresh computer science graduates with sharp algorithmic aptitude to build data pipeline ingestion services and automated reporting pipelines.",
                "Construct ETL workflows processing high-volume datasets\nImplement statistical models and database query optimizations\nMaintain reliable codebase documentation and automated CI tests"
        );
        jobRoleRepository.save(job3);
        attachSkill(job3, skillsMap.get("python"), RequirementType.REQUIRED);
        attachSkill(job3, skillsMap.get("sql"), RequirementType.REQUIRED);
        attachSkill(job3, skillsMap.get("data structures"), RequirementType.REQUIRED);
        attachSkill(job3, skillsMap.get("problem solving"), RequirementType.REQUIRED);
        attachSkill(job3, skillsMap.get("git"), RequirementType.REQUIRED);
        attachSkill(job3, skillsMap.get("communication"), RequirementType.PREFERRED);
        jobRoleRepository.save(job3);

        // Job 4
        JobRole job4 = new JobRole(
                "Frontend Web Developer Trainee",
                "Veloce Digital Media",
                "Frontend",
                "Chennai / Hybrid",
                "Internship to Full-Time",
                "₹4,80,000 - ₹6,20,000 / year",
                "Passionate about human-centered UI/UX and responsive web engineering? We are looking for a trainee frontend developer with strong CSS, HTML, and modern JS foundations.",
                "Transform design specs into pixel-perfect semantic HTML/CSS\nConnect frontend components to REST APIs and handle asynchronous state\nOptimize web performance, accessibility, and cross-browser stability"
        );
        jobRoleRepository.save(job4);
        attachSkill(job4, skillsMap.get("html"), RequirementType.REQUIRED);
        attachSkill(job4, skillsMap.get("css"), RequirementType.REQUIRED);
        attachSkill(job4, skillsMap.get("javascript"), RequirementType.REQUIRED);
        attachSkill(job4, skillsMap.get("git"), RequirementType.REQUIRED);
        attachSkill(job4, skillsMap.get("communication"), RequirementType.REQUIRED);
        attachSkill(job4, skillsMap.get("react"), RequirementType.PREFERRED);
        jobRoleRepository.save(job4);

        // Job 5
        JobRole job5 = new JobRole(
                "Junior Cloud & DevOps Trainee",
                "StratoSphere Cloud Labs",
                "DevOps",
                "Gurugram / On-site",
                "Full-Time",
                "₹5,80,000 - ₹7,80,000 / year",
                "Kickstart your career in cloud infrastructure and site reliability. Learn infrastructure as code, CI/CD pipelines, container orchestration, and cloud security monitoring.",
                "Assist senior SREs in automating container deployment pipelines\nMonitor application logs, uptime alerts, and resource utilization\nDocument configuration runbooks and disaster recovery protocols"
        );
        jobRoleRepository.save(job5);
        attachSkill(job5, skillsMap.get("git"), RequirementType.REQUIRED);
        attachSkill(job5, skillsMap.get("github"), RequirementType.REQUIRED);
        attachSkill(job5, skillsMap.get("linux"), RequirementType.REQUIRED);
        attachSkill(job5, skillsMap.get("problem solving"), RequirementType.REQUIRED);
        attachSkill(job5, skillsMap.get("communication"), RequirementType.REQUIRED);
        attachSkill(job5, skillsMap.get("docker"), RequirementType.PREFERRED);
        jobRoleRepository.save(job5);

        // Job 6
        JobRole job6 = new JobRole(
                "C++ Systems Engineering Intern",
                "Vertex Real-Time Systems",
                "Systems",
                "Bangalore / On-site",
                "Internship",
                "₹35,000 / month stipend",
                "Work on low-latency memory management, operating system primitives, and high-performance algorithms for real-time sensor processing systems.",
                "Write safe, high-speed C++ modules for memory-constrained platforms\nBenchmark execution cycles and profile memory leakages\nCollaborate on embedded driver integration"
        );
        jobRoleRepository.save(job6);
        attachSkill(job6, skillsMap.get("c"), RequirementType.REQUIRED);
        attachSkill(job6, skillsMap.get("c++"), RequirementType.REQUIRED);
        attachSkill(job6, skillsMap.get("data structures"), RequirementType.REQUIRED);
        attachSkill(job6, skillsMap.get("problem solving"), RequirementType.REQUIRED);
        attachSkill(job6, skillsMap.get("linux"), RequirementType.PREFERRED);
        jobRoleRepository.save(job6);
    }

    private void attachSkill(JobRole job, Skill skill, RequirementType type) {
        if (skill != null) {
            job.addSkill(skill, type);
        }
    }

    private void seedLearningResources(Map<String, Skill> skillsMap) {
        if (resourceRepository.count() > 0) return;

        Object[][] resources = {
                {"java", "Official Java Tutorials & Language Documentation", "Oracle Corporation", "https://docs.oracle.com/javase/tutorial/", "Free", "Beginner to Advanced", "Comprehensive practical guide covering Java OOP principles, collections, and concurrency.", false},
                {"spring boot", "Building a RESTful Web Service with Spring Boot", "Spring.io Official Guides", "https://spring.io/guides/gs/rest-service", "Free", "Beginner", "Step-by-step practical tutorial to spin up your first production Spring Boot REST API.", false},
                {"sql", "SQL Tutorial & Interactive Practice", "W3Schools / SQL Zoo", "https://www.w3schools.com/sql/", "Free", "Beginner", "Learn SQL queries, JOIN operations, aggregations, and schema design with live sandbox.", false},
                {"git", "Pro Git Book (Complete Official Edition)", "Git SCM Project", "https://git-scm.com/book/en/v2", "Free", "Beginner to Intermediate", "The definitive open-source guide to distributed version control and collaborative workflows.", false},
                {"github", "GitHub Skills - Interactive Learning Lab", "GitHub Education", "https://skills.github.com/", "Free", "Beginner", "Interactive browser exercises for pull requests, branch protection, and merge conflict resolution.", false},
                {"data structures", "Data Structures and Algorithms Specialization", "GeeksforGeeks", "https://www.geeksforgeeks.org/data-structures/", "Free", "Intermediate", "Authoritative references on arrays, linked lists, trees, graphs, sorting, and complexity analysis.", false},
                {"docker", "Docker Get Started & Container Orientation", "Docker Official Docs", "https://docs.docker.com/get-started/", "Free", "Beginner", "Understand containerization, Dockerfiles, images, containers, and network bridging.", false},
                {"javascript", "JavaScript Guide & Modern Web Standards", "MDN Web Docs (Mozilla)", "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide", "Free", "Beginner to Advanced", "The web industry standard documentation for JavaScript syntax, asynchronous promises, and DOM.", false},
                {"python", "The Python Tutorial (Official Docs)", "Python Software Foundation", "https://docs.python.org/3/tutorial/", "Free", "Beginner", "Official introductory guide to Python syntax, modules, virtual environments, and exceptions.", false},
                {"react", "React Documentation (Quick Start)", "React.dev (Meta Open Source)", "https://react.dev/learn", "Free", "Beginner to Intermediate", "Official interactive documentation teaching functional components, state hooks, and lifecycle.", false},
                {"problem solving", "Problem Solving & Algorithmic Practice", "HackerRank", "https://www.hackerrank.com/domains/tutorials/10-days-of-javascript", "Free", "Beginner to Advanced", "Coding challenges designed to sharpen computational thinking and interview readiness.", false},
                {"communication", "Technical Communication & Team Collaboration", "Google Tech Writing", "https://developers.google.com/tech-writing", "Free", "Beginner", "Learn clear technical writing, code review etiquette, and cross-functional engineering communication.", false}
        };

        for (Object[] r : resources) {
            String skillKey = (String) r[0];
            Skill skill = skillsMap.get(skillKey);
            if (skill != null) {
                resourceRepository.save(new LearningResource(
                        skill,
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (String) r[5],
                        (String) r[6],
                        (Boolean) r[7]
                ));
            }
        }
    }
}
