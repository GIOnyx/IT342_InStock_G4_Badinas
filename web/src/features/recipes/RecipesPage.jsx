import { useEffect, useMemo, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import api from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

const PREFILL_STORAGE_KEY = 'instock_prefill_ingredients';
const OVERVIEW_STATS_KEY = 'instock_overview_stats';
const RECIPE_SUGGESTIONS = [
  'butter chicken',
  'chicken adobo',
  'beef broccoli',
  'garlic shrimp',
  'pork sisig',
  'fried rice',
  'carbonara',
  'chicken curry',
  'banana bread',
  'pancit canton'
];

function RecipesPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const addToast = useToast();
  const [ingredientsInput, setIngredientsInput] = useState('');
  const [maxResults, setMaxResults] = useState(10);
  const [recipes, setRecipes] = useState([]);
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
  const [isPantryPrefillMode, setIsPantryPrefillMode] = useState(false);
  const [backgroundPantryIngredients, setBackgroundPantryIngredients] = useState([]);
  const hasProcessedPrefillRef = useRef(false);
  const hasLoadedFavoritesRef = useRef(false);

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

  const favoriteExternalIds = useMemo(() => new Set(favorites.map((item) => item.externalRecipeId)), [favorites]);

  const updateSuggestions = (value) => {
    const q = value.trim().toLowerCase();
    if (!q) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }
    const list = RECIPE_SUGGESTIONS.filter((item) => item.includes(q)).slice(0, 8);
    setSuggestions(list);
    setShowSuggestions(list.length > 0);
  };

  const applySuggestion = (ingredient) => {
    setIngredientsInput(ingredient);
    setIsPantryPrefillMode(false);
    setShowSuggestions(false);
  };

  const highResImage = (url) => {
    if (!url) {
      return 'https://placehold.co/900x620?text=Recipe';
    }
    return url.replace(/(\d+)x(\d+)/, '636x393');
  };

  const runSearch = async (ingredientsList = []) => {
    const query = ingredientsInput.trim();
    const hasBackgroundIngredients = ingredientsList.length > 0;

    if (!query && !hasBackgroundIngredients) {
      addToast('Please enter a recipe name.', 'error');
      return;
    }

    setLoading(true);
    try {
      const response = hasBackgroundIngredients
        ? await api.get('/recipes/search', {
          params: {
            ingredients: ingredientsList.join(','),
            number: maxResults,
          },
        })
        : await api.get('/recipes/search-by-name', {
          params: {
            query,
            number: maxResults,
          },
        });

      setRecipes(response.data?.data || []);
      localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
        pantryCount: Number(JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}')?.pantryCount || 0),
        favoritesCount: favorites.length,
      }));
      addToast('Recipes loaded.', 'success');
    } catch {
      addToast('Unable to load recipes.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    await runSearch(isPantryPrefillMode ? backgroundPantryIngredients : []);
  };

  useEffect(() => {
    if (hasProcessedPrefillRef.current) {
      return;
    }
    hasProcessedPrefillRef.current = true;

    const stateIngredients = location.state?.pantryIngredients;
    const storedIngredients = localStorage.getItem(PREFILL_STORAGE_KEY);

    let prefill = [];

    if (Array.isArray(stateIngredients) && stateIngredients.length) {
      prefill = stateIngredients;
    } else if (storedIngredients) {
      try {
        const parsed = JSON.parse(storedIngredients);
        if (Array.isArray(parsed) && parsed.length) {
          prefill = parsed;
        }
      } catch {
        // Ignore bad storage payload.
      }
    }

    if (prefill.length) {
      setIngredientsInput('');
      setBackgroundPantryIngredients(prefill);
      setIsPantryPrefillMode(true);
      runSearch(prefill);
      localStorage.removeItem(PREFILL_STORAGE_KEY);
    }
  }, []);

  useEffect(() => {
    if (hasLoadedFavoritesRef.current) {
      return;
    }
    hasLoadedFavoritesRef.current = true;

    const loadFavorites = async () => {
      try {
        const response = await api.get('/favorites');
        setFavorites(response.data?.data || []);
      } catch {
        // Ignore favorites preload failure.
      }
    };

    loadFavorites();
  }, []);

  const clearRecipesView = () => {
    setIngredientsInput('');
    setRecipes([]);
    setSuggestions([]);
    setShowSuggestions(false);
    setIsPantryPrefillMode(false);
    setBackgroundPantryIngredients([]);
    localStorage.removeItem(PREFILL_STORAGE_KEY);
    addToast('Recipe finder reset. You can now search as a regular recipe book.', 'success');
  };

  const addToFavorites = async (recipe) => {
    try {
      const response = await api.post('/favorites', {
        externalRecipeId: recipe.recipeId,
        title: recipe.title,
        imageUrl: recipe.imageUrl,
        summary: `Matched ${recipe.matchedIngredientCount}, missing ${recipe.missingIngredientCount}`,
        likes: recipe.likes,
      });
      const saved = response.data?.data;
      if (saved) {
        setFavorites((prev) => [saved, ...prev]);
        const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
        localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
          pantryCount: Number(overview.pantryCount || 0),
          favoritesCount: Number(overview.favoritesCount || 0) + 1,
        }));
      }
      addToast('Added to favorites.', 'success');
    } catch (error) {
      if (error.response?.status === 409) {
        addToast('Already in favorites.', 'info');
      } else {
        addToast('Unable to add favorite.', 'error');
      }
    }
  };

  return (
    <>
      <header className="dashboard-header">
        <h1>Recipe Finder</h1>
        <p>Search and save recipes using recipe keywords or ingredient terms.</p>
      </header>

      <section className="dash-card">
        <div className="search-controls">
          <div className="form-column autocomplete-wrap">
            <label htmlFor="ingredients">Recipe</label>
            <input
              id="ingredients"
              className="ingredients-input"
              value={ingredientsInput}
              onChange={(e) => {
                setIngredientsInput(e.target.value);
                setIsPantryPrefillMode(false);
                setBackgroundPantryIngredients([]);
                updateSuggestions(e.target.value);
              }}
              onFocus={() => updateSuggestions(ingredientsInput)}
              onBlur={() => setTimeout(() => setShowSuggestions(false), 120)}
              onKeyDown={(event) => {
                if ((event.key === 'Enter' || event.key === 'Tab') && showSuggestions && suggestions.length) {
                  event.preventDefault();
                  applySuggestion(suggestions[0]);
                  return;
                }

                if (event.key === 'Enter') {
                  event.preventDefault();
                  handleSearch();
                }
              }}
              placeholder="Search recipe (e.g., beef broccoli)"
            />
            {showSuggestions && (
              <div className="autocomplete-menu">
                {suggestions.map((item) => (
                  <button key={item} type="button" className="autocomplete-item" onClick={() => applySuggestion(item)}>
                    {item}
                  </button>
                ))}
              </div>
            )}
            <div className="helper-text">
              {isPantryPrefillMode
                ? `Using ${backgroundPantryIngredients.length} pantry ingredient(s) in the background`
                : 'Recipe title search mode'}
            </div>
          </div>

          <div className="form-column">
            <label htmlFor="maxResults">Max Results</label>
            <select id="maxResults" className="result-select" value={maxResults} onChange={(e) => setMaxResults(Number(e.target.value))}>
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={15}>15</option>
              <option value={20}>20</option>
            </select>
          </div>
        </div>

        <div className="search-actions">
          <button className="action-btn action-btn-primary" type="button" onClick={handleSearch} disabled={loading}>
            {loading ? 'Searching recipes...' : 'Search Recipe'}
          </button>
          <button className="action-btn action-btn-secondary" type="button" onClick={clearRecipesView}>
            Clear Recipes
          </button>
        </div>
      </section>

      <section className="recipe-results-wrap">
        {!loading && recipes.length === 0 && (
          <div className="results-state">No recipes loaded yet. Search using ingredients above.</div>
        )}

        {!loading && recipes.length > 0 && (
          <div className="recipe-grid">
            {recipes.map((recipe) => (
              <article key={recipe.recipeId} className="recipe-card">
                <button
                  type="button"
                  className="recipe-clickable"
                  onClick={() => navigate(`/dashboard/recipes/${recipe.recipeId}`)}
                  title="View full recipe"
                >
                  <img
                    className="recipe-image"
                    src={highResImage(recipe.imageUrl)}
                    alt={recipe.title}
                    loading="lazy"
                    decoding="async"
                  />
                </button>
                <div className="recipe-body">
                  <button type="button" className="recipe-title-btn" onClick={() => navigate(`/dashboard/recipes/${recipe.recipeId}`)}>
                    {recipe.title}
                  </button>
                  <p className="recipe-meta">{recipe.matchedIngredientCount} matched | {recipe.missingIngredientCount} missing | {recipe.likes} likes</p>
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
    </>
  );
}

export default RecipesPage;
