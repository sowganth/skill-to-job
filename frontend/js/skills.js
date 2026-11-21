/**
 * Skill2Job - Skills Management Controller
 * Handles adding, deleting, categorizing, and searching student skills.
 */

let mySkillsList = [];
let currentCategoryFilter = 'ALL';
let currentSearchQuery = '';

let selectedDomains = new Set(['ALL']);
let currentQuickPickSearch = '';

// Comprehensive Real-World Industry Skills Catalogue (Current 2025/2026 In-Demand Requirements)
const SKILL_CATALOGUE = [
  // 1. Web & Full-Stack Development
  { name: 'React', category: 'Technical', domain: 'web' },
  { name: 'Next.js', category: 'Technical', domain: 'web' },
  { name: 'TypeScript', category: 'Technical', domain: 'web' },
  { name: 'JavaScript', category: 'Technical', domain: 'web' },
  { name: 'HTML5', category: 'Technical', domain: 'web' },
  { name: 'CSS3', category: 'Technical', domain: 'web' },
  { name: 'Tailwind CSS', category: 'Technical', domain: 'web' },
  { name: 'Node.js', category: 'Technical', domain: 'web' },
  { name: 'Express.js', category: 'Technical', domain: 'web' },
  { name: 'REST APIs', category: 'Technical', domain: 'web' },
  { name: 'GraphQL', category: 'Technical', domain: 'web' },
  { name: 'Vue.js', category: 'Technical', domain: 'web' },
  { name: 'Angular', category: 'Technical', domain: 'web' },

  // 2. Backend & Enterprise Systems
  { name: 'Java', category: 'Technical', domain: 'backend' },
  { name: 'Spring Boot', category: 'Technical', domain: 'backend' },
  { name: 'Python', category: 'Technical', domain: 'backend' },
  { name: 'FastAPI', category: 'Technical', domain: 'backend' },
  { name: 'Django', category: 'Technical', domain: 'backend' },
  { name: 'C++', category: 'Technical', domain: 'backend' },
  { name: 'C#', category: 'Technical', domain: 'backend' },
  { name: '.NET Core', category: 'Technical', domain: 'backend' },
  { name: 'Go (Golang)', category: 'Technical', domain: 'backend' },
  { name: 'Microservices', category: 'Technical', domain: 'backend' },
  { name: 'System Design', category: 'Technical', domain: 'backend' },
  { name: 'SQL', category: 'Technical', domain: 'backend' },
  { name: 'PostgreSQL', category: 'Technical', domain: 'backend' },
  { name: 'MySQL', category: 'Technical', domain: 'backend' },
  { name: 'MongoDB', category: 'Technical', domain: 'backend' },
  { name: 'Redis', category: 'Technical', domain: 'backend' },
  { name: 'Apache Kafka', category: 'Technical', domain: 'backend' },

  // 3. Cloud, DevOps & Site Reliability (SRE)
  { name: 'AWS', category: 'Technical', domain: 'cloud' },
  { name: 'Microsoft Azure', category: 'Technical', domain: 'cloud' },
  { name: 'Google Cloud (GCP)', category: 'Technical', domain: 'cloud' },
  { name: 'Docker', category: 'Technical', domain: 'cloud' },
  { name: 'Kubernetes', category: 'Technical', domain: 'cloud' },
  { name: 'CI/CD Pipelines', category: 'Technical', domain: 'cloud' },
  { name: 'Linux / Bash', category: 'Technical', domain: 'cloud' },
  { name: 'Terraform', category: 'Technical', domain: 'cloud' },
  { name: 'GitHub Actions', category: 'Technical', domain: 'cloud' },
  { name: 'Git', category: 'Technical', domain: 'cloud' },
  { name: 'GitHub', category: 'Technical', domain: 'cloud' },
  { name: 'Nginx', category: 'Technical', domain: 'cloud' },

  // 4. AI, Machine Learning & Data Science (Current Market Requirement)
  { name: 'Generative AI / LLMs', category: 'Technical', domain: 'ai' },
  { name: 'Prompt Engineering', category: 'Technical', domain: 'ai' },
  { name: 'LangChain', category: 'Technical', domain: 'ai' },
  { name: 'RAG Architecture', category: 'Technical', domain: 'ai' },
  { name: 'PyTorch', category: 'Technical', domain: 'ai' },
  { name: 'TensorFlow', category: 'Technical', domain: 'ai' },
  { name: 'Scikit-Learn', category: 'Technical', domain: 'ai' },
  { name: 'Pandas', category: 'Technical', domain: 'ai' },
  { name: 'NumPy', category: 'Technical', domain: 'ai' },
  { name: 'Data Analytics', category: 'Technical', domain: 'ai' },
  { name: 'Power BI', category: 'Technical', domain: 'ai' },
  { name: 'Tableau', category: 'Technical', domain: 'ai' },
  { name: 'Computer Vision', category: 'Technical', domain: 'ai' },
  { name: 'Natural Language Processing', category: 'Technical', domain: 'ai' },

  // 5. Mobile & App Engineering
  { name: 'Flutter', category: 'Technical', domain: 'mobile' },
  { name: 'React Native', category: 'Technical', domain: 'mobile' },
  { name: 'Android (Kotlin)', category: 'Technical', domain: 'mobile' },
  { name: 'iOS (Swift)', category: 'Technical', domain: 'mobile' },

  // 6. Cybersecurity & Networks
  { name: 'Ethical Hacking', category: 'Technical', domain: 'security' },
  { name: 'Network Security', category: 'Technical', domain: 'security' },
  { name: 'OWASP Top 10', category: 'Technical', domain: 'security' },
  { name: 'Cryptography', category: 'Technical', domain: 'security' },
  { name: 'Penetration Testing', category: 'Technical', domain: 'security' },

  // 7. Core Computer Science Fundamentals
  { name: 'Data Structures', category: 'Technical', domain: 'cs' },
  { name: 'Algorithms', category: 'Technical', domain: 'cs' },
  { name: 'Object-Oriented Programming', category: 'Technical', domain: 'cs' },
  { name: 'Database Management (DBMS)', category: 'Technical', domain: 'cs' },
  { name: 'Operating Systems', category: 'Technical', domain: 'cs' },
  { name: 'Computer Networks', category: 'Technical', domain: 'cs' },
  { name: 'C Language', category: 'Technical', domain: 'cs' },

  // 8. Workplace & Professional Soft Skills
  { name: 'Problem Solving', category: 'Soft Skill', domain: 'soft' },
  { name: 'Technical Communication', category: 'Soft Skill', domain: 'soft' },
  { name: 'Team Collaboration', category: 'Soft Skill', domain: 'soft' },
  { name: 'Critical Thinking', category: 'Soft Skill', domain: 'soft' },
  { name: 'Agile & Scrum', category: 'Soft Skill', domain: 'soft' },
  { name: 'Adaptability', category: 'Soft Skill', domain: 'soft' },
  { name: 'Time Management', category: 'Soft Skill', domain: 'soft' },
  { name: 'Leadership', category: 'Soft Skill', domain: 'soft' }
];

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.requireAuth()) return;

  setupCatalogueChips();
  setupAddSkillForm();
  setupFilters();

  await loadSkills();
});

