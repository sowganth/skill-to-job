/**
 * Skill2Job - Student Profile Controller
 * Handles viewing and editing academic details and career interests.
 */

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.requireAuth()) return;

  await loadProfile();
  setupProfileForm();
});

/**
 * Load student profile from backend API or local session
 */
async function loadProfile() {
  const loadingIndicator = document.getElementById('profile-loading');
  const formContainer = document.getElementById('profile-form-container');

  try {
    if (loadingIndicator) loadingIndicator.style.display = 'flex';
    if (formContainer) formContainer.style.display = 'none';

    let student = null;
    try {
      const res = await apiFetch('/students/profile');
      student = res.student || Auth.getUser();
    } catch (e) {
      student = Auth.getUser();
    }

    if (student) {
      // Populate form fields
      document.getElementById('name').value = student.name || '';
      document.getElementById('studentId').value = student.studentId || '';
      document.getElementById('email').value = student.email || '';
      document.getElementById('college').value = student.college || '';
      document.getElementById('department').value = student.department || 'Computer Science & Engineering';
      document.getElementById('year').value = student.year || '3rd Year';
      document.getElementById('careerInterests').value = student.careerInterests || '';
      document.getElementById('bio').value = student.bio || '';

      // Update avatar and labels
      const initials = (student.name || 'S').split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
      const avatarEl = document.getElementById('profile-big-avatar');
      if (avatarEl) avatarEl.textContent = initials;
      
      const nameHeading = document.getElementById('profile-heading-name');
      if (nameHeading) nameHeading.textContent = student.name || 'Student';

      const emailHeading = document.getElementById('profile-heading-email');
      if (emailHeading) emailHeading.textContent = student.email || '';
    }

  } catch (err) {
    console.error('Error loading profile:', err);
    App.showToast('Failed to load profile details.', 'error');
  } finally {
    if (loadingIndicator) loadingIndicator.style.display = 'none';
    if (formContainer) formContainer.style.display = 'block';
  }
}

/**
 * Setup profile submit handler
 */
function setupProfileForm() {
  const form = document.getElementById('profile-form');
  const saveBtn = document.getElementById('save-profile-btn');
  const btnText = document.getElementById('save-btn-text');
  const btnSpinner = document.getElementById('save-btn-spinner');

  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('name').value.trim();
    const studentId = document.getElementById('studentId').value.trim();
    const email = document.getElementById('email').value.trim();
    const college = document.getElementById('college').value.trim();
    const department = document.getElementById('department').value;
    const year = document.getElementById('year').value;
    const careerInterests = document.getElementById('careerInterests').value.trim();
    const bio = document.getElementById('bio').value.trim();

    if (!name || !studentId || !email || !college) {
      App.showToast('Please complete all mandatory academic fields.', 'error');
      return;
    }

    const payload = {
      name,
      studentId,
      email,
      college,
      department,
      year,
      careerInterests,
      bio
    };

    saveBtn.disabled = true;
    btnText.textContent = 'Saving Changes...';
    btnSpinner.style.display = 'inline-block';

    try {
      const res = await apiFetch('/students/profile', {
        method: 'PUT',
        body: JSON.stringify(payload)
      });

      // Update local storage session
      Auth.updateUser(payload);
      Auth.renderHeaderUser();

      // Update header headings
      const nameHeading = document.getElementById('profile-heading-name');
      if (nameHeading) nameHeading.textContent = name;

      App.showToast(res.message || 'Profile saved successfully!', 'success');
    } catch (err) {
      App.showToast(err.message || 'Failed to update profile.', 'error');
    } finally {
      saveBtn.disabled = false;
      btnText.textContent = 'Save Profile Changes';
      btnSpinner.style.display = 'none';
    }
  });
}
