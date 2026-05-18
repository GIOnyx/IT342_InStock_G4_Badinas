import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api, { removeFavorite as removeFavoriteRequest } from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';
const FAVORITES_CACHE_KEY = 'instock_favorites_cache';

function FavoritesPage() {
  const navigate = useNavigate();
  const addToast = useToast();
  const [isOffline, setIsOffline] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [removingId, setRemovingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [favorites, setFavorites] = useState(() => {
    // Seed from cache so the list renders before the network call
    try {
      const cached = localStorage.getItem(FAVORITES_CACHE_KEY);
      return cached ? JSON.parse(cached) : [];
    } catch {
      return [];
    }
  });

  const highResImage = (url) => {
    if (!url) {
      return 'https://placehold.co/900x620?text=Recipe';
    }
    return url.replace(/(\d+)x(\d+)/, '636x393');
  };

  const loadFavorites = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/favorites');
      const items = response.data?.data || [];
      setFavorites(items);
      setIsOffline(false);
      // Persist to cache for offline use
      localStorage.setItem(FAVORITES_CACHE_KEY, JSON.stringify(items));
    } catch {
      // Network failed — serve from cache silently
      const cached = localStorage.getItem(FAVORITES_CACHE_KEY);
      if (cached) {
        try {
          setFavorites(JSON.parse(cached));
          setIsOffline(true);
        } catch {
          // Cache corrupted — ignore
        }
      } else {
        addToast('Unable to load favorites.', 'error');
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadFavorites();
  }, []);

  const filteredFavorites = useMemo(() => {
    const query = searchTerm.trim().toLowerCase();
    const list = query
      ? favorites.filter((item) => (item.title || '').toLowerCase().includes(query))
      : favorites;
    return [...list].sort((a, b) => (b.likes || 0) - (a.likes || 0));
  }, [favorites, searchTerm]);

  const removeFavorite = async (id) => {
    setRemovingId(id);
    try {
      await removeFavoriteRequest(id);
      setFavorites((prev) => {
        const next = prev.filter((item) => item.id !== id);
        localStorage.setItem(FAVORITES_CACHE_KEY, JSON.stringify(next));
        return next;
      });
      const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
      localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
        pantryCount: Number(overview.pantryCount || 0),
        favoritesCount: Math.max(0, Number(overview.favoritesCount || 0) - 1),
      }));
      addToast('Favorite removed.', 'success');
    } catch {
      addToast('Unable to remove favorite.', 'error');
    } finally {
      setRemovingId(null);
    }
  };

  return (
    <>
      <header className="dashboard-header">
        <h1>Favorites</h1>
        <p>Your saved recipes from the database.</p>
        {isOffline && (
          <p className="offline-banner">
            📶 Offline — showing cached favorites.
          </p>
        )}
      </header>

      <section className="dash-card">
        <div className="search-controls">
          <div className="form-column">
            <label htmlFor="favorite-search">Search Favorites</label>
            <input
              id="favorite-search"
              className="ingredients-input"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              placeholder="Search by recipe title"
            />
          </div>
        </div>

        {isLoading && <div className="results-state">Loading favorites...</div>}

        {!isLoading && filteredFavorites.length === 0 && (
          <div className="results-state">
            {searchTerm.trim() ? 'No matching favorites found.' : 'No favorites yet.'}
          </div>
        )}

        {!isLoading && filteredFavorites.length > 0 && (
          <div className="recipe-grid">
            {filteredFavorites.map((item) => (
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
                    <button
                      type="button"
                      className="action-btn action-btn-secondary"
                      onClick={() => navigate(`/dashboard/recipes/${item.externalRecipeId}`)}
                    >
                      View Full Recipe
                    </button>
                    <button
                      type="button"
                      className="favorite-remove"
                      onClick={() => removeFavorite(item.id)}
                      disabled={removingId === item.id}
                    >
                      {removingId === item.id ? 'Removing...' : 'Remove'}
                    </button>
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
