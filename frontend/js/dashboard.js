/**
 * Skill2Job - Student Dashboard Controller
 * Calculates real-time statistics, job match rankings, and skill recommendations.
 */

document.addEventListener('DOMContentLoaded', async () => {
  // Ensure route is authenticated
  if (!Auth.requireAuth()) return;

  const user = Auth.getUser();
  if (user) {
    document.getElementById('dash-welcome-name').textContent = user.name || 'Student';
  }

  await loadDashboardData();
});

/**
 * Fetch student profile, skills, and jobs to populate metric cards and recommendation widgets
 */
async function loadDashboardData() {
  const loadingIndicator = document.getElementById('dash-loading');
  const dashContent = document.getElementById('dash-main-grid');

  try {
    if (loadingIndicator) loadingIndicator.style.display = 'flex';
    if (dashContent) dashContent.style.display = 'none';

    // 1. Fetch current student skills
    let mySkills = [];
    try {
      const skillsRes = await apiFetch('/skills');
      mySkills = (skillsRes && skillsRes.skills && skillsRes.skills.length > 0) ? skillsRes.skills : App.getStudentSkills();
    } catch (e) {
      mySkills = App.getStudentSkills();
    }
    if (!mySkills || mySkills.length === 0) {
      mySkills = App.getStudentSkills();
    }

    // 2. Fetch available jobs
    let allJobs = [];
    try {
      const jobsRes = await apiFetch('/jobs');
      allJobs = jobsRes.jobs || [];
    } catch (e) {
      allJobs = MOCK_JOBS_DATABASE;
    }

    // 3. Calculate Matches for all jobs
    const evaluatedJobs = allJobs.map(job => {
      const match = App.calculateSkillMatch(job.requiredSkills, mySkills);
      return {
        ...job,
        matchPercentage: match.matchPercentage,
        matchedSkills: match.matchedSkills,
        missingSkills: match.missingSkills
      };
    });

    // Sort descending by match percentage
    evaluatedJobs.sort((a, b) => b.matchPercentage - a.matchPercentage);

    // 4. Update Metric Counters
    renderMetrics(mySkills, evaluatedJobs);

    // 5. Render Top Recommended Jobs (Top 3)
    renderRecommendedJobs(evaluatedJobs.slice(0, 3));

    // 6. Render Recent Skills list
    renderRecentSkills(mySkills);

    // 7. Calculate and render recommended skills to learn
    renderSkillRecommendations(evaluatedJobs, mySkills);

  } catch (err) {
    console.error('Failed to load dashboard data:', err);
    App.showToast('Failed to load some dashboard components.', 'error');
  } finally {
    if (loadingIndicator) loadingIndicator.style.display = 'none';
    if (dashContent) dashContent.style.display = 'block';
  }
}

/**
 * Render metric summary cards
 */
function renderMetrics(mySkills, evaluatedJobs) {
  // Total Skills Count
  const skillsCountEl = document.getElementById('metric-total-skills');
  if (skillsCountEl) skillsCountEl.textContent = mySkills.length;

  // Matching Jobs Count (jobs with >= 60% match)
  const matchingJobs = evaluatedJobs.filter(j => j.matchPercentage >= 60);
  const matchingJobsEl = document.getElementById('metric-matching-jobs');
  if (matchingJobsEl) matchingJobsEl.textContent = matchingJobs.length;

  // Top Job Match
  const topJob = evaluatedJobs[0];
  const topMatchEl = document.getElementById('metric-top-match');
  const topRoleEl = document.getElementById('metric-top-role');
  if (topJob) {
    if (topMatchEl) topMatchEl.textContent = `${topJob.matchPercentage}%`;
    if (topRoleEl) topRoleEl.textContent = topJob.title;
  } else {
    if (topMatchEl) topMatchEl.textContent = '0%';
    if (topRoleEl) topRoleEl.textContent = 'No jobs listed';
  }

  // Profile Completion Score Calculation
  const user = Auth.getUser() || {};
  let score = 30; // base registered account
  if (user.studentId) score += 10;
  if (user.department) score += 10;
  if (user.year) score += 10;
  if (user.college) score += 10;
  if (user.bio) score += 10;
  if (mySkills.length >= 3) score += 10;
  if (mySkills.length >= 6) score += 10;
  score = Math.min(score, 100);

  const profileScoreEl = document.getElementById('metric-profile-score');
  const profileBarEl = document.getElementById('metric-profile-bar');
  if (profileScoreEl) profileScoreEl.textContent = `${score}%`;
  if (profileBarEl) profileBarEl.style.width = `${score}%`;
}

/**
 * Render top matching jobs list
 */
