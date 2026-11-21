/**
 * Skill2Job - Job Recommendations Controller
 * Evaluates student skills against vacancies, provides multi-criteria filtering, and sorting.
 */

let allJobsList = [];
let studentSkillsList = [];

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.requireAuth()) return;

  setupFilterListeners();
  await loadJobs();
});

/**
 * Fetch jobs and student skills, calculate matches, and render cards
 */
async function loadJobs() {
  const loading = document.getElementById('jobs-loading');
  const grid = document.getElementById('jobs-grid-container');
  const banner = document.getElementById('mock-mode-notice-banner');

  try {
    if (loading) loading.style.display = 'flex';
    if (grid) grid.style.display = 'none';

    // 1. Fetch student skills
    try {
      const skillsRes = await apiFetch('/skills');
      studentSkillsList = (skillsRes && skillsRes.skills && skillsRes.skills.length > 0) ? skillsRes.skills : App.getStudentSkills();
    } catch (e) {
      studentSkillsList = App.getStudentSkills();
    }
    if (!studentSkillsList || studentSkillsList.length === 0) {
      studentSkillsList = App.getStudentSkills();
    }

    // 2. Fetch jobs from Spring Boot backend
    let isMock = false;
    try {
      const jobsRes = await apiFetch('/jobs');
      allJobsList = jobsRes.jobs || [];
    } catch (e) {
      allJobsList = MOCK_JOBS_DATABASE;
      isMock = true;
    }

    if (banner) {
      banner.style.display = isMock ? 'flex' : 'none';
    }

    // Check URL parameters for default filter e.g. ?cat=Backend or ?keyword=Java
    const urlParams = new URLSearchParams(window.location.search);
    const catParam = urlParams.get('cat');
    if (catParam) {
      const catSelect = document.getElementById('filter-category');
      if (catSelect) catSelect.value = catParam;
    }
    const keywordParam = urlParams.get('keyword') || urlParams.get('search');
    if (keywordParam) {
      const searchInput = document.getElementById('search-jobs-input');
      if (searchInput) searchInput.value = keywordParam;
    }

    applyFiltersAndRender();

  } catch (err) {
    console.error('Error loading jobs:', err);
    App.showToast('Failed to load job listings.', 'error');
  } finally {
    if (loading) loading.style.display = 'none';
    if (grid) grid.style.display = 'grid';
  }
}

/**
 * Filter and sort listeners
 */
function setupFilterListeners() {
  document.getElementById('search-jobs-input').addEventListener('input', applyFiltersAndRender);
  document.getElementById('filter-category').addEventListener('change', applyFiltersAndRender);
  document.getElementById('filter-match').addEventListener('change', applyFiltersAndRender);
  document.getElementById('sort-jobs').addEventListener('change', applyFiltersAndRender);
}

/**
 * Filter, score, and sort jobs
 */
