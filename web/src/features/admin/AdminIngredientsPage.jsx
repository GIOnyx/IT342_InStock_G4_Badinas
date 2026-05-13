import { useEffect, useState } from 'react';
import { fetchMasterIngredients, createMasterIngredient, deleteMasterIngredient } from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

function AdminIngredientsPage() {
  const addToast = useToast();
  const [items, setItems] = useState([]);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [loading, setLoading] = useState(false);

  const load = async () => {
    try {
      const res = await fetchMasterIngredients();
      setItems(res.data?.data || []);
    } catch (e) {
      addToast('Unable to load master ingredients', 'error');
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async () => {
    if (!name.trim()) return addToast('Name required', 'error');
    setLoading(true);
    try {
      await createMasterIngredient({ name: name.trim(), category });
      addToast('Ingredient created', 'success');
      setName(''); setCategory('');
      await load();
    } catch (e) {
      addToast(e.response?.data?.message || 'Create failed', 'error');
    } finally { setLoading(false); }
  };

  const handleDelete = async (id) => {
    try {
      await deleteMasterIngredient(id);
      addToast('Deleted', 'success');
      setItems(items.filter(i => i.id !== id));
    } catch (e) {
      addToast('Delete failed', 'error');
    }
  };

  return (
    <div className="admin-ingredients-page">
      <h1>Master Ingredients</h1>

      <div className="admin-create">
        <input placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} />
        <input placeholder="Category" value={category} onChange={(e) => setCategory(e.target.value)} />
        <button onClick={handleCreate} disabled={loading}>{loading ? 'Creating...' : 'Create'}</button>
      </div>

      <ul className="admin-list">
        {items.map(it => (
          <li key={it.id}>
            <strong>{it.name}</strong> <em>{it.category}</em>
            <button onClick={() => handleDelete(it.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default AdminIngredientsPage;