function renderRecommendedJobs(jobs) {
  const container = document.getElementById('recommended-jobs-list');
  if (!container) return;

  if (jobs.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">💼</div>
        <h3>No Job Matches Yet</h3>
        <p>Add technical and soft skills to see high-probability job recommendations.</p>
        <a href="skills.html" class="btn btn-primary btn-sm">Add Skills Now</a>
      </div>
    `;
    return;
  }

  let html = '';
  jobs.forEach(job => {
    let matchClass = 'match-high';
    if (job.matchPercentage < 60) matchClass = 'match-low';
    else if (job.matchPercentage < 80) matchClass = 'match-mid';

    const skillChips = job.requiredSkills.map(sk => {
      const isMatched = job.matchedSkills.includes(sk);
      return `<span class="skill-tag ${isMatched ? 'skill-tag-matched' : 'skill-tag-missing'}">
        ${isMatched ? '✓' : '✗'} ${escapeHtml(sk)}
      </span>`;
    }).join('');

    html += `
      <div class="job-card">
        <div class="job-card-header">
          <div>
            <h3 class="job-card-title">${escapeHtml(job.title)}</h3>
            <div class="job-card-company">${escapeHtml(job.company)} &bull; ${escapeHtml(job.location)}</div>
          </div>
          <span class="match-badge ${matchClass}">${job.matchPercentage}% Match</span>
        </div>

        <div class="job-meta-row">
          <span>📂 ${escapeHtml(job.category)}</span>
          <span>💼 ${escapeHtml(job.type)}</span>
        </div>

        <div class="job-skills-section">
          <div class="job-skills-label">Skill Alignment (${job.matchedSkills.length}/${job.requiredSkills.length} matched)</div>
          <div class="skill-tags-list">
            ${skillChips}
          </div>
        </div>

        <div class="job-card-actions">
          <a href="job-details.html?id=${job.id}" class="btn btn-secondary btn-sm" style="flex:1;">
            View Details
          </a>
          <a href="skill-gap.html?jobId=${job.id}" class="btn btn-primary btn-sm" style="flex:1;">
            Analyze Gap
          </a>
        </div>
      </div>
    `;
  });

  container.innerHTML = html;
}

/**
 * Render recent skills list
 */
function renderRecentSkills(skills) {
  const container = document.getElementById('recent-skills-list');
  if (!container) return;

  if (skills.length === 0) {
    container.innerHTML = `<p style="font-size:0.875rem; color:var(--text-muted);">No skills added yet.</p>`;
    return;
  }

  // Show last 6 skills
  const recent = [...skills].reverse().slice(0, 6);
  const chips = recent.map(s => `
    <span class="skill-tag">
      <strong>${escapeHtml(s.name)}</strong>
      <span class="skill-tag-proficiency">${escapeHtml(s.proficiency)}</span>
    </span>
  `).join('');

  container.innerHTML = `<div class="skill-tags-list">${chips}</div>`;
}

/**
 * Identify top missing skills across the highest matching jobs
 */
function renderSkillRecommendations(evaluatedJobs, mySkills) {
  const container = document.getElementById('recommended-skills-list');
  if (!container) return;

  const missingMap = {};
  evaluatedJobs.slice(0, 4).forEach(job => {
    job.missingSkills.forEach(skill => {
      missingMap[skill] = (missingMap[skill] || 0) + 1;
    });
  });

  const sortedMissing = Object.keys(missingMap).sort((a, b) => missingMap[b] - missingMap[a]);

  if (sortedMissing.length === 0) {
    container.innerHTML = `
      <div style="font-size:0.875rem; color:#15803d; background:#dcfce7; padding:0.75rem 1rem; border-radius:var(--radius-md);">
        🎉 Outstanding! You have matched all required skills for your top candidate jobs.
      </div>
    `;
    return;
  }

  const chips = sortedMissing.slice(0, 4).map(skillName => `
    <div style="display:flex; justify-content:space-between; align-items:center; padding:0.65rem 0.85rem; background:var(--bg-subtle); border:1px solid var(--border-color); border-radius:var(--radius-md); margin-bottom:0.5rem;">
      <div>
        <strong style="font-size:0.875rem; color:var(--text-primary);">${escapeHtml(skillName)}</strong>
        <span style="display:block; font-size:0.75rem; color:var(--text-muted);">Required in ${missingMap[skillName]} target roles</span>
      </div>
      <a href="learning-resources.html?skill=${encodeURIComponent(skillName)}" class="btn btn-secondary btn-sm" style="font-size:0.75rem; padding:0.3rem 0.6rem;">
        Learn →
      </a>
    </div>
  `).join('');

  container.innerHTML = chips;
}
