import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';
import './DashboardShell.css';

const NAV_ITEMS = [
  { key: 'overview', label: 'Overview', path: '/dashboard/overview' },
  { key: 'pantry', label: 'My Pantry', path: '/dashboard/pantry' },
  { key: 'recipes', label: 'Recipe Finder', path: '/dashboard/recipes' },
  { key: 'favorites', label: 'Favorites', path: '/dashboard/favorites' },
  { key: 'profile', label: 'Profile', path: '/dashboard/profile' },
  { key: 'settings', label: 'Settings', path: '/dashboard/settings' },
];

function DashboardShell() {
  const navigate = useNavigate();
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || '{}'));

  useEffect(() => {
    const loadCurrentUser = async () => {
      try {
        const response = await api.get('/auth/me');
        const currentUser = response.data?.data;
        if (currentUser) {
          setUser(currentUser);
          localStorage.setItem('user', JSON.stringify(currentUser));
        }
      } catch {
        // Keep local fallback user.
      }
    };

    loadCurrentUser();
  }, []);

  const initials = user.fullName
    ? user.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase()
    : 'U';

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-top">
          <div className="sidebar-brand">
            <div className="sidebar-badge">IS</div>
            <span className="sidebar-brand-name">InStock</span>
          </div>

          <nav className="sidebar-nav">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.key}
                to={item.path}
                className={({ isActive }) => `sidebar-nav-link${isActive ? ' active' : ''}`}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>

        <div className="sidebar-footer">
          <div className="sidebar-avatar">{initials}</div>
          <div className="sidebar-user-info">
            <div className="sidebar-user-name">{user.fullName || 'User'}</div>
            <div className="sidebar-user-email">{user.email || ''}</div>
          </div>
          <button className="sidebar-logout-btn" onClick={handleLogout} title="Logout">
            Logout
          </button>
        </div>
      </aside>

      <main className="dashboard-main">
        <Outlet context={{ user }} />
      </main>
    </div>
  );
}

export default DashboardShell;
