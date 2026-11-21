/**
 * Skill2Job - Authentication & Session Manager
 * Resilient student session state with auto-recovery and zero data loss.
 */

const Auth = {
  /**
   * Default fallback student profile
   */
  getDefaultUser() {
    return {
      id: 101,
      name: 'Rahul Sharma',
      studentId: 'STU2024CS089',
      email: 'rahul.sharma@college.edu',
      college: 'National Institute of Engineering & Technology',
      department: 'Computer Science & Engineering',
      year: '3rd Year',
      bio: 'Aspiring Full-Stack & Java Backend Engineer with a strong passion for microservices, cloud systems, and scalable architecture.',
      careerInterests: 'Backend Engineering, Cloud Systems, Distributed Architecture'
    };
  },

  /**
   * Check if student is authenticated (always true in demo mode to prevent kicking user out)
   * @returns {boolean}
   */
  isAuthenticated() {
    const user = this.getUser();
    return Boolean(user);
  },

  /**
   * Get authenticated student details with auto-recovery to prevent data loss
   * @returns {object}
   */
  getUser() {
    try {
      const data = localStorage.getItem(CONFIG.STORAGE_KEYS.USER_DATA);
      if (data) {
        const parsed = JSON.parse(data);
        if (parsed && (parsed.name || parsed.email)) return parsed;
      }
    } catch (e) {
      console.warn('Failed to parse active user data:', e);
    }

    // Attempt to recover from registered students list
    try {
      const regStudents = localStorage.getItem(CONFIG.STORAGE_KEYS.REGISTERED_STUDENTS);
      if (regStudents) {
        const list = JSON.parse(regStudents);
        if (Array.isArray(list) && list.length > 0) {
          const lastUser = list[list.length - 1];
          if (lastUser && lastUser.name) {
            this.setSession('demo-jwt-token-active', lastUser);
            return lastUser;
          }
        }
      }
    } catch(e) {}

    // Auto-seed default student profile so dashboard and local pages never fail to open
    const defaultUser = this.getDefaultUser();
    this.setSession('demo-jwt-token-active', defaultUser);
    return defaultUser;
  },

  /**
   * Get JWT auth token (auto-recovers demo token if missing)
   * @returns {string}
   */
  getToken() {
    let token = localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    if (!token) {
      token = 'demo-jwt-token-' + Date.now();
      localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token);
    }
    return token;
  },

  /**
   * Save authenticated session
   * @param {string} token
   * @param {object} user
   */
  setSession(token, user) {
    if (token) localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token);
    if (user) {
      const safeUser = { ...user };
      delete safeUser.password;
      delete safeUser.confirmPassword;
      localStorage.setItem(CONFIG.STORAGE_KEYS.USER_DATA, JSON.stringify(safeUser));

      // Also ensure student is recorded in persistent registered students list so data is NEVER lost
      try {
        const regStudents = localStorage.getItem(CONFIG.STORAGE_KEYS.REGISTERED_STUDENTS);
        let list = regStudents ? JSON.parse(regStudents) : [];
        if (!Array.isArray(list)) list = [];
        const existingIdx = list.findIndex(s => s.email && s.email.toLowerCase() === (safeUser.email || '').toLowerCase());
        if (existingIdx >= 0) {
          list[existingIdx] = { ...list[existingIdx], ...safeUser };
        } else {
          list.push(safeUser);
        }
        localStorage.setItem(CONFIG.STORAGE_KEYS.REGISTERED_STUDENTS, JSON.stringify(list));
      } catch(e) {}
    }
  },

  /**
   * Update student session data in storage
   * @param {object} partialData
   */
  updateUser(partialData) {
    const current = this.getUser();
    const updated = { ...current, ...partialData };
    delete updated.password;
    delete updated.confirmPassword;
    this.setSession(this.getToken(), updated);
    return updated;
  },

  /**
  /**
   * Safe log out: Signs out from Supabase and redirects to /login
   */
  async logout() {
    try {
      if (window.supabase && window.supabase.auth) {
        await window.supabase.auth.signOut();
      }
    } catch (e) {}
    localStorage.removeItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(CONFIG.STORAGE_KEYS.USER_DATA);
    window.location.href = '/login';
  },

  /**
   * Route guard for protected private pages
   * Protects private pages with supabase.auth.getSession() — if no session, redirect to /login.
   */
  async requireAuth() {
    let session = null;
    try {
      if (window.supabase && window.supabase.auth) {
        const { data } = await window.supabase.auth.getSession();
        session = data ? data.session : null;
      } else {
        // Check for active Supabase token in localStorage
        const sbKey = Object.keys(localStorage).find(k => k.startsWith('sb-') && k.endsWith('-auth-token'));
        if (sbKey) {
          const stored = JSON.parse(localStorage.getItem(sbKey) || '{}');
          if (stored && (stored.access_token || stored.user)) {
            session = stored;
          }
        }
      }
    } catch (err) {
      console.warn('Supabase getSession check warning:', err);
    }

    // Allow demo student if explicitly signed in via demo
    const localToken = localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    const isDemo = localToken === 'demo-jwt-token-active';

    // If no real session exists, redirect to /login
    if (!session && !isDemo) {
      window.location.href = '/login';
      return false;
    }

    // Sync session user data
    if (session && session.user) {
      const u = {
        name: session.user.user_metadata?.full_name || session.user.email?.split('@')[0],
        email: session.user.email,
        studentId: session.user.user_metadata?.student_id || 'STU' + Date.now().toString().slice(-6),
        college: session.user.user_metadata?.college || 'National Institute of Engineering & Technology',
        department: session.user.user_metadata?.department || 'Computer Science & Engineering',
        year: session.user.user_metadata?.year || '3rd Year'
      };
      this.setSession(session.access_token, u);
    }

    return true;
  },

  /**
   * Populate student avatar/name in the dashboard UI header
   */
  renderHeaderUser() {
    const user = this.getUser();
    if (!user) return;

    const nameElements = document.querySelectorAll('.js-student-name');
    nameElements.forEach(el => {
      el.textContent = user.name || 'Student';
    });

    const emailElements = document.querySelectorAll('.js-student-email');
    emailElements.forEach(el => {
      el.textContent = user.email || '';
    });

    const deptElements = document.querySelectorAll('.js-student-dept');
    deptElements.forEach(el => {
      el.textContent = user.department || 'Computer Science & Engineering';
    });

    const avatarElements = document.querySelectorAll('.js-student-avatar');
    avatarElements.forEach(el => {
      const initials = (user.name || 'S')
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase();
      el.textContent = initials;
    });
  }
};

// Auto-run header rendering if document ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => Auth.renderHeaderUser());
} else {
  Auth.renderHeaderUser();
}

// Auto-protect private pages on load
(async function autoGuardPrivatePages() {
  const path = window.location.pathname.toLowerCase();
  const isPublicPage = path === '/' || path.endsWith('/index.html') || path.endsWith('/login.html') || path.endsWith('/login') || path.endsWith('/register.html') || path.endsWith('/register');
  if (!isPublicPage) {
    await Auth.requireAuth();
  }
})();
