import { useState } from 'react';
import { useToast } from '../../core/components/Toast';
import { changeCurrentPassword } from '../../core/services/api';
import {
  ALLERGEN_OPTIONS,
  DEFAULT_WEB_SETTINGS,
  THEME_OPTIONS,
  applyWebSettings,
  getWebSettings,
  resetWebSettings,
  saveWebSettings,
} from '../../core/services/settings';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';
const PREFILL_STORAGE_KEY = 'instock_prefill_ingredients';

function SettingsPage() {
  const addToast = useToast();
  const [settings, setSettings] = useState(() => getWebSettings());
  const [user] = useState(() => JSON.parse(localStorage.getItem('user') || '{}'));
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [savingPassword, setSavingPassword] = useState(false);

  const updateSetting = (key, value) => {
    setSettings((prev) => ({ ...prev, [key]: value }));
  };

  const toggleAllergen = (key) => {
    setSettings((prev) => {
      const current = Array.isArray(prev.allergens) ? prev.allergens : [];
      const next = current.includes(key)
        ? current.filter((a) => a !== key)
        : [...current, key];
      return { ...prev, allergens: next };
    });
  };

  const saveSettings = () => {
    const saved = saveWebSettings(settings);
    applyWebSettings(saved);
    setSettings(saved);
    addToast('Settings saved.', 'success');
  };

  const restoreDefaults = () => {
    const defaults = resetWebSettings();
    applyWebSettings(defaults);
    setSettings(defaults);
    addToast('Settings reset to defaults.', 'success');
  };

  const clearLocalWorkspace = () => {
    localStorage.removeItem(OVERVIEW_STATS_KEY);
    localStorage.removeItem(PREFILL_STORAGE_KEY);
    addToast('Local dashboard cache cleared.', 'success');
  };

  const updatePasswordField = (key, value) => {
    setPasswordForm((prev) => ({ ...prev, [key]: value }));
  };

  const savePassword = async () => {
    if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
      addToast('Complete all password fields.', 'error');
      return;
    }

    if (passwordForm.newPassword.length < 8) {
      addToast('New password must be at least 8 characters.', 'error');
      return;
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      addToast('New passwords do not match.', 'error');
      return;
    }

    setSavingPassword(true);
    try {
      await changeCurrentPassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      });
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      addToast('Password changed.', 'success');
    } catch (error) {
      addToast(error.response?.data?.message || 'Unable to change password.', 'error');
    } finally {
      setSavingPassword(false);
    }
  };

  const activeTheme = THEME_OPTIONS[settings.theme] || THEME_OPTIONS[DEFAULT_WEB_SETTINGS.theme];

  return (
    <div className="settings-page">
      <header className="dashboard-header">
        <h1>Settings</h1>
        <p>Customize the web app experience on this browser.</p>
      </header>

      <section className="settings-grid">
        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Appearance</h2>
              <p>Choose the dashboard color theme.</p>
            </div>
          </div>

          <label className="settings-field" htmlFor="theme-select">
            <span>Theme</span>
            <select
              id="theme-select"
              className="result-select"
              value={settings.theme}
              onChange={(event) => updateSetting('theme', event.target.value)}
            >
              {Object.entries(THEME_OPTIONS).map(([key, theme]) => (
                <option key={key} value={key}>{theme.label}</option>
              ))}
            </select>
          </label>

          <div className="theme-preview" aria-label={`${activeTheme.label} theme preview`}>
            {Object.entries(activeTheme.colors).slice(0, 4).map(([name, color]) => (
              <span key={name} className="theme-swatch" style={{ background: color }} />
            ))}
          </div>
        </article>

        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Security</h2>
              <p>Change your account password.</p>
            </div>
          </div>

          <div className="settings-form">
            <label className="settings-field" htmlFor="current-password">
              <span>Current Password</span>
              <input
                id="current-password"
                className="ingredients-input"
                type="password"
                value={passwordForm.currentPassword}
                onChange={(event) => updatePasswordField('currentPassword', event.target.value)}
                autoComplete="current-password"
              />
            </label>

            <label className="settings-field" htmlFor="new-password">
              <span>New Password</span>
              <input
                id="new-password"
                className="ingredients-input"
                type="password"
                value={passwordForm.newPassword}
                onChange={(event) => updatePasswordField('newPassword', event.target.value)}
                autoComplete="new-password"
              />
            </label>

            <label className="settings-field" htmlFor="confirm-password">
              <span>Confirm New Password</span>
              <input
                id="confirm-password"
                className="ingredients-input"
                type="password"
                value={passwordForm.confirmPassword}
                onChange={(event) => updatePasswordField('confirmPassword', event.target.value)}
                autoComplete="new-password"
              />
            </label>

            <button type="button" className="action-btn action-btn-primary" onClick={savePassword} disabled={savingPassword}>
              {savingPassword ? 'Changing...' : 'Change Password'}
            </button>
          </div>
        </article>

        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Recipe Finder</h2>
              <p>Set defaults for recipe search.</p>
            </div>
          </div>

          <label className="settings-field" htmlFor="default-limit">
            <span>Default max results</span>
            <select
              id="default-limit"
              className="result-select"
              value={settings.defaultRecipeLimit}
              onChange={(event) => updateSetting('defaultRecipeLimit', Number(event.target.value))}
            >
              <option value={5}>5 recipes</option>
              <option value={10}>10 recipes</option>
              <option value={15}>15 recipes</option>
              <option value={20}>20 recipes</option>
            </select>
          </label>

          <label className="settings-toggle">
            <input
              type="checkbox"
              checked={settings.autocompleteEnabled}
              onChange={(event) => updateSetting('autocompleteEnabled', event.target.checked)}
            />
            <span>
              <strong>Recipe autocomplete</strong>
              <small>Show recipe title suggestions while typing.</small>
            </span>
          </label>
        </article>

        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Dietary Preferences</h2>
              <p>Select allergens to exclude from all recipe searches (AC-4).</p>
            </div>
          </div>

          {settings.allergens?.length > 0 && (
            <p className="settings-allergen-summary">
              Active filters: <strong>{settings.allergens.join(', ')}</strong>
            </p>
          )}

          <div className="settings-allergen-grid">
            {ALLERGEN_OPTIONS.map(({ key, label }) => (
              <label key={key} className="settings-toggle settings-allergen-item">
                <input
                  type="checkbox"
                  checked={Array.isArray(settings.allergens) && settings.allergens.includes(key)}
                  onChange={() => toggleAllergen(key)}
                />
                <span>
                  <strong>{label}</strong>
                </span>
              </label>
            ))}
          </div>
        </article>

        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Account</h2>
              <p>Signed-in user details.</p>
            </div>
          </div>

          <div className="settings-list">
            <div className="setting-row"><span>Name</span><strong>{user.fullName || 'N/A'}</strong></div>
            <div className="setting-row"><span>Email</span><strong>{user.email || 'N/A'}</strong></div>
            <div className="setting-row"><span>Role</span><strong>{user.role || 'USER'}</strong></div>
          </div>
        </article>

        <article className="settings-panel">
          <div className="dash-card-header">
            <div>
              <h2>Local Data</h2>
              <p>Clear browser-only dashboard cache.</p>
            </div>
          </div>

          <button type="button" className="action-btn action-btn-secondary settings-danger" onClick={clearLocalWorkspace}>
            Clear Local Cache
          </button>
        </article>
      </section>

      <div className="settings-actions">
        <button type="button" className="action-btn action-btn-primary" onClick={saveSettings}>
          Save Settings
        </button>
        <button type="button" className="action-btn action-btn-secondary" onClick={restoreDefaults}>
          Reset Defaults
        </button>
      </div>
    </div>
  );
}

export default SettingsPage;