/**
 * Fetch current student skills from API
 */
async function loadSkills() {
  const loading = document.getElementById('skills-loading');
  const listContainer = document.getElementById('skills-table-body');

  try {
    if (loading) loading.style.display = 'flex';

    const res = await apiFetch('/skills');
    mySkillsList = (res && res.skills && res.skills.length > 0) ? res.skills : App.getStudentSkills();
    localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));

    renderSkillsTable();
    updateCatalogueChipStates();
  } catch (err) {
    console.error('Error fetching skills:', err);
    mySkillsList = App.getStudentSkills();
    localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));
    renderSkillsTable();
    updateCatalogueChipStates();
  } finally {
    if (loading) loading.style.display = 'none';
  }
}

/**
 * Render chips filtered by active domain tab and search keyword
 */
function renderCatalogueChips() {
  const container = document.getElementById('catalogue-chips-container');
  if (!container) return;

  const isAllSelected = selectedDomains.has('ALL') || selectedDomains.size === 0;

  const filtered = SKILL_CATALOGUE.filter(item => {
    const matchDomain = isAllSelected || selectedDomains.has(item.domain);
    const matchSearch = !currentQuickPickSearch || item.name.toLowerCase().includes(currentQuickPickSearch);
    return matchDomain && matchSearch;
  });

  // Dynamically update the divider label and count badge
  const nameEl = document.getElementById('active-domain-name');
  const countEl = document.getElementById('domain-skills-count');

  if (nameEl) {
    if (isAllSelected) {
      nameEl.textContent = 'All Available Skills';
    } else {
      const activeTabs = Array.from(document.querySelectorAll('.domain-tab.active'))
        .filter(t => t.getAttribute('data-domain') !== 'ALL');
      const domainNames = activeTabs.map(t => t.textContent.trim().replace(/\s*\(\d+\+?\)/, ''));
      if (domainNames.length === 1) {
        nameEl.textContent = `${domainNames[0]} Skills`;
      } else if (domainNames.length === 2) {
        nameEl.textContent = `${domainNames[0]} & ${domainNames[1]}`;
      } else {
        nameEl.textContent = `${domainNames.length} Domains Combined`;
      }
    }
  }

  if (countEl) {
    countEl.textContent = `${filtered.length} Skills`;
  }

  if (filtered.length === 0) {
    container.innerHTML = `
      <div style="font-size:0.875rem; color:var(--text-muted); padding:0.75rem 0.25rem;">
        No skills match "${escapeHtml(currentQuickPickSearch)}" in the selected domains. Try selecting "All Domains" or clearing the search.
      </div>
    `;
    return;
  }

  container.innerHTML = filtered.map(item => `
    <button type="button" class="catalogue-chip" data-skill="${escapeHtml(item.name)}" data-cat="${escapeHtml(item.category)}" data-domain="${escapeHtml(item.domain)}">
      <span class="chip-status-icon"></span>
      <span class="chip-name">${escapeHtml(item.name)}</span>
    </button>
  `).join('');

  container.querySelectorAll('.catalogue-chip').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.preventDefault();
      const skillName = btn.getAttribute('data-skill');
      const cat = btn.getAttribute('data-cat');
      await handleCatalogueChipClick(skillName, cat, btn);
    });
  });

  updateCatalogueChipStates();
}

