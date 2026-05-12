import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';

function RecipeDetailPage() {
  const { recipeId } = useParams();
  const navigate = useNavigate();
  const addToast = useToast();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [pantryItems, setPantryItems] = useState([]);

  const highResImage = (url) => {
    if (!url) {
      return 'https://placehold.co/900x620?text=Recipe';
    }
    return url.replace(/(\d+)x(\d+)/, '636x393');
  };

  const cleanSummary = (value) => {
    if (!value) {
      return '';
    }

    return value.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim();
  };

  const normalizeIngredientTokens = (value) => {
    const ignoredWords = new Set([
      'a',
      'an',
      'and',
      'or',
      'of',
      'to',
      'taste',
      'cup',
      'cups',
      'tablespoon',
      'tablespoons',
      'teaspoon',
      'teaspoons',
      'tbsp',
      'tsp',
      'ounce',
      'ounces',
      'oz',
      'gram',
      'grams',
      'g',
      'kg',
      'ml',
      'liter',
      'liters',
      'pinch',
      'diced',
      'chopped',
      'sliced',
      'minced',
      'fresh',
      'small',
      'medium',
      'large',
    ]);

    return value
      .toLowerCase()
      .replace(/[^a-z\s]/g, ' ')
      .split(/\s+/)
      .map((token) => token.trim())
      .filter(Boolean)
      .map((token) => (token.endsWith('s') && token.length > 3 ? token.slice(0, -1) : token))
      .filter((token) => !ignoredWords.has(token));
  };

  const userHasIngredient = (ingredient) => {
    const ingredientTokens = normalizeIngredientTokens(ingredient);

    return pantryItems.some((item) => {
      const pantryTokens = normalizeIngredientTokens(item.name || '');

      if (pantryTokens.length === 0) {
        return false;
      }

      return pantryTokens.every((token) => ingredientTokens.includes(token));
    });
  };

  useEffect(() => {
    const loadRecipeDetails = async () => {
      setLoading(true);
      try {
        const [recipeResponse, favoritesResponse, pantryResponse] = await Promise.all([
          api.get(`/recipes/${recipeId}`),
          api.get('/favorites').catch(() => ({ data: { data: [] } })),
          api.get('/stock').catch(() => ({ data: { data: [] } })),
        ]);
        const nextRecipe = recipeResponse.data?.data || null;
        const favorites = favoritesResponse.data?.data || [];

        setRecipe(nextRecipe);
        setPantryItems(pantryResponse.data?.data || []);
        setIsFavorite(favorites.some((item) => Number(item.externalRecipeId) === Number(recipeId)));
      } catch {
        addToast('Unable to load recipe details.', 'error');
      } finally {
        setLoading(false);
      }
    };

    loadRecipeDetails();
  }, [addToast, recipeId]);

  const addToFavorites = async () => {
    if (!recipe || isFavorite) {
      return;
    }

    setFavoriteLoading(true);
    try {
      const summary = cleanSummary(recipe.summary)
        || `${recipe.readyInMinutes || '-'} min, ${recipe.servings || '-'} servings`;

      const response = await api.post('/favorites', {
        externalRecipeId: recipe.recipeId,
        title: recipe.title,
        imageUrl: recipe.imageUrl,
        summary,
        likes: recipe.likes || 0,
      });

      if (response.data?.data) {
        const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
        localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
          pantryCount: Number(overview.pantryCount || 0),
          favoritesCount: Number(overview.favoritesCount || 0) + 1,
        }));
      }

      setIsFavorite(true);
      addToast('Added to favorites.', 'success');
    } catch (error) {
      if (error.response?.status === 409) {
        setIsFavorite(true);
        addToast('Already in favorites.', 'info');
      } else {
        addToast('Unable to add favorite.', 'error');
      }
    } finally {
      setFavoriteLoading(false);
    }
  };

  if (loading) {
    return <div className="results-state">Loading recipe details...</div>;
  }

  if (!recipe) {
    return <div className="results-state">Recipe not found.</div>;
  }

  const summary = cleanSummary(recipe.summary);
  const ingredients = recipe.ingredients || [];
  const instructions = recipe.instructions || [];
  const ownedIngredientCount = ingredients.filter(userHasIngredient).length;

  return (
    <div className="recipe-detail-page">
      <header className="dashboard-header detail-header">
        <button type="button" className="action-btn action-btn-secondary detail-back-btn" onClick={() => navigate(-1)}>
          <span aria-hidden="true">&lt;-</span>
          <span>Back to Recipes</span>
        </button>
      </header>

      <section className="recipe-detail-hero">
        <div className="recipe-detail-media">
          <img
            className="recipe-detail-image"
            src={highResImage(recipe.imageUrl)}
            alt={recipe.title}
            loading="lazy"
            decoding="async"
          />
        </div>

        <div className="recipe-detail-summary">
          <div>
            <p className="recipe-detail-kicker">Full recipe</p>
            <h1>{recipe.title}</h1>
            <div className="recipe-detail-stats">
              <span>{recipe.readyInMinutes || '-'} min</span>
              <span>{recipe.servings || '-'} servings</span>
              <span>{ingredients.length} ingredients</span>
              <span>{ownedIngredientCount} in pantry</span>
            </div>
          </div>

          {summary && <p className="recipe-detail-description">{summary}</p>}

          <div className="recipe-detail-actions">
            <button
              type="button"
              className="action-btn action-btn-primary"
              onClick={addToFavorites}
              disabled={isFavorite || favoriteLoading}
            >
              {isFavorite ? 'In Favorites' : favoriteLoading ? 'Adding...' : 'Add to Favorites'}
            </button>
            {recipe.sourceUrl && (
              <a className="action-btn action-btn-secondary" href={recipe.sourceUrl} target="_blank" rel="noreferrer">
                Open Original Source
              </a>
            )}
          </div>
        </div>
      </section>

      <section className="recipe-detail-grid">
        <article className="recipe-detail-panel">
          <div className="dash-card-header">
            <div>
              <h2>Ingredients</h2>
              <p>{ownedIngredientCount} available, {ingredients.length - ownedIngredientCount} missing</p>
            </div>
          </div>
          <ul className="detail-list detail-list-ingredients">
            {ingredients.length === 0 && <li>No ingredients available.</li>}
            {ingredients.map((item, index) => {
              const isOwned = userHasIngredient(item);

              return (
                <li
                  key={`${item}-${index}`}
                  className={`detail-ingredient ${isOwned ? 'detail-ingredient-owned' : 'detail-ingredient-missing'}`}
                >
                  <span>{item}</span>
                  <strong>{isOwned ? 'Have' : 'Missing'}</strong>
                </li>
              );
            })}
          </ul>
        </article>

        <article className="recipe-detail-panel recipe-detail-panel-wide">
          <div className="dash-card-header">
            <div>
              <h2>Instructions</h2>
              <p>{instructions.length} step{instructions.length === 1 ? '' : 's'}</p>
            </div>
          </div>
          <ol className="detail-list detail-list-steps">
            {instructions.length === 0 && <li>No instructions available.</li>}
            {instructions.map((step, index) => (
              <li key={`${step}-${index}`}>{step}</li>
            ))}
          </ol>
        </article>
      </section>
    </div>
  );
}

export default RecipeDetailPage;
