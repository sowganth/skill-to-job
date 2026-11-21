/**
 * Skill2Job - Frontend Configuration
 * Student Skill-to-Job Finder System
 * Configured for Java Spring Boot Backend (default: http://localhost:8080/api)
 */

const CONFIG = {
  // Backend Spring Boot REST API Base URL
  API_BASE_URL: 'http://localhost:8080/api',

  // Request timeout in milliseconds (short timeout for responsive mock fallback)
  REQUEST_TIMEOUT: 2500,

  // Fallback to rich mock data when backend is not yet started or unreachable
  // Enables students to demo and test all UI flows immediately
  ENABLE_MOCK_FALLBACK: true,

  // Storage keys
  STORAGE_KEYS: {
    AUTH_TOKEN: 's2j_auth_token',
    USER_DATA: 's2j_user_data',
    MY_SKILLS: 's2j_my_skills',
    APP_SETTINGS: 's2j_app_settings',
    REGISTERED_STUDENTS: 's2j_registered_students'
  },

  // Skill proficiency levels (strictly fixed, do not invent values)
  PROFICIENCY_LEVELS: ['Beginner', 'Intermediate', 'Advanced'],

  // Experience level options
  EXPERIENCE_LEVELS: ['< 6 Months', '6 - 12 Months', '1 - 2 Years', '2+ Years'],

  // Skill categories
  SKILL_CATEGORIES: ['Technical', 'Soft Skill']
};

/**
 * Safe HTML escaping to prevent XSS vulnerabilities
 * @param {string} str
 * @returns {string}
 */
function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

/**
 * Universal API fetch wrapper with timeout, token injection, and offline fallback
 * @param {string} endpoint - e.g. '/students/profile'
 * @param {object} options - Fetch options (method, headers, body)
 * @returns {Promise<any>}
 */
