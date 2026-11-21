/**
 * Skill2Job - Skill Gap Analysis Controller
 * Provides comparative analysis between student competency and job requirements with prioritized bridging roadmaps.
 */

let jobsDatabase = [];
let studentSkills = [];
let currentSelectedJobId = 1;

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.requireAuth()) return;

  const urlParams = new URLSearchParams(window.location.search);
  const paramJobId = urlParams.get('jobId');
  if (paramJobId) {
    currentSelectedJobId = parseInt(paramJobId, 10);
  }

  await loadGapData();
  setupJobSelector();
});

/**
 * Fetch skills and jobs, calculate gap metrics
 */
async function loadGapData() {
  const loading = document.getElementById('gap-loading');
  const content = document.getElementById('gap-main-content');

  try {
    if (loading) loading.style.display = 'flex';
    if (content) content.style.display = 'none';

    // 1. Fetch student skills
    try {
      const skillsRes = await apiFetch('/skills');
      studentSkills = (skillsRes && skillsRes.skills && skillsRes.skills.length > 0) ? skillsRes.skills : App.getStudentSkills();
    } catch (e) {
      studentSkills = App.getStudentSkills();
    }
    if (!studentSkills || studentSkills.length === 0) {
      studentSkills = App.getStudentSkills();
    }

    // 2. Fetch jobs
    try {
      const jobsRes = await apiFetch('/jobs');
      jobsDatabase = jobsRes.jobs || [];
    } catch (e) {
      jobsDatabase = MOCK_JOBS_DATABASE;
    }

    // Populate job selector dropdown
    populateJobDropdown();

    // Render analysis for currently selected job
    renderJobGapAnalysis(currentSelectedJobId);

  } catch (err) {
    console.error('Error loading gap data:', err);
    App.showToast('Failed to calculate skill gap analytics.', 'error');
  } finally {
    if (loading) loading.style.display = 'none';
    if (content) content.style.display = 'block';
  }
}

/**
 * Populate job selector dropdown
 */
function populateJobDropdown() {
  const select = document.getElementById('select-target-job');
  if (!select) return;

  select.innerHTML = jobsDatabase.map(job => `
    <option value="${job.id}" ${job.id === currentSelectedJobId ? 'selected' : ''}>
      ${escapeHtml(job.title)} (${escapeHtml(job.company)})
    </option>
  `).join('');
}

/**
 * Setup change listener on job selector
 */
function setupJobSelector() {
  const select = document.getElementById('select-target-job');
  if (!select) return;

  select.addEventListener('change', (e) => {
    currentSelectedJobId = parseInt(e.target.value, 10);
    renderJobGapAnalysis(currentSelectedJobId);
  });
}

/**
 * Render complete gap analysis layout for a job
 */