function applyFiltersAndRender() {
  const searchQuery = document.getElementById('search-jobs-input').value.trim().toLowerCase();
  const selectedCategory = document.getElementById('filter-category').value;
  const selectedMatchFilter = document.getElementById('filter-match').value;
  const sortBy = document.getElementById('sort-jobs').value;

  // 1. Score each job against student's logged skills
  let evaluated = allJobsList.map(job => {
    const match = App.calculateSkillMatch(job.requiredSkills, studentSkillsList);
    return {
      ...job,
      matchPercentage: match.matchPercentage,
      matchedSkills: match.matchedSkills,
      missingSkills: match.missingSkills
    };
  });

  // 2. Filter by search query
  if (searchQuery) {
    evaluated = evaluated.filter(j => 
      j.title.toLowerCase().includes(searchQuery) ||
      j.company.toLowerCase().includes(searchQuery) ||
      j.requiredSkills.some(s => s.toLowerCase().includes(searchQuery))
    );
  }

  // 3. Filter by category
  if (selectedCategory !== 'ALL') {
    const selCat = selectedCategory.toLowerCase();
    evaluated = evaluated.filter(j => {
      const jobCat = (j.category || '').toLowerCase();
      return jobCat === selCat || jobCat.includes(selCat) || selCat.includes(jobCat);
    });
  }

  // 4. Filter by match percentage range
  if (selectedMatchFilter === 'HIGH') {
    evaluated = evaluated.filter(j => j.matchPercentage >= 80);
  } else if (selectedMatchFilter === 'MED') {
    evaluated = evaluated.filter(j => j.matchPercentage >= 50 && j.matchPercentage < 80);
  } else if (selectedMatchFilter === 'LOW') {
    evaluated = evaluated.filter(j => j.matchPercentage < 50);
  }

  // 5. Sort
  if (sortBy === 'MATCH_DESC') {
    evaluated.sort((a, b) => b.matchPercentage - a.matchPercentage);
  } else if (sortBy === 'MATCH_ASC') {
    evaluated.sort((a, b) => a.matchPercentage - b.matchPercentage);
  } else if (sortBy === 'TITLE_ASC') {
    evaluated.sort((a, b) => a.title.localeCompare(b.title));
  } else if (sortBy === 'SKILLS_COUNT') {
    evaluated.sort((a, b) => b.requiredSkills.length - a.requiredSkills.length);
  }

  // 6. Render
  renderJobs(evaluated);
}

/**
 * Render job cards
 */
function renderJobs(jobs) {
  const grid = document.getElementById('jobs-grid-container');
  const emptyState = document.getElementById('jobs-empty-state');
  const countBadge = document.getElementById('jobs-count-badge');

  if (countBadge) countBadge.textContent = `${jobs.length} Available`;

  if (jobs.length === 0) {
    grid.innerHTML = '';
    if (emptyState) emptyState.style.display = 'block';
    return;
  }

  if (emptyState) emptyState.style.display = 'none';

  let html = '';
  jobs.forEach(job => {
    let matchBadgeClass = 'match-high';
    if (job.matchPercentage < 50) matchBadgeClass = 'match-low';
    else if (job.matchPercentage < 80) matchBadgeClass = 'match-mid';

    // Skill pills
    const skillPills = job.requiredSkills.map(sk => {
      const isMatched = job.matchedSkills.includes(sk);
      return `
        <span class="skill-tag ${isMatched ? 'skill-tag-matched' : 'skill-tag-missing'}">
          ${isMatched ? '✓' : '✗'} ${escapeHtml(sk)}
        </span>
      `;
    }).join('');

    html += `
      <div class="job-card">
        <div class="job-card-header">
          <div>
            <h3 class="job-card-title">${escapeHtml(job.title)}</h3>
            <div class="job-card-company">${escapeHtml(job.company)} &bull; ${escapeHtml(job.location)}</div>
          </div>
          <span class="match-badge ${matchBadgeClass}">${job.matchPercentage}% Match</span>
        </div>

        <div class="job-meta-row">
          <span class="badge badge-primary">${escapeHtml(job.category)}</span>
          <span style="font-size:0.75rem; color:var(--text-muted);">${escapeHtml(job.type)}</span>
          <span style="font-size:0.75rem; color:var(--text-muted);">${escapeHtml(job.salaryRange || '')}</span>
        </div>

        <p style="font-size:0.875rem; color:var(--text-secondary); line-height:1.5; margin-bottom:1rem; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">
          ${escapeHtml(job.description)}
        </p>

        <div class="job-skills-section">
          <div class="job-skills-label">
            Skill Alignment: ${job.matchedSkills.length} matched / ${job.missingSkills.length} gap
          </div>
          <div class="skill-tags-list">
            ${skillPills}
          </div>
        </div>

        <div class="job-card-actions">
          <a href="job-details.html?id=${job.id}" class="btn btn-secondary btn-sm" style="flex:1;">
            Job Details
          </a>
          <a href="skill-gap.html?jobId=${job.id}" class="btn btn-primary btn-sm" style="flex:1;">
            View Missing Skills
          </a>
        </div>
      </div>
    `;
  });

  grid.innerHTML = html;
}