/**
 * Setup multi-select domain filter buttons and search box
 */
function setupCatalogueChips() {
  const allTabs = document.querySelectorAll('.domain-tab');

  allTabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      e.preventDefault();
      const domain = tab.getAttribute('data-domain');

      if (domain === 'ALL') {
        // Clicking "All Domains" resets selection to ALL only
        selectedDomains.clear();
        selectedDomains.add('ALL');
        allTabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
      } else {
        // Clicking a specific domain toggles it on/off (multi-select)
        if (selectedDomains.has('ALL')) {
          selectedDomains.delete('ALL');
          const allTab = document.querySelector('.domain-tab[data-domain="ALL"]');
          if (allTab) allTab.classList.remove('active');
        }

        if (selectedDomains.has(domain)) {
          selectedDomains.delete(domain);
          tab.classList.remove('active');
        } else {
          selectedDomains.add(domain);
          tab.classList.add('active');
        }

        // If user unselected all specific domains, revert back to ALL
        if (selectedDomains.size === 0) {
          selectedDomains.add('ALL');
          const allTab = document.querySelector('.domain-tab[data-domain="ALL"]');
          if (allTab) allTab.classList.add('active');
        }
      }

      renderCatalogueChips();
    });
  });

  // Quick Pick search input
  const quickSearch = document.getElementById('quick-pick-search');
  if (quickSearch) {
    quickSearch.addEventListener('input', (e) => {
      currentQuickPickSearch = e.target.value.trim().toLowerCase();
      renderCatalogueChips();
    });
  }

  renderCatalogueChips();
}

/**
 * Direct Touch Handler: Instantly adds or toggles skill in "My Skills"
 */
