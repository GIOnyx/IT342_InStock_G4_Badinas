import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useToast } from '../components/Toast';
import './Dashboard.css';

const NAV_ITEMS = [
  {
    key: 'overview',
    label: 'Overview',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
        <rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>
      </svg>
    ),
  },
  {
    key: 'pantry',
    label: 'My Pantry',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/>
        <path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/>
      </svg>
    ),
  },
  {
    key: 'recipes',
    label: 'Recipe Finder',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
        <path d="M8 7h6"/><path d="M8 11h8"/>
      </svg>
    ),
  },
  {
    key: 'favorites',
    label: 'Favorites',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/>
      </svg>
    ),
  },
  {
    key: 'profile',
    label: 'Profile',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="8" r="4"/><path d="M6 20v-2a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v2"/>
      </svg>
    ),
  },
  {
    key: 'settings',
    label: 'Settings',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
        <circle cx="12" cy="12" r="3"/>
      </svg>
    ),
  },
];

const SECTION_IDS = {
  overview: 'overview-section',
  pantry: 'pantry-section',
  recipes: 'recipes-section',
  favorites: 'favorites-section',
  profile: 'profile-section',
  settings: 'settings-section',
};

const DEFAULT_INGREDIENTS = 'chicken, rice, tomato, onion, garlic';

const INGREDIENT_LIBRARY = [
  'apple', 'banana', 'beef', 'bell pepper', 'broccoli', 'butter', 'cabbage', 'carrot', 'cauliflower', 'cheese',
  'chicken', 'chili', 'coconut milk', 'corn', 'cucumber', 'egg', 'flour', 'garlic', 'ginger', 'green beans',
  'ground pork', 'ham', 'lettuce', 'lemon', 'lime', 'milk', 'mushroom', 'noodles', 'olive oil', 'onion',
  'pasta', 'potato', 'pumpkin', 'rice', 'salmon', 'shrimp', 'soy sauce', 'spinach', 'sugar', 'sweet potato',
  'tomato', 'tofu', 'tuna', 'vinegar', 'yogurt', 'zucchini'
];

