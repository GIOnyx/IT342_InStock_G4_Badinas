import { useEffect, useState } from 'react';
import api from '../services/api';

function ProfilePage() {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || '{}'));

  useEffect(() => {
    const load = async () => {
      try {
        const response = await api.get('/auth/me');
        if (response.data?.data) {
          setUser(response.data.data);
        }
      } catch {
        // Keep fallback.
      }
    };

    load();
  }, []);

  return (
    <>
      <header className="dashboard-header">
        <h1>Profile</h1>
        <p>Authenticated user profile from backend.</p>
      </header>

      <section className="dash-card profile-grid">
        <div className="profile-item"><span>Name</span><strong>{user.fullName || 'N/A'}</strong></div>
        <div className="profile-item"><span>Email</span><strong>{user.email || 'N/A'}</strong></div>
        <div className="profile-item"><span>Role</span><strong>{user.role || 'USER'}</strong></div>
      </section>
    </>
  );
}

export default ProfilePage;
