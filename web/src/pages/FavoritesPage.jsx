import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useToast } from '../components/Toast';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';

function FavoritesPage() {
  const navigate = useNavigate();
  const addToast = useToast();
  const [favorites, setFavorites] = useState([]);

  const highResImage = (url) => {
    if (!url) {
      return 'https://placehold.co/900x620?text=Recipe';
    }
    return url.replace(/(\d+)x(\d+)/, '636x393');
  };

  const loadFavorites = async () => {
    try {
      const response = await api.get('/favorites');
      setFavorites(response.data?.data || []);
    } catch {
      addToast('Unable to load favorites.', 'error');
    }
  };

  useEffect(() => {
    loadFavorites();
  }, []);

  const sortedFavorites = useMemo(() => [...favorites].sort((a, b) => b.likes - a.likes), [favorites]);

  const removeFavorite = async (id) => {
    try {
      await api.delete(`/favorites/${id}`);
      setFavorites((prev) => prev.filter((item) => item.id !== id));
      const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
      localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
        pantryCount: Number(overview.pantryCount || 0),
        favoritesCount: Math.max(0, Number(overview.favoritesCount || 0) - 1),
      }));
      addToast('Favorite removed.', 'success');
    } catch {
      addToast('Unable to remove favorite.', 'error');
    }
  };

  return (
    <>
      <header className="dashboard-header">
        <h1>Favorites</h1>
        <p>Your saved recipes from the database.</p>
      </header>

      <section className="dash-card">
        {sortedFavorites.length === 0 ? (
          <div className="results-state">No favorites yet.</div>
        ) : (
          <div className="recipe-grid">
            {sortedFavorites.map((item) => (
              <article key={item.id} className="recipe-card">
                <button
                  type="button"
                  className="recipe-clickable"
                  onClick={() => navigate(`/dashboard/recipes/${item.externalRecipeId}`)}
                  title="View full recipe"
                >
                  <img
                    className="recipe-image"
                    src={highResImage(item.imageUrl)}
                    alt={item.title}
                    loading="lazy"
                    decoding="async"
                  />
                </button>
                <div className="recipe-body">
                  <button
                    type="button"
                    className="recipe-title-btn"
                    onClick={() => navigate(`/dashboard/recipes/${item.externalRecipeId}`)}
                  >
                    {item.title}
                  </button>
                  <p className="recipe-meta">{item.likes} likes</p>
                  <div className="favorite-row-actions">
                    <button type="button" className="favorite-remove" onClick={() => removeFavorite(item.id)}>Remove</button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </>
  );
}

export default FavoritesPage;