async function handleCatalogueChipClick(skillName, cat, btn) {
  if (!skillName) return;

  // Trigger pop bounce animation on touch
  btn.classList.remove('chip-anim-pop');
  void btn.offsetWidth; // Force CSS reflow
  btn.classList.add('chip-anim-pop');

  // Also pre-fill form inputs for convenience
  const nameInput = document.getElementById('skillName');
  const catSelect = document.getElementById('skillCategory');
  if (nameInput) nameInput.value = skillName;
  if (catSelect) catSelect.value = cat;

  const existingIndex = mySkillsList.findIndex(
    s => s.name.trim().toLowerCase() === skillName.trim().toLowerCase()
  );

  // If already in portfolio -> tap toggles it off (removes it)
  if (existingIndex > -1) {
    const existing = mySkillsList[existingIndex];
    mySkillsList.splice(existingIndex, 1);
    localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));
    renderSkillsTable();
    updateCatalogueChipStates();
    App.showToast(`Removed "${skillName}" from your skills inventory.`, 'info', 2000);

    try {
      await apiFetch(`/skills/${existing.id}`, { method: 'DELETE' });
    } catch (e) {
      console.log('Offline mock storage sync for delete');
    }
    return;
  }

  // If NOT in portfolio -> directly add it to My Skills
  const profSelect = document.getElementById('skillProficiency');
  const expSelect = document.getElementById('skillExperience');
  const proficiency = (profSelect && profSelect.value) ? profSelect.value : 'Intermediate';
  const experience = (expSelect && expSelect.value) ? expSelect.value : '6 - 12 Months';

  const payload = {
    name: skillName,
    category: cat || 'Technical',
    proficiency: proficiency,
    experience: experience
  };

  // Immediate optimistic update so student sees it added in real time
  const tempId = Date.now();
  const newSkill = { id: tempId, ...payload };
  mySkillsList.push(newSkill);
  localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));

  renderSkillsTable();
  updateCatalogueChipStates();
  App.showToast(`✓ "${skillName}" directly added to your skills!`, 'success', 2500);

  // Sync with API in background
  try {
    const res = await apiFetch('/skills', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    if (res && res.skill && res.skill.id) {
      newSkill.id = res.skill.id;
      localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));
    }
  } catch (err) {
    console.log('Saved to local storage fallback');
  }
}

/**
 * Change chip appearance if skill is already added (Emerald pill with checkmark)
 */
function updateCatalogueChipStates() {
  const existingNames = mySkillsList.map(s => s.name.trim().toLowerCase());
  document.querySelectorAll('.catalogue-chip').forEach(btn => {
    const rawName = btn.getAttribute('data-skill');
    const name = (rawName || '').trim().toLowerCase();
    const statusIcon = btn.querySelector('.chip-status-icon');

    if (existingNames.includes(name)) {
      btn.classList.add('chip-added');
      if (statusIcon) {
        statusIcon.innerHTML = `<span class="chip-check-icon">✓</span>`;
      }
      btn.style.opacity = '1';
      btn.style.textDecoration = 'none';
      btn.title = `"${rawName}" added! Touch again to remove.`;
    } else {
      btn.classList.remove('chip-added');
      if (statusIcon) {
        statusIcon.innerHTML = '';
      }
      btn.style.opacity = '1';
      btn.style.textDecoration = 'none';
      btn.title = `Touch to directly add "${rawName}" to My Skills`;
    }
  });
}

/**
 * Filter tabs & Search event listeners
 */
function setupFilters() {
  // Category tabs
  document.querySelectorAll('.js-skill-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.js-skill-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      currentCategoryFilter = tab.getAttribute('data-filter');
      renderSkillsTable();
    });
  });

  // Search input
  const searchInput = document.getElementById('search-skills-input');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      currentSearchQuery = e.target.value.trim().toLowerCase();
      renderSkillsTable();
    });
  }
}

/**
 * Render table / list of added skills
 */