function Dashboard() {
  const navigate = useNavigate();
  const addToast = useToast();

  const [activeSection, setActiveSection] = useState('overview');
  const [ingredientsInput, setIngredientsInput] = useState(DEFAULT_INGREDIENTS);
  const [maxResults, setMaxResults] = useState(10);
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [error, setError] = useState('');
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || '{}'));
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [pantryItems, setPantryItems] = useState([]);
  const [favorites, setFavorites] = useState([]);
  const [savingPantry, setSavingPantry] = useState(false);

  const firstName = user.fullName ? user.fullName.split(' ')[0] : 'User';
  const initials = user.fullName
    ? user.fullName.split(' ').map((n) => n[0]).join('').slice(0, 2).toUpperCase()
    : 'U';

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [userRes, pantryRes, favoritesRes] = await Promise.all([
          api.get('/auth/me'),
          api.get('/stock'),
          api.get('/favorites'),
        ]);

        const currentUser = userRes.data?.data;
        if (currentUser) {
          setUser(currentUser);
          localStorage.setItem('user', JSON.stringify(currentUser));
        }

        const pantryData = pantryRes.data?.data || [];
        setPantryItems(pantryData);

        const favoriteData = favoritesRes.data?.data || [];
        setFavorites(favoriteData);
      } catch {
        // Keep local fallback values when API calls fail.
      }
    };

    loadInitialData();
  }, []);

  const parsedIngredients = useMemo(() => {
    const unique = new Set();
    return ingredientsInput
      .split(',')
      .map((item) => item.trim())
      .filter((item) => item.length > 0)
      .filter((item) => {
        const key = item.toLowerCase();
        if (unique.has(key)) {
          return false;
        }
        unique.add(key);
        return true;
      });
  }, [ingredientsInput]);

  const favoriteRecipeCandidates = useMemo(() => {
    return [...favorites].sort((a, b) => b.likes - a.likes).slice(0, 6);
  }, [favorites]);

  const favoriteExternalIds = useMemo(() => {
    return new Set(favorites.map((item) => item.externalRecipeId));
  }, [favorites]);

  const stats = useMemo(() => {
    if (!recipes.length) {
      return {
        returned: 0,
        avgMatched: 0,
        avgMissing: 0,
        bestLikes: 0,
      };
    }

    const matchedTotal = recipes.reduce((acc, recipe) => acc + recipe.matchedIngredientCount, 0);
    const missingTotal = recipes.reduce((acc, recipe) => acc + recipe.missingIngredientCount, 0);
    const bestLikes = recipes.reduce((max, recipe) => Math.max(max, recipe.likes), 0);

    return {
      returned: recipes.length,
      avgMatched: Math.round((matchedTotal / recipes.length) * 10) / 10,
      avgMissing: Math.round((missingTotal / recipes.length) * 10) / 10,
      bestLikes,
    };
  }, [recipes]);

  const updateSuggestions = (inputValue) => {
    const lastChunk = inputValue.split(',').pop() || '';
    const query = lastChunk.trim().toLowerCase();

    if (!query) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    const selected = new Set(parsedIngredients.map((item) => item.toLowerCase()));
    const matches = INGREDIENT_LIBRARY
      .filter((ingredient) => ingredient.includes(query) && !selected.has(ingredient.toLowerCase()))
      .slice(0, 8);

    setSuggestions(matches);
    setShowSuggestions(matches.length > 0);
  };

  const handleIngredientChange = (event) => {
    const nextValue = event.target.value;
    setIngredientsInput(nextValue);
    setError('');
    updateSuggestions(nextValue);
  };

  const applySuggestion = (ingredientName) => {
    const chunks = ingredientsInput.split(',');
    chunks[chunks.length - 1] = ` ${ingredientName}`;
    const composed = `${chunks.join(',').replace(/^\s+/, '')}, `;
    setIngredientsInput(composed);
    setSuggestions([]);
    setShowSuggestions(false);
  };

  const handleIngredientKeyDown = (event) => {
    if ((event.key === 'Enter' || event.key === 'Tab') && showSuggestions && suggestions.length) {
      event.preventDefault();
      applySuggestion(suggestions[0]);
    }

    if (event.key === 'Escape') {
      setShowSuggestions(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const handleNavClick = (sectionKey) => {
    setActiveSection(sectionKey);
    const section = document.getElementById(SECTION_IDS[sectionKey]);
    if (section) {
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  const handleSearch = async () => {
    setError('');

    if (!parsedIngredients.length) {
      const message = 'Please enter at least one ingredient.';
      setError(message);
      addToast(message, 'error');
      return;
    }

    if (parsedIngredients.length > 15) {
      const message = 'Use up to 15 ingredients per search for better results.';
      setError(message);
      addToast(message, 'error');
      return;
    }

    const invalidIngredient = parsedIngredients.find((item) => !/^[a-zA-Z\s-]+$/.test(item));
    if (invalidIngredient) {
      const message = `Only letters, spaces, and hyphens are allowed: ${invalidIngredient}`;
      setError(message);
      addToast(message, 'error');
      return;
    }

    setLoading(true);
    setHasSearched(true);

    try {
      const response = await api.get('/recipes/search', {
        params: {
          ingredients: parsedIngredients.join(','),
          number: maxResults,
        },
      });

      const payload = response.data?.data || [];
      setRecipes(payload);
      addToast(`Found ${payload.length} recipe suggestion(s).`, 'success');
    } catch (requestError) {
      const message = requestError.response?.data?.message || 'Unable to load recipes right now. Please try again.';
      setRecipes([]);
      setError(message);
      addToast(message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const savePantryFromInput = async () => {
    if (!parsedIngredients.length) {
      addToast('Add at least one ingredient first.', 'info');
      return;
    }

    setSavingPantry(true);
    let createdCount = 0;

    for (const ingredient of parsedIngredients) {
      try {
        await api.post('/stock', { name: ingredient });
        createdCount += 1;
      } catch (requestError) {
        if (requestError.response?.status !== 409) {
          addToast(`Failed to save ${ingredient}`, 'error');
        }
      }
    }

    try {
      const pantryRes = await api.get('/stock');
      setPantryItems(pantryRes.data?.data || []);
    } catch {
      addToast('Unable to refresh pantry list.', 'error');
    }

    addToast(
      createdCount > 0
        ? `${createdCount} ingredient(s) added to pantry.`
        : 'Ingredients are already in pantry.',
      'success'
    );
    setSavingPantry(false);
  };

  const removePantryItem = async (id) => {
    try {
      await api.delete(`/stock/${id}`);
      setPantryItems((prev) => prev.filter((item) => item.id !== id));
      addToast('Ingredient removed from pantry.', 'success');
    } catch {
      addToast('Unable to remove pantry item.', 'error');
    }
  };

  const addToFavorites = async (recipe) => {
    try {
      const payload = {
        externalRecipeId: recipe.recipeId,
        title: recipe.title,
        imageUrl: recipe.imageUrl,
        summary: `Matched ${recipe.matchedIngredientCount}, missing ${recipe.missingIngredientCount}`,
        likes: recipe.likes,
      };

      const response = await api.post('/favorites', payload);
      const saved = response.data?.data;
      if (saved) {
        setFavorites((prev) => [saved, ...prev]);
      }
      addToast('Recipe added to favorites.', 'success');
    } catch (requestError) {
      if (requestError.response?.status === 409) {
        addToast('Recipe is already in favorites.', 'info');
      } else {
        addToast('Unable to add favorite recipe.', 'error');
      }
    }
  };

  const removeFavorite = async (favoriteId) => {
    try {
      await api.delete(`/favorites/${favoriteId}`);
      setFavorites((prev) => prev.filter((item) => item.id !== favoriteId));
      addToast('Favorite removed.', 'success');
    } catch {
      addToast('Unable to remove favorite.', 'error');
    }
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
            {NAV_ITEMS.map((item) => {
              const isActive = activeSection === item.key;
              return (
                <button
                  key={item.key}
                  className={`sidebar-nav-item${isActive ? ' active' : ''}`}
                  onClick={() => handleNavClick(item.key)}
                >
                  {item.icon}
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>
        </div>

        <div className="sidebar-footer">
          <div className="sidebar-avatar">{initials}</div>
          <div className="sidebar-user-info">
            <div className="sidebar-user-name">{user.fullName || 'User'}</div>
            <div className="sidebar-user-email">{user.email || ''}</div>
          </div>
          <button className="sidebar-logout-btn" onClick={handleLogout} title="Logout">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </button>
        </div>
      </aside>

      <main className="dashboard-main">
        <header id="overview-section" className="dashboard-header">
          <h1>Welcome back, {firstName}!</h1>
          <p>Use your pantry ingredients to discover recipes faster.</p>
        </header>

        <section className="stats-grid">
          <div className="stat-card">
            <div className="stat-info">
              <div className="stat-label">Recipes Returned</div>
              <div className="stat-value">{stats.returned}</div>
            </div>
            <div className="stat-icon">R</div>
          </div>

          <div className="stat-card">
            <div className="stat-info">
              <div className="stat-label">Avg Matched</div>
              <div className="stat-value">{stats.avgMatched}</div>
            </div>
            <div className="stat-icon">M</div>
          </div>

          <div className="stat-card">
            <div className="stat-info">
              <div className="stat-label">Avg Missing</div>
              <div className="stat-value">{stats.avgMissing}</div>
            </div>
            <div className="stat-icon">X</div>
          </div>

          <div className="stat-card">
            <div className="stat-info">
              <div className="stat-label">Best Likes</div>
              <div className="stat-value">{stats.bestLikes}</div>
            </div>
            <div className="stat-icon">L</div>
          </div>
        </section>

        <section id="pantry-section" className="dash-card">
          <div className="dash-card-header">
            <h2>My Pantry</h2>
            <p>Saved pantry ingredients from your account.</p>
          </div>

          <div className="search-actions" style={{ marginTop: 0, marginBottom: '0.9rem' }}>
            <button
              className="action-btn action-btn-primary"
              type="button"
              onClick={savePantryFromInput}
              disabled={savingPantry}
            >
              {savingPantry ? 'Saving...' : 'Save Current Ingredients to Pantry'}
            </button>
          </div>

          <div className="chip-list">
            {pantryItems.length === 0 && <span className="muted-text">No pantry items yet.</span>}
            {pantryItems.map((item) => (
              <button
                key={item.id}
                type="button"
                className="pantry-chip pantry-chip-removable"
                title="Remove from pantry"
                onClick={() => removePantryItem(item.id)}
              >
                {item.name} ×
              </button>
            ))}
          </div>
        </section>

        <section id="recipes-section" className="dash-card recipe-search-card">
          <div className="dash-card-header">
            <h2>Recipe Finder</h2>
            <p>Type ingredients separated by commas. Autocomplete is enabled.</p>
          </div>

          <div className="search-controls">
            <div className="form-column autocomplete-wrap">
              <label htmlFor="ingredients">Ingredients</label>
              <input
                id="ingredients"
                className="ingredients-input"
                value={ingredientsInput}
                onChange={handleIngredientChange}
                onKeyDown={handleIngredientKeyDown}
                onBlur={() => setTimeout(() => setShowSuggestions(false), 120)}
                onFocus={() => updateSuggestions(ingredientsInput)}
                placeholder="tomato, egg, spinach"
                autoComplete="off"
              />

              {showSuggestions && (
                <div className="autocomplete-menu">
                  {suggestions.map((option) => (
                    <button
                      key={option}
                      type="button"
                      className="autocomplete-item"
                      onClick={() => applySuggestion(option)}
                    >
                      {option}
                    </button>
                  ))}
                </div>
              )}

              <div className="helper-text">{parsedIngredients.length} unique ingredient(s) detected</div>
              <div className="quick-suggestions">
                {INGREDIENT_LIBRARY.slice(0, 10).map((ingredient) => (
                  <button
                    key={ingredient}
                    type="button"
                    className="quick-chip"
                    onClick={() => applySuggestion(ingredient)}
                  >
                    + {ingredient}
                  </button>
                ))}
              </div>
            </div>

            <div className="form-column small-column">
              <label htmlFor="maxResults">Max Results</label>
              <select
                id="maxResults"
                className="result-select"
                value={maxResults}
                onChange={(event) => setMaxResults(Number(event.target.value))}
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={15}>15</option>
                <option value={20}>20</option>
              </select>
            </div>
          </div>

          <div className="search-actions">
            <button className="action-btn action-btn-primary" type="button" onClick={handleSearch} disabled={loading}>
              {loading ? 'Finding recipes...' : 'Suggest Recipes'}
            </button>
            <button
              className="action-btn action-btn-secondary"
              type="button"
              onClick={() => {
                setIngredientsInput(DEFAULT_INGREDIENTS);
                setError('');
                setShowSuggestions(false);
              }}
            >
              Use Sample Ingredients
            </button>
            <button
              className="action-btn action-btn-secondary"
              type="button"
              onClick={() => {
                setIngredientsInput('');
                setRecipes([]);
                setHasSearched(false);
                setError('');
                setSuggestions([]);
                setShowSuggestions(false);
              }}
            >
              Clear
            </button>
          </div>

          {error && <div className="inline-error">{error}</div>}
        </section>

        <section className="recipe-results-wrap">
          {loading && <div className="results-state">Searching recipes from the backend...</div>}

          {!loading && hasSearched && recipes.length === 0 && !error && (
            <div className="results-state">No recipes matched your current ingredients. Try adding more ingredients.</div>
          )}

          {!loading && recipes.length > 0 && (
            <div className="recipe-grid">
              {recipes.map((recipe) => (
                <article key={recipe.recipeId} className="recipe-card">
                  <img
                    className="recipe-image"
                    src={recipe.imageUrl || 'https://placehold.co/600x400?text=Recipe'}
                    alt={recipe.title}
                  />
                  <div className="recipe-body">
                    <h3>{recipe.title}</h3>
                    <p className="recipe-meta">
                      {recipe.matchedIngredientCount} matched | {recipe.missingIngredientCount} missing | {recipe.likes} likes
                    </p>

                    <div className="ingredient-badges">
                      {recipe.matchedIngredients?.slice(0, 5).map((ingredient) => (
                        <span key={`${recipe.recipeId}-${ingredient}`} className="badge badge-matched">
                          {ingredient}
                        </span>
                      ))}
                      {recipe.missingIngredients?.slice(0, 3).map((ingredient) => (
                        <span key={`${recipe.recipeId}-${ingredient}-missing`} className="badge badge-missing">
                          {ingredient}
                        </span>
                      ))}
                    </div>

                    <div className="recipe-actions">
                      <button
                        type="button"
                        className="action-btn action-btn-secondary"
                        onClick={() => addToFavorites(recipe)}
                        disabled={favoriteExternalIds.has(recipe.recipeId)}
                      >
                        {favoriteExternalIds.has(recipe.recipeId) ? 'In Favorites' : 'Add to Favorites'}
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>

        <section id="favorites-section" className="dash-card">
          <div className="dash-card-header">
            <h2>Favorites</h2>
            <p>Top liked recipes from your latest search results.</p>
          </div>

          {favoriteRecipeCandidates.length === 0 ? (
            <div className="results-state">No favorites yet. Run a recipe search first.</div>
          ) : (
            <div className="favorites-list">
              {favoriteRecipeCandidates.map((recipe) => (
                <div key={`fav-${recipe.id}`} className="favorite-row">
                  <span>{recipe.title}</span>
                  <div className="favorite-row-actions">
                    <strong>{recipe.likes} likes</strong>
                    <button type="button" className="favorite-remove" onClick={() => removeFavorite(recipe.id)}>
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <section id="profile-section" className="dash-card">
          <div className="dash-card-header">
            <h2>Profile</h2>
            <p>Authenticated user profile from backend.</p>
          </div>

          <div className="profile-grid">
            <div className="profile-item"><span>Name</span><strong>{user.fullName || 'N/A'}</strong></div>
            <div className="profile-item"><span>Email</span><strong>{user.email || 'N/A'}</strong></div>
            <div className="profile-item"><span>Role</span><strong>{user.role || 'USER'}</strong></div>
          </div>
        </section>

        <section id="settings-section" className="dash-card">
          <div className="dash-card-header">
            <h2>Settings</h2>
            <p>Theme and search behavior preferences.</p>
          </div>

          <div className="settings-list">
            <div className="setting-row"><span>Theme</span><strong>Main Green/Gold</strong></div>
            <div className="setting-row"><span>Autocomplete</span><strong>Enabled</strong></div>
            <div className="setting-row"><span>Search Limit</span><strong>Up to 20 results</strong></div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default Dashboard;
