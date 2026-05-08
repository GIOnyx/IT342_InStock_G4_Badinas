import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

function RecipeDetailPage() {
  const { recipeId } = useParams();
  const navigate = useNavigate();
  const addToast = useToast();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);

  const highResImage = (url) => {
    if (!url) {
      return 'https://placehold.co/900x620?text=Recipe';
    }
    return url.replace(/(\d+)x(\d+)/, '636x393');
  };

  useEffect(() => {
    const loadRecipeDetails = async () => {
      setLoading(true);
      try {
        const response = await api.get(`/recipes/${recipeId}`);
        setRecipe(response.data?.data || null);
      } catch {
        addToast('Unable to load recipe details.', 'error');
      } finally {
        setLoading(false);
      }
    };

    loadRecipeDetails();
  }, [recipeId]);

  if (loading) {
    return <div className="results-state">Loading recipe details...</div>;
  }

  if (!recipe) {
    return <div className="results-state">Recipe not found.</div>;
  }

  return (
    <>
      <header className="dashboard-header detail-header">
        <button type="button" className="action-btn action-btn-secondary detail-back-btn" onClick={() => navigate(-1)}>
          <span aria-hidden="true">←</span>
          <span>Back to Recipes</span>
        </button>
        <h1>{recipe.title}</h1>
        <p>{recipe.readyInMinutes || '-'} min | {recipe.servings || '-'} servings</p>
      </header>

      <section className="dash-card recipe-detail-card">
        <img
          className="recipe-detail-image"
          src={highResImage(recipe.imageUrl)}
          alt={recipe.title}
          loading="lazy"
          decoding="async"
        />

        <div className="recipe-detail-grid">
          <div className="dash-card">
            <div className="dash-card-header">
              <h2>Ingredients</h2>
            </div>
            <ul className="detail-list">
              {(recipe.ingredients || []).length === 0 && <li>No ingredients available.</li>}
              {(recipe.ingredients || []).map((item, index) => (
                <li key={`${item}-${index}`}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="dash-card">
            <div className="dash-card-header">
              <h2>Instructions</h2>
            </div>
            <ol className="detail-list">
              {(recipe.instructions || []).length === 0 && <li>No instructions available.</li>}
              {(recipe.instructions || []).map((step, index) => (
                <li key={`${step}-${index}`}>{step}</li>
              ))}
            </ol>
          </div>
        </div>

        {recipe.sourceUrl && (
          <div className="search-actions">
            <a className="action-btn action-btn-primary" href={recipe.sourceUrl} target="_blank" rel="noreferrer">
              Open Original Source
            </a>
          </div>
        )}
      </section>
    </>
  );
}

export default RecipeDetailPage;
