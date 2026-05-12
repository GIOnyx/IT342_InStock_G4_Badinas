import { useEffect, useMemo, useRef, useState } from 'react';
import api, { updateCurrentUser, uploadAvatar } from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

const MAX_FILE_BYTES = 5 * 1024 * 1024; // 5 MB
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

function ProfilePage() {
  const addToast = useToast();
  const fileInputRef = useRef(null);

  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || '{}'));
  const [fullName, setFullName] = useState(() => JSON.parse(localStorage.getItem('user') || '{}')?.fullName || '');
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [avatarPreview, setAvatarPreview] = useState(null); // local blob URL before confirmation

  const initials = useMemo(() => {
    const source = fullName || user.fullName || user.email || 'User';
    return source
      .split(' ')
      .map((item) => item[0])
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }, [fullName, user.email, user.fullName]);

  useEffect(() => {
    const load = async () => {
      try {
        const response = await api.get('/auth/me');
        const currentUser = response.data?.data;
        if (currentUser) {
          setUser(currentUser);
          setFullName(currentUser.fullName || '');
          localStorage.setItem('user', JSON.stringify(currentUser));
        }
      } catch {
        // Keep fallback.
      }
    };

    load();
  }, []);

  // Revoke any blob URL when component unmounts or preview changes
  useEffect(() => {
    return () => {
      if (avatarPreview) URL.revokeObjectURL(avatarPreview);
    };
  }, [avatarPreview]);

  const saveProfile = async () => {
    const trimmedName = fullName.trim();

    if (!trimmedName) {
      addToast('Name cannot be empty.', 'error');
      return;
    }

    setSaving(true);
    try {
      const response = await updateCurrentUser({ fullName: trimmedName });
      const updatedUser = response.data?.data;

      if (updatedUser) {
        setUser(updatedUser);
        setFullName(updatedUser.fullName || '');
        localStorage.setItem('user', JSON.stringify(updatedUser));
        window.dispatchEvent(new CustomEvent('instock-user-change', { detail: updatedUser }));
      }

      addToast('Profile updated.', 'success');
    } catch (error) {
      addToast(error.response?.data?.message || 'Unable to update profile.', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleAvatarClick = () => {
    if (!uploading) fileInputRef.current?.click();
  };

  const handleFileChange = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Reset input so the same file can be re-selected after an error
    event.target.value = '';

    // Client-side validation
    if (!ALLOWED_TYPES.includes(file.type)) {
      addToast('Invalid file type. Please choose a JPEG, PNG, GIF or WebP image.', 'error');
      return;
    }
    if (file.size > MAX_FILE_BYTES) {
      addToast('File is too large. Maximum size is 5 MB.', 'error');
      return;
    }

    // Show local preview immediately
    const localPreview = URL.createObjectURL(file);
    setAvatarPreview(localPreview);

    setUploading(true);
    try {
      const response = await uploadAvatar(file);
      const updatedUser = response.data?.data;

      if (updatedUser) {
        setUser(updatedUser);
        setFullName(updatedUser.fullName || '');
        localStorage.setItem('user', JSON.stringify(updatedUser));
        window.dispatchEvent(new CustomEvent('instock-user-change', { detail: updatedUser }));
        // Revoke preview; the real URL comes from the server
        URL.revokeObjectURL(localPreview);
        setAvatarPreview(null);
      }

      addToast('Profile picture updated.', 'success');
    } catch (error) {
      // Revoke optimistic preview on failure
      URL.revokeObjectURL(localPreview);
      setAvatarPreview(null);
      addToast(error.response?.data?.message || 'Unable to upload avatar.', 'error');
    } finally {
      setUploading(false);
    }
  };

  // Resolve which image URL to show (preference: local preview → server URL → null)
  const displayAvatarUrl = avatarPreview || user.avatarUrl || null;

  return (
    <div className="profile-page">
      <header className="dashboard-header">
        <h1>Profile</h1>
        <p>Manage your account information.</p>
      </header>

      <section className="profile-layout">
        <article className="profile-panel profile-avatar-panel">
          <div className="dash-card-header">
            <div>
              <h2>Profile Picture</h2>
              <p>Click your avatar to upload a new photo.</p>
            </div>
          </div>

          {/* Avatar — clickable upload trigger */}
          <div
            className={`profile-avatar-large profile-avatar-upload${uploading ? ' profile-avatar-uploading' : ''}`}
            onClick={handleAvatarClick}
            role="button"
            tabIndex={0}
            aria-label="Upload profile picture"
            onKeyDown={(e) => e.key === 'Enter' && handleAvatarClick()}
            title="Click to change profile picture"
          >
            {displayAvatarUrl ? (
              <img
                src={displayAvatarUrl}
                alt="Profile avatar"
                className="profile-avatar-img"
              />
            ) : (
              initials
            )}

            {/* Hover overlay */}
            {!uploading && (
              <span className="profile-avatar-overlay" aria-hidden="true">
                📷
              </span>
            )}

            {/* Upload spinner */}
            {uploading && (
              <span className="profile-avatar-spinner" aria-label="Uploading…" />
            )}
          </div>

          <p className="muted-text">
            {uploading ? 'Uploading…' : 'JPEG, PNG, GIF or WebP · Max 5 MB'}
          </p>

          {/* Hidden file input */}
          <input
            ref={fileInputRef}
            id="avatar-file-input"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            style={{ display: 'none' }}
            onChange={handleFileChange}
          />
        </article>

        <article className="profile-panel">
          <div className="dash-card-header">
            <div>
              <h2>Personal Information</h2>
              <p>Update your account details.</p>
            </div>
          </div>

          <div className="profile-form">
            <label className="settings-field" htmlFor="profile-full-name">
              <span>Full Name</span>
              <input
                id="profile-full-name"
                className="ingredients-input"
                value={fullName}
                onChange={(event) => setFullName(event.target.value)}
                placeholder="Enter your full name"
              />
            </label>

            <label className="settings-field" htmlFor="profile-email">
              <span>Email Address</span>
              <input
                id="profile-email"
                className="ingredients-input"
                value={user.email || ''}
                disabled
                readOnly
              />
            </label>

            <button type="button" className="action-btn action-btn-primary" onClick={saveProfile} disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </article>
      </section>

      <section className="profile-panel">
        <div className="dash-card-header">
          <div>
            <h2>Account Statistics</h2>
            <p>Your InStock journey</p>
          </div>
        </div>
        <div className="profile-stats">
          <div><strong>{user.role || 'USER'}</strong><span>Account Type</span></div>
          <div><strong>{user.email ? 'Active' : 'N/A'}</strong><span>Account Status</span></div>
          <div><strong>{user.id || '-'}</strong><span>User ID</span></div>
        </div>
      </section>
    </div>
  );
}

export default ProfilePage;
