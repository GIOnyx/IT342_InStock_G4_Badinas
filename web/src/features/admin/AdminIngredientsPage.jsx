import { useEffect, useMemo, useState } from 'react';
import {
  createMasterIngredient,
  deleteMasterIngredient,
  fetchAdminStats,
  fetchAdminUsers,
  fetchMasterIngredients,
} from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

function AdminIngredientsPage() {
  const addToast = useToast();
  const [items, setItems] = useState([]);
  const [users, setUsers] = useState([]);
  const [stats, setStats] = useState({ totalUsers: 0, totalPantryItems: 0, totalFavorites: 0 });
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [loadingIngredients, setLoadingIngredients] = useState(false);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [loadingStats, setLoadingStats] = useState(false);
  const [creating, setCreating] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const loadIngredients = async () => {
    setLoadingIngredients(true);
    try {
      const res = await fetchMasterIngredients();
      setItems(res.data?.data || []);
    } catch (e) {
      addToast('Unable to load master ingredients', 'error');
    } finally {
      setLoadingIngredients(false);
    }
  };

  const loadUsers = async () => {
    setLoadingUsers(true);
    try {
      const res = await fetchAdminUsers();
      setUsers(res.data?.data || []);
    } catch (e) {
      addToast('Unable to load users', 'error');
    } finally {
      setLoadingUsers(false);
    }
  };

  const loadStats = async () => {
    setLoadingStats(true);
    try {
      const res = await fetchAdminStats();
      setStats(res.data?.data || { totalUsers: 0, totalPantryItems: 0, totalFavorites: 0 });
    } catch (e) {
      addToast('Unable to load system stats', 'error');
    } finally {
      setLoadingStats(false);
    }
  };

  useEffect(() => {
    loadIngredients();
    loadUsers();
    loadStats();
  }, []);

  const totalIngredients = useMemo(() => items.length, [items.length]);

  const handleCreate = async () => {
    if (!name.trim()) return addToast('Name required', 'error');
    setCreating(true);
    try {
      await createMasterIngredient({ name: name.trim(), category });
      addToast('Ingredient created', 'success');
      setName(''); setCategory('');
      await loadIngredients();
    } catch (e) {
      addToast(e.response?.data?.message || 'Create failed', 'error');
    } finally { setCreating(false); }
  };

  const handleDelete = async (id) => {
    setDeletingId(id);
    try {
      await deleteMasterIngredient(id);
      addToast('Deleted', 'success');
      setItems(items.filter(i => i.id !== id));
    } catch (e) {
      addToast('Delete failed', 'error');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="admin-ingredients-page">
      <header className="dashboard-header">
        <h1>Admin</h1>
        <p>System oversight for users and ingredients.</p>
      </header>

      <section className="stats-grid">
        <div className="stat-card">
          <div>
            <div className="stat-label">Total Users</div>
            <div className="stat-value">{loadingStats ? '...' : stats.totalUsers}</div>
          </div>
        </div>
        <div className="stat-card">
          <div>
            <div className="stat-label">Master Ingredients</div>
            <div className="stat-value">{loadingIngredients ? '...' : totalIngredients}</div>
          </div>
        </div>
      </section>

      <section className="dash-card">
        <div className="dash-card-header">
          <div>
            <h2>Registered Users</h2>
            <p>Admin-only list of accounts.</p>
          </div>
        </div>

        {loadingUsers && (
          <div className="results-state">
            <div className="flex items-center gap-2">
              <span className="inline-block h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-slate-700" />
              <span>Loading users...</span>
            </div>
          </div>
        )}
        {!loadingUsers && users.length === 0 && (
          <div className="results-state">No users registered yet.</div>
        )}
        {!loadingUsers && users.length > 0 && (
          <div className="overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead>
                <tr className="border-b border-slate-200 text-slate-500">
                  <th className="py-3 pr-4 font-semibold">Name</th>
                  <th className="py-3 pr-4 font-semibold">Email</th>
                  <th className="py-3 pr-4 font-semibold">Role</th>
                  <th className="py-3 font-semibold">Account ID</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id} className="border-b border-slate-100">
                    <td className="py-3 pr-4 text-slate-900">{user.fullName || 'Unnamed user'}</td>
                    <td className="py-3 pr-4 text-slate-600">{user.email || 'No email'}</td>
                    <td className="py-3 pr-4">
                      <span
                        className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${
                          user.role === 'ADMIN'
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-slate-100 text-slate-600'
                        }`}
                      >
                        {user.role || 'USER'}
                      </span>
                    </td>
                    <td className="py-3 text-slate-500">{user.id ?? '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <section className="dash-card">
        <div className="dash-card-header">
          <div>
            <h2>Master Ingredients</h2>
            <p>Create and manage the central ingredient list.</p>
          </div>
        </div>

        <div className="search-controls">
          <div className="form-column">
            <label htmlFor="ingredient-name">Name</label>
            <input
              id="ingredient-name"
              className="ingredients-input"
              placeholder="Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="form-column">
            <label htmlFor="ingredient-category">Category</label>
            <input
              id="ingredient-category"
              className="ingredients-input"
              placeholder="Category"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            />
          </div>
          <div className="form-column">
            <label className="helper-text">&nbsp;</label>
            <button type="button" className="action-btn action-btn-primary" onClick={handleCreate} disabled={creating}>
              {creating ? 'Creating...' : 'Create'}
            </button>
          </div>
        </div>

        {loadingIngredients && <div className="results-state">Loading ingredients...</div>}
        {!loadingIngredients && items.length === 0 && <div className="results-state">No master ingredients yet.</div>}
        {!loadingIngredients && items.length > 0 && (
          <ul className="admin-list">
            {items.map((it) => (
              <li key={it.id}>
                <strong>{it.name}</strong> <em>{it.category}</em>
                <button type="button" onClick={() => handleDelete(it.id)} disabled={deletingId === it.id}>
                  {deletingId === it.id ? 'Deleting...' : 'Delete'}
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

export default AdminIngredientsPage;
