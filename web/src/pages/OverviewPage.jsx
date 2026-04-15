import { useEffect, useState } from 'react';
import api from '../services/api';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';

function OverviewPage() {
  const [pantryCount, setPantryCount] = useState(() => Number(JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}')?.pantryCount || 0));
  const [favoritesCount, setFavoritesCount] = useState(() => Number(JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}')?.favoritesCount || 0));

  useEffect(() => {
    const loadCounts = async () => {
      try {
        const [pantryRes, favoritesRes] = await Promise.all([
          api.get('/stock'),
          api.get('/favorites'),
        ]);
        const nextPantry = (pantryRes.data?.data || []).length;
        const nextFavorites = (favoritesRes.data?.data || []).length;

        setPantryCount(nextPantry);
        setFavoritesCount(nextFavorites);

        localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
          pantryCount: nextPantry,
          favoritesCount: nextFavorites,
        }));
      } catch {
        // Keep local persisted fallback counts.
      }
    };

    loadCounts();
  }, []);

  return (
    <>
      <header className="dashboard-header">
        <h1>Overview</h1>
        <p>Quick summary of your pantry and saved recipes.</p>
      </header>

      <section className="stats-grid">
        <div className="stat-card"><div><div className="stat-label">Pantry Items</div><div className="stat-value">{pantryCount}</div></div></div>
        <div className="stat-card"><div><div className="stat-label">Favorites</div><div className="stat-value">{favoritesCount}</div></div></div>
      </section>
    </>
  );
}

export default OverviewPage;