function renderSkillsTable() {
  const tbody = document.getElementById('skills-table-body');
  const emptyState = document.getElementById('skills-empty-state');
  const countBadge = document.getElementById('total-skills-badge');

  if (!tbody) return;

  // Filter skills
  let filtered = mySkillsList.filter(s => {
    const matchesCat = currentCategoryFilter === 'ALL' || s.category === currentCategoryFilter;
    const matchesSearch = !currentSearchQuery || s.name.toLowerCase().includes(currentSearchQuery);
    return matchesCat && matchesSearch;
  });

  if (countBadge) {
    countBadge.textContent = `${mySkillsList.length} Total`;
  }

  if (filtered.length === 0) {
    tbody.innerHTML = '';
    if (emptyState) emptyState.style.display = 'block';
    return;
  }

  if (emptyState) emptyState.style.display = 'none';

  let rowsHtml = '';
  filtered.forEach(skill => {
    let profBadgeClass = 'badge-primary';
    let profWidth = '33%';
    if (skill.proficiency === 'Intermediate') {
      profBadgeClass = 'badge-blue';
      profWidth = '66%';
    } else if (skill.proficiency === 'Advanced') {
      profBadgeClass = 'badge-success';
      profWidth = '100%';
    }

    const catBadgeClass = skill.category === 'Technical' ? 'badge-purple' : 'badge-neutral';

    rowsHtml += `
      <tr>
        <td style="padding:1rem 1.25rem;">
          <strong style="color:var(--text-primary); font-size:0.95rem;">${escapeHtml(skill.name)}</strong>
        </td>
        <td style="padding:1rem 1.25rem;">
          <span class="badge ${catBadgeClass}">${escapeHtml(skill.category)}</span>
        </td>
        <td style="padding:1rem 1.25rem;">
          <span class="badge ${profBadgeClass}">${escapeHtml(skill.proficiency)}</span>
          <div class="match-bar-track" style="height:4px; max-width:90px; margin-top:0.35rem;">
            <div class="match-bar-fill" style="width:${profWidth};"></div>
          </div>
        </td>
        <td style="padding:1rem 1.25rem; font-size:0.875rem; color:var(--text-secondary);">
          ${escapeHtml(skill.experience || 'Not specified')}
        </td>
        <td style="padding:1rem 1.25rem; text-align:right;">
          <button type="button" class="btn btn-secondary btn-sm js-delete-skill" data-id="${skill.id}" data-name="${escapeHtml(skill.name)}" style="color:#ef4444; border-color:#fee2e2;">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="3 6 5 6 21 6"></polyline>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            </svg>
            Remove
          </button>
        </td>
      </tr>
    `;
  });

  tbody.innerHTML = rowsHtml;

  // Setup delete handlers
  tbody.querySelectorAll('.js-delete-skill').forEach(btn => {
    btn.addEventListener('click', async () => {
      const id = btn.getAttribute('data-id');
      const name = btn.getAttribute('data-name');
      await deleteSkill(id, name);
    });
  });
}

/**
 * Handle Add Skill Form submission
 */
function setupAddSkillForm() {
  const form = document.getElementById('add-skill-form');
  const addBtn = document.getElementById('btn-add-skill');

  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('skillName').value.trim();
    const category = document.getElementById('skillCategory').value;
    const proficiency = document.getElementById('skillProficiency').value;
    const experience = document.getElementById('skillExperience').value;

    if (!name) {
      App.showToast('Please enter or select a skill name.', 'error');
      return;
    }

    // Check duplicate
    const exists = mySkillsList.some(s => s.name.toLowerCase() === name.toLowerCase());
    if (exists) {
      App.showToast(`"${name}" is already in your skills inventory.`, 'warning');
      return;
    }

    const payload = {
      name,
      category,
      proficiency,
      experience
    };

    addBtn.disabled = true;

    try {
      const res = await apiFetch('/skills', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      const newSkill = res.skill || { id: Date.now(), ...payload };
      mySkillsList.push(newSkill);
      localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));

      // Reset form
      document.getElementById('skillName').value = '';
      
      renderSkillsTable();
      updateCatalogueChipStates();

      // Trigger bounce pop animation on the newly added chip
      const matchingChip = Array.from(document.querySelectorAll('.catalogue-chip')).find(
        btn => (btn.getAttribute('data-skill') || '').trim().toLowerCase() === name.trim().toLowerCase()
      );
      if (matchingChip) {
        matchingChip.classList.remove('chip-anim-pop');
        void matchingChip.offsetWidth;
        matchingChip.classList.add('chip-anim-pop');
      }

      App.showToast(`"${name}" successfully added to your portfolio!`, 'success');
    } catch (err) {
      App.showToast(err.message || 'Failed to add skill.', 'error');
    } finally {
      addBtn.disabled = false;
    }
  });
}

/**
 * Delete a skill from student's portfolio
 */
async function deleteSkill(id, name) {
  if (!confirm(`Are you sure you want to remove "${name}" from your skills list?`)) return;

  try {
    await apiFetch(`/skills/${id}`, {
      method: 'DELETE'
    });

    mySkillsList = mySkillsList.filter(s => String(s.id) !== String(id));
    localStorage.setItem(CONFIG.STORAGE_KEYS.MY_SKILLS, JSON.stringify(mySkillsList));

    renderSkillsTable();
    updateCatalogueChipStates();

    App.showToast(`Removed "${name}" from your portfolio.`, 'info');
  } catch (err) {
    App.showToast(err.message || 'Failed to delete skill.', 'error');
  }
}