function renderJobGapAnalysis(jobId) {
  const job = jobsDatabase.find(j => j.id === jobId) || jobsDatabase[0];
  if (!job) return;

  const match = App.calculateSkillMatch(job.requiredSkills, studentSkills);

  // Update Overview Header
  document.getElementById('gap-job-title').textContent = job.title;
  document.getElementById('gap-job-meta').textContent = `${job.company} • ${job.category} • ${job.location}`;
  
  // Match Score Badge & Progress Bar
  const matchBadge = document.getElementById('gap-match-badge');
  matchBadge.textContent = `${match.matchPercentage}% Alignment`;
  let matchClass = 'match-high';
  if (match.matchPercentage < 50) matchClass = 'match-low';
  else if (match.matchPercentage < 80) matchClass = 'match-mid';
  matchBadge.className = `match-badge ${matchClass}`;

  document.getElementById('gap-progress-text').textContent = `${match.matchPercentage}%`;
  document.getElementById('gap-progress-bar').style.width = `${match.matchPercentage}%`;

  // Alignment qualitative summary
  const summaryEl = document.getElementById('gap-readiness-summary');
  if (match.matchPercentage >= 80) {
    summaryEl.innerHTML = `<span style="color:#15803d; font-weight:700;">High Readiness:</span> Your profile strongly matches this position. Minor gaps can be addressed in a few study sessions.`;
  } else if (match.matchPercentage >= 50) {
    summaryEl.innerHTML = `<span style="color:#b45309; font-weight:700;">Moderate Alignment:</span> You meet fundamental prerequisites but need to master critical framework/tooling requirements before applying.`;
  } else {
    summaryEl.innerHTML = `<span style="color:#64748b; font-weight:700;">Foundational Phase:</span> This role requires additional specialized technical depth. Follow the roadmap below to progress.`;
  }

  // Column 1: Skills You Possess
  const possessedContainer = document.getElementById('possessed-skills-list');
  document.getElementById('possessed-count').textContent = match.matchedSkills.length;
  
  if (match.matchedSkills.length === 0) {
    possessedContainer.innerHTML = `
      <p style="font-size:0.875rem; color:var(--text-muted); padding:1rem 0;">
        You have not yet added any of the skills required for this role.
      </p>
    `;
  } else {
    possessedContainer.innerHTML = match.matchedSkills.map(skillName => {
      // Find student's proficiency
      const found = studentSkills.find(s => s.name.toLowerCase() === skillName.toLowerCase());
      const proficiency = found ? found.proficiency : 'Intermediate';
      const experience = found ? found.experience : 'Logged';

      return `
        <div class="gap-item-card">
          <div>
            <strong style="color:var(--text-primary); font-size:0.95rem;">✓ ${escapeHtml(skillName)}</strong>
            <div style="font-size:0.75rem; color:var(--text-muted); margin-top:0.2rem;">
              Experience: ${escapeHtml(experience)}
            </div>
          </div>
          <div>
            <span class="badge badge-success">${escapeHtml(proficiency)}</span>
          </div>
        </div>
      `;
    }).join('');
  }

  // Column 2: Missing Skills & Suggested Priorities
  const missingContainer = document.getElementById('missing-skills-list');
  document.getElementById('missing-count').textContent = match.missingSkills.length;

  if (match.missingSkills.length === 0) {
    missingContainer.innerHTML = `
      <div style="background:#dcfce7; border:1px solid #bbf7d0; border-radius:var(--radius-md); padding:1.25rem; color:#15803d; font-size:0.875rem;">
        🎉 <strong>Zero Skill Gaps!</strong> You possess all prerequisite skills mandated by this role.
      </div>
    `;
  } else {
    missingContainer.innerHTML = match.missingSkills.map((skillName, index) => {
      // First 2 missing skills have High priority, remainder Medium
      const isHighPriority = index < 2;
      const priorityLabel = isHighPriority ? 'High Priority' : 'Medium Priority';
      const priorityClass = isHighPriority ? 'priority-badge-high' : 'priority-badge-med';

      return `
        <div class="gap-item-card">
          <div style="flex:1;">
            <strong style="color:#b91c1c; font-size:0.95rem;">⚠️ ${escapeHtml(skillName)}</strong>
            <div style="font-size:0.75rem; color:var(--text-muted); margin-top:0.2rem;">
              Required core prerequisite
            </div>
          </div>
          <div style="display:flex; align-items:center; gap:0.5rem;">
            <span class="${priorityClass}">${priorityLabel}</span>
            <a href="learning-resources.html?skill=${encodeURIComponent(skillName)}" class="btn btn-secondary btn-sm" style="font-size:0.75rem; padding:0.25rem 0.6rem;">
              Study →
            </a>
          </div>
        </div>
      `;
    }).join('');
  }

  // Bottom Section: Immediate Curated Learning Resources for Missing Skills
  renderGapResources(match.missingSkills);
}

/**
 * Render curated learning resources cards specifically for the missing skills
 */
function renderGapResources(missingSkills) {
  const container = document.getElementById('gap-resources-container');
  if (!container) return;

  if (missingSkills.length === 0) {
    container.innerHTML = `
      <p style="font-size:0.875rem; color:var(--text-muted);">No learning resources required for this position!</p>
    `;
    return;
  }

  // Filter verified resources matching missing skills
  const relevantResources = VERIFIED_LEARNING_RESOURCES.filter(res => 
    missingSkills.some(m => m.toLowerCase() === res.skill.toLowerCase())
  );

  if (relevantResources.length === 0) {
    container.innerHTML = `
      <p style="font-size:0.875rem; color:var(--text-muted);">
        General documentation available on <a href="learning-resources.html">Learning Resources page</a>.
      </p>
    `;
    return;
  }

  container.innerHTML = relevantResources.map(res => `
    <div class="resource-card">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.4rem;">
        <span class="resource-provider">${escapeHtml(res.provider)}</span>
        <span class="badge badge-success">${escapeHtml(res.status)}</span>
      </div>
      <h4 class="resource-title">${escapeHtml(res.title)}</h4>
      <p class="resource-desc">${escapeHtml(res.description)}</p>
      <div style="display:flex; justify-content:space-between; align-items:center; margin-top:auto; pt-2;">
        <span class="badge badge-neutral">${escapeHtml(res.skill)} &bull; ${escapeHtml(res.level)}</span>
        <a href="${escapeHtml(res.url)}" target="_blank" rel="noopener noreferrer" class="btn btn-primary btn-sm" style="font-size:0.75rem;">
          Open Guide ↗
        </a>
      </div>
    </div>
  `).join('');
}
