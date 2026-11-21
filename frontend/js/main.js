/**
 * Skill2Job - Global UI & Application Helpers
 * Toast notifications, modal handling, match calculation engine, and backend status check.
 */

const App = {
  /**
   * Display a floating toast notification
   * @param {string} message
   * @param {'success'|'error'|'warning'|'info'} type
   * @param {number} duration
   */
  showToast(message, type = 'info', duration = 4000) {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      container.setAttribute('aria-live', 'polite');
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast-item toast-${type}`;
    
    // Choose icon based on type
    let iconSvg = '';
    if (type === 'success') {
      iconSvg = `<svg class="toast-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>`;
    } else if (type === 'error') {
      iconSvg = `<svg class="toast-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/></svg>`;
    } else if (type === 'warning') {
      iconSvg = `<svg class="toast-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>`;
    } else {
      iconSvg = `<svg class="toast-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/></svg>`;
    }

    toast.innerHTML = `
      ${iconSvg}
      <span class="toast-msg">${escapeHtml(message)}</span>
      <button class="toast-close" aria-label="Close message">&times;</button>
    `;

    toast.querySelector('.toast-close').addEventListener('click', () => {
      toast.classList.add('toast-exit');
      setTimeout(() => toast.remove(), 250);
    });

    container.appendChild(toast);

    setTimeout(() => {
      if (toast.parentElement) {
        toast.classList.add('toast-exit');
        setTimeout(() => toast.remove(), 250);
      }
    }, duration);
  },

  /**
   * Calculate match details between a job's requirements and a student's skills
   * @param {Array<string>} requiredSkills
   * @param {Array<object|string>} studentSkills
   * @returns {{ matchPercentage: number, matchedSkills: string[], missingSkills: string[] }}
   */
  calculateSkillMatch(requiredSkills = [], studentSkills = []) {
    if (!requiredSkills || requiredSkills.length === 0) {
      return { matchPercentage: 100, matchedSkills: [], missingSkills: [] };
    }

    // Normalize student skill names to lowercase
    const studentSkillNames = studentSkills.map(s => {
      if (typeof s === 'string') return s.trim().toLowerCase();
      return (s.name || '').trim().toLowerCase();
    });

    const matchedSkills = [];
    const missingSkills = [];

    requiredSkills.forEach(req => {
      const normalizedReq = req.trim().toLowerCase();
      if (studentSkillNames.includes(normalizedReq)) {
        matchedSkills.push(req);
      } else {
        missingSkills.push(req);
      }
    });

    const matchPercentage = Math.round((matchedSkills.length / requiredSkills.length) * 100);

    return {
      matchPercentage,
      matchedSkills,
      missingSkills
    };
  },

  /**
   * Helper to retrieve student's current skills from cache or fallback
   */
  getStudentSkills() {
    try {
      const data = localStorage.getItem(CONFIG.STORAGE_KEYS.MY_SKILLS);
      if (data) return JSON.parse(data);
    } catch(e){}
    // Default initial skills
    return [
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
  },

  /**
   * Initialize mobile navigation and sidebar toggles
   */
  initNavbars() {
    // Landing page mobile menu toggle
    const mobileMenuBtn = document.getElementById('mobile-menu-btn');
    const mobileNav = document.getElementById('mobile-nav');
    if (mobileMenuBtn && mobileNav) {
      mobileMenuBtn.addEventListener('click', () => {
        const isExpanded = mobileNav.classList.toggle('active');
        mobileMenuBtn.setAttribute('aria-expanded', isExpanded);
        mobileMenuBtn.classList.toggle('is-active');
      });
    }

    // Dashboard mobile sidebar toggle
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const dashboardSidebar = document.querySelector('.dashboard-sidebar');
    const sidebarBackdrop = document.getElementById('sidebar-backdrop');
    
    if (sidebarToggle && dashboardSidebar) {
      sidebarToggle.addEventListener('click', () => {
        dashboardSidebar.classList.toggle('open');
        if (sidebarBackdrop) sidebarBackdrop.classList.toggle('active');
      });
    }

    if (sidebarBackdrop) {
      sidebarBackdrop.addEventListener('click', () => {
        if (dashboardSidebar) dashboardSidebar.classList.remove('open');
        sidebarBackdrop.classList.remove('active');
      });
    }

    // 21st.dev Collapsible Sidebar Toggle & State Memory
    const collapseToggle = document.getElementById('sidebar-collapse-toggle');
    if (collapseToggle && dashboardSidebar) {
      const isCollapsedSaved = localStorage.getItem('skill2job_sidebar_collapsed') === 'true';
      if (isCollapsedSaved && window.innerWidth > 1024) {
        dashboardSidebar.classList.add('is-collapsed');
        document.body.classList.add('sidebar-collapsed');
        dashboardSidebar.setAttribute('data-collapsed', 'true');
      }

      collapseToggle.addEventListener('click', () => {
        const isCollapsed = dashboardSidebar.classList.toggle('is-collapsed');
        document.body.classList.toggle('sidebar-collapsed', isCollapsed);
        dashboardSidebar.setAttribute('data-collapsed', isCollapsed ? 'true' : 'false');
        localStorage.setItem('skill2job_sidebar_collapsed', isCollapsed);
      });

      // Keyboard shortcut Ctrl+B / Cmd+B to toggle sidebar collapse
      window.addEventListener('keydown', (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'b') {
          e.preventDefault();
          collapseToggle.click();
        }
      });
    }

    // Setup global logout triggers
    document.querySelectorAll('.js-logout-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
      });
    });

    // Check backend health & show notice if in mock fallback
    this.checkBackendHealth();

    // Initialize 21st.dev tactile button animations & ripple effects
    this.initButtonAnimations();

    // Initialize sidebar search & user hydration
    this.initSidebarSearch();
    this.hydrateSidebarProfile();
  },

  /**
   * 21st.dev Button Click Animation & Ripple Engine
   */
  initButtonAnimations() {
    document.addEventListener('pointerdown', (e) => {
      const btn = e.target.closest('.btn, .sidebar-link, .category-card, .metric-card, .quick-chip, .tag-pill');
      if (!btn) return;

      const rect = btn.getBoundingClientRect();
      const ripple = document.createElement('span');
      ripple.className = 'click-ripple';
      const size = Math.max(rect.width, rect.height) * 1.5;
      ripple.style.width = `${size}px`;
      ripple.style.height = `${size}px`;
      ripple.style.left = `${e.clientX - rect.left - size / 2}px`;
      ripple.style.top = `${e.clientY - rect.top - size / 2}px`;

      // If button position is static, ensure relative for absolute ripple positioning
      const pos = window.getComputedStyle(btn).position;
      if (pos === 'static') btn.style.position = 'relative';

      btn.appendChild(ripple);
      setTimeout(() => ripple.remove(), 600);
    });
  },

  /**
   * 21st.dev Component 28489: Sidebar Search with Keyboard Shortcut (Ctrl+K)
   */
  initSidebarSearch() {
    const searchInput = document.getElementById('sidebar-search-input');
    if (!searchInput) return;

    // Keyboard shortcut handler (Ctrl+K / Cmd+K)
    window.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        searchInput.focus();
        searchInput.select();
      }
    });

    // Real-time navigation filter
    searchInput.addEventListener('input', (e) => {
      const query = e.target.value.trim().toLowerCase();
      const sidebarLinks = document.querySelectorAll('.dashboard-sidebar .sidebar-link');
      
      sidebarLinks.forEach(link => {
        const text = link.textContent.toLowerCase();
        if (!query || text.includes(query)) {
          link.style.display = 'flex';
        } else {
          link.style.display = 'none';
        }
      });
    });

    // Enter triggers quick job search jump
    searchInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && searchInput.value.trim()) {
        window.location.href = `jobs.html?keyword=${encodeURIComponent(searchInput.value.trim())}`;
      }
    });
  },

  /**
   * Hydrates Sidebar Profile with Logged-in Student Info
   */
  hydrateSidebarProfile() {
    if (typeof Auth === 'undefined') return;
    const user = Auth.getUser();
    if (!user) return;

    const nameEl = document.getElementById('sidebar-student-name');
    const deptEl = document.getElementById('sidebar-student-dept');
    const avatarEl = document.getElementById('sidebar-student-avatar');

    if (nameEl) nameEl.textContent = user.name || 'Student';
    if (deptEl) deptEl.textContent = user.department ? `${user.department} • ${user.yearOfStudy || ''}` : 'Computer Science';
    if (avatarEl && user.name) {
      const parts = user.name.trim().split(' ');
      const initials = parts.length > 1 ? (parts[0][0] + parts[1][0]).toUpperCase() : parts[0].substring(0, 2).toUpperCase();
      avatarEl.textContent = initials;
    }
  },

  /**
   * Detects backend connectivity to provide live vs demo mode visual badge
   */
  async checkBackendHealth() {
    const badgeContainer = document.getElementById('backend-status-pill');
    if (!badgeContainer) return;

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const res = await fetch(`${CONFIG.API_BASE_URL}/health`, { signal: controller.signal });
      clearTimeout(timeoutId);

      if (res.ok) {
        badgeContainer.innerHTML = `
          <span class="status-dot dot-online"></span>
          <span>Backend Online</span>
        `;
        badgeContainer.title = `Connected to Java Spring Boot (${CONFIG.API_BASE_URL})`;
      } else {
        throw new Error('Not 200');
      }
    } catch (e) {
      badgeContainer.innerHTML = `
        <span class="status-dot dot-demo"></span>
        <span>Demo Mode (Mock Data)</span>
      `;
      badgeContainer.title = `Spring Boot backend is not detected at ${CONFIG.API_BASE_URL}. Using built-in student dataset.`;
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  App.initNavbars();
});
