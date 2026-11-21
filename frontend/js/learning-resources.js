/**
 * Skill2Job - Learning Resources Controller
 * Manages verified educational pathways, documentation links, and skill-specific roadmaps.
 */

let activeSkillFilter = 'ALL';
let activeLevelFilter = 'ALL';
let activeSearchQuery = '';

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.requireAuth()) return;

  setupResourceFilters();

  // Check URL param ?skill=...
  const urlParams = new URLSearchParams(window.location.search);
  const skillParam = urlParams.get('skill');
  if (skillParam) {
    const skillSelect = document.getElementById('filter-resource-skill');
    if (skillSelect) {
      skillSelect.value = skillParam;
      activeSkillFilter = skillParam;
    }
  }

  populateSkillFilterDropdown();
  renderResources();
});

/**
 * Populate skill dropdown dynamically from verified resources list
 */
function populateSkillFilterDropdown() {
  const select = document.getElementById('filter-resource-skill');
  if (!select) return;

  // Extract unique skills
  const skillsSet = new Set(VERIFIED_LEARNING_RESOURCES.map(r => r.skill));
  const uniqueSkills = Array.from(skillsSet).sort();

  let optionsHtml = '<option value="ALL">All Skills</option>';
  uniqueSkills.forEach(s => {
    const isSelected = s.toLowerCase() === activeSkillFilter.toLowerCase();
    optionsHtml += `<option value="${escapeHtml(s)}" ${isSelected ? 'selected' : ''}>${escapeHtml(s)}</option>`;
  });

  select.innerHTML = optionsHtml;
}

/**
 * Setup event listeners for filtering
 */
function setupResourceFilters() {
  const searchInput = document.getElementById('search-resources-input');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      activeSearchQuery = e.target.value.trim().toLowerCase();
      renderResources();
    });
  }

  const skillSelect = document.getElementById('filter-resource-skill');
  if (skillSelect) {
    skillSelect.addEventListener('change', (e) => {
      activeSkillFilter = e.target.value;
      renderResources();
    });
  }

  const levelSelect = document.getElementById('filter-resource-level');
  if (levelSelect) {
    levelSelect.addEventListener('change', (e) => {
      activeLevelFilter = e.target.value;
      renderResources();
    });
  }
}

/**
 * Filter and render resource cards
 */
function renderResources() {
  const grid = document.getElementById('resources-grid-container');
  const emptyState = document.getElementById('resources-empty-state');
  const countBadge = document.getElementById('resources-count-badge');

  if (!grid) return;

  let filtered = VERIFIED_LEARNING_RESOURCES.filter(res => {
    const matchesSkill = activeSkillFilter === 'ALL' || res.skill.toLowerCase() === activeSkillFilter.toLowerCase();
    const matchesLevel = activeLevelFilter === 'ALL' || res.level.toLowerCase().includes(activeLevelFilter.toLowerCase());
    const matchesSearch = !activeSearchQuery || 
      res.title.toLowerCase().includes(activeSearchQuery) ||
      res.provider.toLowerCase().includes(activeSearchQuery) ||
      res.skill.toLowerCase().includes(activeSearchQuery) ||
      res.description.toLowerCase().includes(activeSearchQuery);

    return matchesSkill && matchesLevel && matchesSearch;
  });

  if (countBadge) {
    countBadge.textContent = `${filtered.length} Resources`;
  }

  if (filtered.length === 0) {
    grid.innerHTML = '';
    if (emptyState) emptyState.style.display = 'block';
    return;
  }

  if (emptyState) emptyState.style.display = 'none';

  let html = '';
  filtered.forEach(res => {
    const statusBadgeClass = res.status.toLowerCase() === 'free' ? 'badge-success' : 'badge-neutral';

    html += `
      <div class="resource-card">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem;">
          <span class="resource-provider">${escapeHtml(res.provider)}</span>
          <span class="badge ${statusBadgeClass}">${escapeHtml(res.status)}</span>
        </div>

        <h3 class="resource-title">${escapeHtml(res.title)}</h3>
        <p class="resource-desc">${escapeHtml(res.description)}</p>

        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:auto; padding-top:1rem; border-top:1px solid var(--border-color);">
          <div style="display:flex; gap:0.4rem; flex-wrap:wrap;">
            <span class="badge badge-purple">${escapeHtml(res.skill)}</span>
            <span class="badge badge-neutral">${escapeHtml(res.level)}</span>
          </div>

          <a href="${escapeHtml(res.url)}" target="_blank" rel="noopener noreferrer" class="btn btn-primary btn-sm" title="Open verified official guide">
            <span>Open Link</span>
            <span style="font-size:0.75rem;">↗</span>
          </a>
        </div>
      </div>
    `;
  });

  grid.innerHTML = html;
}