async function apiFetch(endpoint, options = {}) {
  const url = `${CONFIG.API_BASE_URL}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
  const token = localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);

  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), CONFIG.REQUEST_TIMEOUT);

  try {
    const response = await fetch(url, {
      ...options,
      headers,
      signal: controller.signal
    });
    clearTimeout(timeoutId);

    // Handle session expiration
    if (response.status === 401) {
      handleUnauthorized();
      throw new Error('Session expired or unauthorized. Please log in again.');
    }

    if (response.status === 403) {
      throw new Error('Access forbidden: You do not have permission for this resource.');
    }

    if (response.status === 404) {
      throw new Error('The requested resource was not found on the backend server.');
    }

    if (!response.ok) {
      const errorJson = await response.json().catch(() => null);
      const message = errorJson?.message || `Server returned status ${response.status}`;
      throw new Error(message);
    }

    return await response.json();
  } catch (error) {
    clearTimeout(timeoutId);

    // If backend server is unreachable or offline, engage mock fallback
    if (CONFIG.ENABLE_MOCK_FALLBACK) {
      console.warn(`[Skill2Job] Backend offline/unreachable at ${CONFIG.API_BASE_URL}. Engaging mock fallback for ${endpoint}`);
      return handleMockFallback(endpoint, options);
    }

    throw error;
  }
}

/**
 * Handles 401 Unauthorized globally
 */
function handleUnauthorized() {
  if (CONFIG.ENABLE_MOCK_FALLBACK) return;
  localStorage.removeItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
}

/**
 * Helper to manage mock registered students database
 */
function getMockRegisteredStudents() {
  try {
    const stored = localStorage.getItem(CONFIG.STORAGE_KEYS.REGISTERED_STUDENTS);
    if (stored) return JSON.parse(stored);
  } catch(e) {}
  return [
    {
      id: 101,
      name: 'Rahul Sharma',
      studentId: 'STU2024CS089',
      email: 'rahul.sharma@college.edu',
      password: 'DemoPass123!',
      college: 'National Institute of Engineering & Technology',
      department: 'Computer Science & Engineering',
      year: '3rd Year',
      bio: 'Aspiring Full-Stack & Java Backend Engineer with a strong passion for microservices, cloud systems, and scalable architecture.',
      careerInterests: 'Backend Engineering, Cloud Systems, Distributed Architecture'
    }
  ];
}

function saveMockRegisteredStudents(list) {
  try {
    localStorage.setItem(CONFIG.STORAGE_KEYS.REGISTERED_STUDENTS, JSON.stringify(list));
  } catch(e) {}
}

/**
 * Built-in mock data provider for local development without active Spring Boot server
 */
function handleMockFallback(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  const cleanEndpoint = endpoint.split('?')[0];

  // Mock Authentication / Registration
  if (cleanEndpoint.includes('/auth/login') || cleanEndpoint.includes('/students/login')) {
    let credentials = {};
    try { credentials = JSON.parse(options.body || '{}'); } catch(e){}
    const list = getMockRegisteredStudents();
    const email = (credentials.email || '').trim().toLowerCase();
    const password = credentials.password || '';

    // Search in registered students list
    const found = list.find(s => s.email && s.email.toLowerCase() === email);
    if (found) {
      if (found.password && password && found.password !== password) {
        return Promise.reject(new Error('Incorrect password for this student account.'));
      }
      return Promise.resolve({
        success: true,
        token: 'demo-jwt-mock-token-' + Date.now(),
        student: found,
        message: `Welcome back, ${found.name}!`
      });
    }

    // Dynamic mock user for any entered email
    const inferredName = email.split('@')[0].replace(/[._-]/g, ' ').replace(/(^\w|\s\w)/g, m => m.toUpperCase()) || 'Student';
    const dynamicUser = {
      id: Date.now(),
      name: inferredName,
      studentId: 'STU' + Math.floor(100000 + Math.random() * 900000),
      email: credentials.email || 'student@college.edu',
      password: password,
      college: 'National Institute of Engineering & Technology',
      department: 'Computer Science & Engineering',
      year: '3rd Year',
      bio: 'Aspiring software developer preparing for campus placements.',
      careerInterests: 'Software Engineering, Full-Stack Development'
    };

    list.push(dynamicUser);
    saveMockRegisteredStudents(list);

    return Promise.resolve({
      success: true,
      token: 'demo-jwt-mock-token-' + Date.now(),
      student: dynamicUser,
      message: `Welcome, ${dynamicUser.name}!`
    });
  }

  if (cleanEndpoint.includes('/auth/register') || cleanEndpoint.includes('/students/register')) {
    let payload = {};
    try { payload = JSON.parse(options.body || '{}'); } catch(e){}
    const list = getMockRegisteredStudents();
    const email = (payload.email || '').trim().toLowerCase();

    const newStudent = {
      id: payload.studentId || ('STU' + Date.now().toString().slice(-6)),
      name: payload.name || 'New Student',
      studentId: payload.studentId || ('STU' + Date.now().toString().slice(-6)),
      email: payload.email,
      password: payload.password,
      college: payload.college || 'Engineering College',
      department: payload.department || 'Computer Science & Engineering',
      year: payload.year || '3rd Year',
      bio: `${payload.department || 'Computer Science'} student focusing on software engineering and placement preparation.`,
      careerInterests: 'Software Engineering, Full-Stack Development'
    };

    const existingIdx = list.findIndex(s => s.email && s.email.toLowerCase() === email);
    if (existingIdx >= 0) {
      list[existingIdx] = { ...list[existingIdx], ...newStudent };
    } else {
      list.push(newStudent);
    }
    saveMockRegisteredStudents(list);

    // Save as current user data
    localStorage.setItem(CONFIG.STORAGE_KEYS.USER_DATA, JSON.stringify(newStudent));

    return Promise.resolve({
      success: true,
      message: 'Student registered successfully. Please proceed to login.',
      studentId: newStudent.studentId,
      student: newStudent
    });
  }

  // Mock Student Profile
  if (cleanEndpoint.includes('/students/profile') || cleanEndpoint.includes('/profile')) {
    if (method === 'PUT' || method === 'POST') {
      let bodyData = {};
      try { bodyData = JSON.parse(options.body || '{}'); } catch(e){}
      const current = JSON.parse(localStorage.getItem(CONFIG.STORAGE_KEYS.USER_DATA) || '{}');
      const updated = { ...current, ...bodyData };
      localStorage.setItem(CONFIG.STORAGE_KEYS.USER_DATA, JSON.stringify(updated));
      return Promise.resolve({
        success: true,
        message: 'Profile updated successfully!',
        student: updated
      });
    }

    const savedUser = localStorage.getItem(CONFIG.STORAGE_KEYS.USER_DATA);
    if (savedUser) {
      return Promise.resolve({ success: true, student: JSON.parse(savedUser) });
    }
    return Promise.resolve({
      success: true,
      student: {
        id: 101,
        name: 'Rahul Sharma',
        studentId: 'STU2024CS089',
        email: 'rahul.sharma@college.edu',
        college: 'National Institute of Engineering & Technology',
        department: 'Computer Science & Engineering',
        year: '3rd Year',
        bio: 'Aspiring Full-Stack & Java Backend Engineer with a strong passion for microservices, cloud systems, and scalable architecture.',
        careerInterests: 'Java Backend, Cloud Systems, Full-Stack Development'
      }
    });
  }

  // Mock Skills
  if (cleanEndpoint.includes('/skills')) {
    const savedSkills = localStorage.getItem(CONFIG.STORAGE_KEYS.MY_SKILLS);
    let mySkills = savedSkills ? JSON.parse(savedSkills) : [
      { id: 1, name: 'Java', category: 'Technical', proficiency: 'Advanced', experience: '1 - 2 Years' },
      { id: 2, name: 'Spring Boot', category: 'Technical', proficiency: 'Intermediate', experience: '6 - 12 Months' },
      { id: 3, name: 'SQL', category: 'Technical', proficiency: 'Intermediate', experience: '1 - 2 Years' },
      { id: 4, name: 'HTML', category: 'Technical', proficiency: 'Advanced', experience: '2+ Years' },
      { id: 5, name: 'CSS', category: 'Technical', proficiency: 'Intermediate', experience: '1 - 2 Years' },
      { id: 6, name: 'JavaScript', category: 'Technical', proficiency: 'Intermediate', experience: '1 - 2 Years' },
      { id: 7, name: 'Git', category: 'Technical', proficiency: 'Intermediate', experience: '1 - 2 Years' },
      { id: 8, name: 'Communication', category: 'Soft Skill', proficiency: 'Advanced', experience: '2+ Years' },
      { id: 9, name: 'Problem Solving', category: 'Soft Skill', proficiency: 'Advanced', experience: '2+ Years' }
    ];

    if (method === 'POST') {
      let newSkill = {};
      try { newSkill = JSON.parse(options.body || '{}'); } catch(e){}
      newSkill.id = Date.now();
      mySkills.push(newSkill);
      localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkills));
      return Promise.resolve({ success: true, message: 'Skill added', skill: newSkill });
    }

    if (method === 'DELETE') {
      const parts = endpoint.split('/');
      const skillId = parseInt(parts[parts.length - 1], 10);
      mySkills = mySkills.filter(s => s.id !== skillId);
      localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkills));
      return Promise.resolve({ success: true, message: 'Skill removed' });
    }

    return Promise.resolve({ success: true, skills: mySkills });
  }

  // Mock Jobs & Recommendations
  if (cleanEndpoint.includes('/jobs')) {
    return Promise.resolve({ success: true, jobs: MOCK_JOBS_DATABASE });
  }

  // Default fallback
  return Promise.resolve({ success: true, data: [] });
}

// Master Mock Jobs Database for fallback
const MOCK_JOBS_DATABASE = [
  {
    id: 1,
    title: 'Junior Java Backend Developer',
    company: 'Nexus Infotech Solutions',
    category: 'Backend',
    location: 'Bangalore / Hybrid',
    type: 'Full-Time (Entry Level)',
    salaryRange: '₹5,50,000 - ₹7,50,000 / year',
    description: 'We are seeking an enthusiastic Junior Java Developer to join our core banking microservices team. You will build resilient REST APIs, design database schemas, and participate in peer code reviews.',
    requiredSkills: ['Java', 'Spring Boot', 'SQL', 'Git', 'Data Structures'],
    preferredSkills: ['Docker', 'REST APIs', 'PostgreSQL', 'Unit Testing'],
    responsibilities: [
      'Design, develop, and maintain RESTful web services in Spring Boot',
      'Optimize database queries and schema designs using MySQL/PostgreSQL',
      'Collaborate with front-end engineers to integrate user-facing features',
      'Participate in agile sprints, daily standups, and design reviews'
    ]
  },
  {
    id: 2,
    title: 'Full Stack Java Engineer (Graduate)',
    company: 'AuraCloud Technologies',
    category: 'Full Stack',
    location: 'Hyderabad / On-site',
    type: 'Full-Time (Campus Placement)',
    salaryRange: '₹6,00,000 - ₹8,20,000 / year',
    description: 'Join our product engineering division to craft modern enterprise SaaS applications. You will work across the stack with Java Spring Boot backend and responsive modern web interfaces.',
    requiredSkills: ['Java', 'Spring Boot', 'JavaScript', 'HTML', 'CSS', 'SQL'],
    preferredSkills: ['React', 'GitHub', 'Problem Solving', 'Tailwind CSS'],
    responsibilities: [
      'Develop end-to-end features spanning database, backend APIs, and web frontend',
      'Ensure high code quality through automated unit and integration tests',
      'Diagnose and troubleshoot application bottlenecks and bugs'
    ]
  },
  {
    id: 3,
    title: 'Associate Software Engineer - Python & Data',
    company: 'QuantVantage Analytics',
    category: 'Data Science',
    location: 'Pune / Remote',
    type: 'Full-Time',
    salaryRange: '₹6,50,000 - ₹9,00,000 / year',
    description: 'Looking for fresh computer science graduates with sharp algorithmic aptitude to build data pipeline ingestion services and automated reporting pipelines.',
    requiredSkills: ['Python', 'SQL', 'Data Structures', 'Problem Solving', 'Git'],
    preferredSkills: ['Pandas', 'FastAPI', 'Linux', 'Communication'],
    responsibilities: [
      'Construct ETL workflows processing high-volume datasets',
      'Implement statistical models and database query optimizations',
      'Maintain reliable codebase documentation and automated CI tests'
    ]
  },
  {
    id: 4,
    title: 'Frontend Web Developer Trainee',
    company: 'Veloce Digital Media',
    category: 'Frontend',
    location: 'Chennai / Hybrid',
    type: 'Internship to Full-Time',
    salaryRange: '₹4,80,000 - ₹6,20,000 / year',
    description: 'Passionate about human-centered UI/UX and responsive web engineering? We are looking for a trainee frontend developer with strong CSS, HTML, and modern JS foundations.',
    requiredSkills: ['HTML', 'CSS', 'JavaScript', 'Git', 'Communication'],
    preferredSkills: ['React', 'TypeScript', 'Responsive Design', 'Figma'],
    responsibilities: [
      'Transform Figma design specs into pixel-perfect semantic HTML/CSS',
      'Connect frontend components to REST APIs and handle asynchronous state',
      'Optimize web performance, accessibility, and cross-browser stability'
    ]
  },
  {
    id: 5,
    title: 'Junior Cloud & DevOps Trainee',
    company: 'StratoSphere Cloud Labs',
    category: 'DevOps',
    location: 'Gurugram / On-site',
    type: 'Full-Time',
    salaryRange: '₹5,80,000 - ₹7,80,000 / year',
    description: 'Kickstart your career in cloud infrastructure and site reliability. Learn infrastructure as code, CI/CD pipelines, container orchestration, and cloud security monitoring.',
    requiredSkills: ['Git', 'GitHub', 'Linux', 'Problem Solving', 'Communication'],
    preferredSkills: ['Docker', 'AWS', 'Shell Scripting', 'Kubernetes'],
    responsibilities: [
      'Assist senior SREs in automating container deployment pipelines',
      'Monitor application logs, uptime alerts, and resource utilization',
      'Document configuration runbooks and disaster recovery protocols'
    ]
  },
  {
    id: 6,
    title: 'C++ Systems Engineering Intern',
    company: 'Vertex Real-Time Systems',
    category: 'Systems',
    location: 'Bangalore / On-site',
    type: 'Internship',
    salaryRange: '₹35,000 / month stipend',
    description: 'Work on low-latency memory management, operating system primitives, and high-performance algorithms for real-time sensor processing systems.',
    requiredSkills: ['C', 'C++', 'Data Structures', 'Problem Solving'],
    preferredSkills: ['Linux', 'Multithreading', 'Git', 'Algorithms'],
    responsibilities: [
      'Write safe, high-speed C++ modules for memory-constrained platforms',
      'Benchmark execution cycles and profile memory leakages',
      'Collaborate on embedded driver integration'
    ]
  },
  {
    id: 7,
    title: 'Generative AI & LLM Solutions Engineer',
    company: 'CogniNova AI Labs',
    category: 'AI & Data Science',
    location: 'Bangalore / Hybrid',
    type: 'Full-Time (Entry to Mid)',
    salaryRange: '₹7,50,000 - ₹11,00,000 / year',
    description: 'Build enterprise-grade AI assistants and automated workflow agents utilizing state-of-the-art Large Language Models, Retrieval-Augmented Generation (RAG), and vector embeddings.',
    requiredSkills: ['Python', 'Generative AI / LLMs', 'LangChain', 'RAG Architecture', 'Problem Solving'],
    preferredSkills: ['PyTorch', 'Prompt Engineering', 'Docker', 'FastAPI'],
    responsibilities: [
      'Implement RAG pipelines connecting proprietary documents to LLM inference endpoints',
      'Optimize prompt templates and evaluate hallucinations using automated benchmarks',
      'Deploy scalable Python API services for high-throughput model serving'
    ]
  },
  {
    id: 8,
    title: 'Cloud Platform & Kubernetes Engineer',
    company: 'ApexCloud Infrastructure',
    category: 'Cloud & DevOps',
    location: 'Hyderabad / Hybrid',
    type: 'Full-Time',
    salaryRange: '₹7,00,000 - ₹9,50,000 / year',
    description: 'Design and operate resilient multi-region cloud infrastructure, automated container orchestration, and continuous deployment workflows on AWS/Azure.',
    requiredSkills: ['AWS', 'Docker', 'Kubernetes', 'CI/CD Pipelines', 'Linux / Bash'],
    preferredSkills: ['Terraform', 'GitHub Actions', 'Python', 'Nginx'],
    responsibilities: [
      'Provision and manage infrastructure-as-code using Terraform and CloudFormation',
      'Scale containerized microservices clusters running on Amazon EKS',
      'Implement automated zero-downtime canary deployment pipelines'
    ]
  },
  {
    id: 9,
    title: 'Junior Mobile App Developer (Flutter / React Native)',
    company: 'PulseMobility Tech',
    category: 'Mobile',
    location: 'Pune / Remote',
    type: 'Full-Time',
    salaryRange: '₹5,20,000 - ₹7,20,000 / year',
    description: 'Craft sleek, cross-platform Android and iOS applications with butter-smooth animations, offline caching, and responsive UI components.',
    requiredSkills: ['Flutter', 'React Native', 'JavaScript', 'REST APIs', 'Git'],
    preferredSkills: ['TypeScript', 'Android (Kotlin)', 'iOS (Swift)', 'Team Collaboration'],
    responsibilities: [
      'Develop pixel-perfect cross-platform user interfaces in Flutter / React Native',
      'Integrate client-side local caching and background synchronization',
      'Publish and maintain builds for Google Play Console and Apple App Store'
    ]
  },
  {
    id: 10,
    title: 'Cybersecurity Analyst & SOC Trainee',
    company: 'Fortress Cyber Defenses',
    category: 'Cybersecurity',
    location: 'Mumbai / Hybrid',
    type: 'Full-Time (Entry Level)',
    salaryRange: '₹6,20,000 - ₹8,50,000 / year',
    description: 'Monitor real-time security alerts, analyze emerging network threats, conduct vulnerability assessments, and protect corporate digital infrastructure against cyber intrusions.',
    requiredSkills: ['Ethical Hacking', 'Network Security', 'OWASP Top 10', 'Linux / Bash', 'Problem Solving'],
    preferredSkills: ['Cryptography', 'Penetration Testing', 'Git', 'Technical Communication'],
    responsibilities: [
      'Investigate security incidents using SIEM log correlation tools',
      'Audit web application endpoints against OWASP Top 10 vulnerabilities',
      'Prepare comprehensive threat assessment reports and mitigation runbooks'
    ]
  },
  {
    id: 11,
    title: 'Modern React & Next.js Full-Stack Developer',
    company: 'PrismScale Innovations',
    category: 'Full Stack',
    location: 'Bangalore / Remote',
    type: 'Full-Time',
    salaryRange: '₹6,80,000 - ₹9,50,000 / year',
    description: 'Build fast, server-rendered web applications with Next.js App Router, TypeScript, and modern headless REST/GraphQL microservices for high-traffic consumer platforms.',
    requiredSkills: ['React', 'Next.js', 'TypeScript', 'Tailwind CSS', 'Node.js', 'REST APIs'],
    preferredSkills: ['GraphQL', 'PostgreSQL', 'Docker', 'Agile & Scrum'],
    responsibilities: [
      'Engineer modular React/Next.js components with rigorous TypeScript safety',
      'Optimize Core Web Vitals, server-side rendering (SSR), and incremental static regeneration (ISR)',
      'Design clean REST API contracts and coordinate with backend engineering squads'
    ]
  },
  {
    id: 12,
    title: 'Data Analyst & Power BI Associate',
    company: 'InsightEdge Analytics Global',
    category: 'AI & Data Science',
    location: 'Gurugram / Hybrid',
    type: 'Full-Time',
    salaryRange: '₹5,80,000 - ₹7,80,000 / year',
    description: 'Transform complex enterprise data lakes into actionable visual intelligence, interactive executive dashboards, and automated trend predictions.',
    requiredSkills: ['SQL', 'Power BI', 'Data Analytics', 'Pandas', 'Problem Solving'],
    preferredSkills: ['Python', 'Tableau', 'Technical Communication', 'NumPy'],
    responsibilities: [
      'Write complex SQL queries, window functions, and data aggregation scripts',
      'Build dynamic Power BI dashboards with drill-through parameters and DAX formulas',
      'Present quantitative findings and business insights directly to project leads'
    ]
  },
  {
    id: 13,
    title: 'Campus Trainee - Associate Software Engineer (Digital)',
    company: 'Tata Consultancy Services (Digital Wing)',
    category: 'Software Engineering',
    location: 'Pan-India / Multiple Centers',
    type: 'Campus Placement (2025/2026 Batch)',
    salaryRange: '₹7,00,000 - ₹9,00,000 / year',
    description: 'Prestigious national digital development role for top engineering graduates. Rotational exposure across cloud-native development, algorithmic computing, and large-scale enterprise modernization.',
    requiredSkills: ['Data Structures', 'Algorithms', 'Object-Oriented Programming', 'Java', 'Problem Solving', 'Technical Communication'],
    preferredSkills: ['SQL', 'Git', 'Linux / Bash', 'Team Collaboration'],
    responsibilities: [
      'Develop algorithmic business logic adhering to strict enterprise design patterns',
      'Participate in agile sprints, technical code walkthroughs, and peer programming',
      'Design unit tests ensuring over 85% code coverage for mission-critical software'
    ]
  },
  {
    id: 14,
    title: 'Backend Microservices Engineer (Go / Golang & Distributed Systems)',
    company: 'HyperStream FinTech',
    category: 'Backend',
    location: 'Bangalore / On-site',
    type: 'Full-Time',
    salaryRange: '₹8,00,000 - ₹12,00,000 / year',
    description: 'Architect and scale ultra-low-latency financial transaction pipelines handling millions of daily events using Go, asynchronous event brokers, and memory caches.',
    requiredSkills: ['Go (Golang)', 'Microservices', 'System Design', 'Docker', 'PostgreSQL'],
    preferredSkills: ['Apache Kafka', 'Redis', 'Kubernetes', 'Git'],
    responsibilities: [
      'Implement concurrency-safe Go routines processing streaming financial payments',
      'Architect event-driven pipelines using Apache Kafka and Redis cluster caching',
      'Monitor distributed tracing metrics and resolve p99 latency regressions'
    ]
  }
];

// Verified Learning Resources Database
const VERIFIED_LEARNING_RESOURCES = [
  {
    id: 1,
    skill: 'Java',
    title: 'Official Java Tutorials & Language Documentation',
    provider: 'Oracle Corporation',
    url: 'https://docs.oracle.com/javase/tutorial/',
    status: 'Free',
    level: 'Beginner to Advanced',
    description: 'Comprehensive, authoritative practical guide covering Java object-oriented principles, generics, collections, and concurrency.'
  },
  {
    id: 2,
    skill: 'Spring Boot',
    title: 'Building a RESTful Web Service with Spring Boot',
    provider: 'Spring.io Official Guides',
    url: 'https://spring.io/guides/gs/rest-service',
    status: 'Free',
    level: 'Beginner',
    description: 'Step-by-step hands-on tutorial to spin up your first production-grade Spring Boot REST API in 15 minutes.'
  },
  {
    id: 3,
    skill: 'SQL',
    title: 'SQL Tutorial & Interactive Practice',
    provider: 'W3Schools / SQL Zoo',
    url: 'https://www.w3schools.com/sql/',
    status: 'Free',
    level: 'Beginner',
    description: 'Learn SQL database queries, JOINs, aggregate functions, indexes, and schema definitions with live sandboxes.'
  },
  {
    id: 4,
    skill: 'Git',
    title: 'Pro Git Book (Complete Official Edition)',
    provider: 'Git SCM Project',
    url: 'https://git-scm.com/book/en/v2',
    status: 'Free',
    level: 'Beginner to Intermediate',
    description: 'The definitive open-source guide to distributed version control, branching models, commits, and collaborative workflows.'
  },
  {
    id: 5,
    skill: 'GitHub',
    title: 'GitHub Skills - Interactive Learning Lab',
    provider: 'GitHub Education',
    url: 'https://skills.github.com/',
    status: 'Free',
    level: 'Beginner',
    description: 'Interactive browser-based exercises for managing pull requests, merge conflict resolution, and GitHub Actions.'
  },
  {
    id: 6,
    skill: 'Data Structures',
    title: 'Data Structures and Algorithms Specialization',
    provider: 'GeeksforGeeks / Coursera',
    url: 'https://www.geeksforgeeks.org/data-structures/',
    status: 'Free',
    level: 'Intermediate',
    description: 'Curated references on arrays, linked lists, trees, graphs, sorting, searching, and time-complexity analysis.'
  },
  {
    id: 7,
    skill: 'Docker',
    title: 'Docker Get Started & Container Orientation',
    provider: 'Docker Official Docs',
    url: 'https://docs.docker.com/get-started/',
    status: 'Free',
    level: 'Beginner',
    description: 'Understand containerization, Dockerfiles, images, containers, and multi-service Docker Compose networks.'
  },
  {
    id: 8,
    skill: 'JavaScript',
    title: 'JavaScript Guide & Modern Web Standards',
    provider: 'MDN Web Docs (Mozilla)',
    url: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide',
    status: 'Free',
    level: 'Beginner to Advanced',
    description: 'The web industry standard documentation for JavaScript syntax, DOM manipulation, Promises, and fetch API.'
  },
  {
    id: 9,
    skill: 'Python',
    title: 'The Python Tutorial (Official Docs)',
    provider: 'Python Software Foundation',
    url: 'https://docs.python.org/3/tutorial/',
    status: 'Free',
    level: 'Beginner',
    description: 'Direct introductory guide to Python syntax, data structures, modules, virtual environments, and exceptions.'
  },
  {
    id: 10,
    skill: 'React',
    title: 'React Documentation (Quick Start)',
    provider: 'React.dev (Meta Open Source)',
    url: 'https://react.dev/learn',
    status: 'Free',
    level: 'Beginner to Intermediate',
    description: 'Official interactive documentation teaching modern functional components, state hooks, and component life-cycles.'
  },
  {
    id: 11,
    skill: 'Problem Solving',
    title: 'Problem Solving & Algorithmic Practice',
    provider: 'HackerRank / LeetCode',
    url: 'https://www.hackerrank.com/domains/tutorials/10-days-of-javascript',
    status: 'Free',
    level: 'Beginner to Advanced',
    description: 'Hands-on coding challenges designed to sharpen computational thinking and technical interview readiness.'
  },
  {
    id: 12,
    skill: 'Communication',
    title: 'Technical Communication & Team Collaboration',
    provider: 'Google Tech Writing / Coursera',
    url: 'https://developers.google.com/tech-writing',
    status: 'Free',
    level: 'Beginner',
    description: 'Learn clear technical writing, code review etiquette, and cross-functional engineering communication.'
  }
];
