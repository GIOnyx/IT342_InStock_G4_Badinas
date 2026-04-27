import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useToast } from '../components/Toast';

const OVERVIEW_STATS_KEY = 'instock_overview_stats';

const INGREDIENT_LIBRARY = [
  'apple', 'banana', 'beef', 'bell pepper', 'broccoli', 'butter', 'cabbage', 'carrot', 'cauliflower', 'cheese',
  'chicken', 'chili', 'coconut milk', 'corn', 'cucumber', 'egg', 'flour', 'garlic', 'ginger', 'green beans',
  'ground pork', 'ham', 'lettuce', 'lemon', 'lime', 'milk', 'mushroom', 'noodles', 'olive oil', 'onion',
  'pasta', 'potato', 'pumpkin', 'rice', 'salmon', 'shrimp', 'soy sauce', 'spinach', 'sugar', 'sweet potato',
  'tomato', 'tofu', 'tuna', 'vinegar', 'yogurt', 'zucchini'
];

function PantryPage() {
  const navigate = useNavigate();
  const addToast = useToast();
  const [pantryItems, setPantryItems] = useState([]);
  const [name, setName] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const hasLoadedPantryRef = useRef(false);

  const pantryNameSet = useMemo(() => {
    return new Set(pantryItems.map((item) => item.name.toLowerCase()));
  }, [pantryItems]);

  const loadPantry = async () => {
    try {
      const response = await api.get('/stock');
      const items = response.data?.data || [];
      setPantryItems(items);

      const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
      localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
        pantryCount: items.length,
        favoritesCount: Number(overview.favoritesCount || 0),
      }));
    } catch {
      addToast('Unable to load pantry.', 'error');
    }
  };

  useEffect(() => {
    if (hasLoadedPantryRef.current) {
      return;
    }
    hasLoadedPantryRef.current = true;
    loadPantry();
  }, []);

  const updateSuggestions = (value) => {
    const query = value.trim().toLowerCase();
    if (!query) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    const list = INGREDIENT_LIBRARY
      .filter((item) => item.includes(query) && !pantryNameSet.has(item.toLowerCase()))
      .slice(0, 8);

    setSuggestions(list);
    setShowSuggestions(list.length > 0);
  };

  const applySuggestion = (value) => {
    setName(value);
    setShowSuggestions(false);
  };

  const addItem = async () => {
    const trimmed = name.trim();
    if (!trimmed) {
      addToast('Enter an ingredient name.', 'error');
      return;
    }

    try {
      await api.post('/stock', { name: trimmed });
      setName('');
      await loadPantry();
      addToast('Ingredient added.', 'success');
    } catch (error) {
      if (error.response?.status === 409) {
        addToast('Ingredient already exists.', 'info');
      } else {
        addToast('Unable to add ingredient.', 'error');
      }
    }
  };

  const clearAll = async () => {
    if (!pantryItems.length) {
      addToast('Pantry is already empty.', 'info');
      return;
    }

    try {
      await api.delete('/stock');
      setPantryItems([]);
      const overview = JSON.parse(localStorage.getItem(OVERVIEW_STATS_KEY) || '{}');
      localStorage.setItem(OVERVIEW_STATS_KEY, JSON.stringify({
        pantryCount: 0,
        favoritesCount: Number(overview.favoritesCount || 0),
      }));
      addToast('Pantry cleared.', 'success');
    } catch {
      addToast('Unable to clear pantry.', 'error');
    }
  };

  const findRecipeFromPantry = () => {
    if (!pantryItems.length) {
      addToast('Add pantry ingredients first.', 'info');
      return;
    }

    const pantryIngredients = pantryItems.map((item) => item.name);
    localStorage.setItem('instock_prefill_ingredients', JSON.stringify(pantryIngredients));
    navigate('/dashboard/recipes', {
      state: { pantryIngredients },
    });
  };

  const removeItem = async (id) => {
    try {
      await api.delete(`/stock/${id}`);
      setPantryItems((prev) => prev.filter((item) => item.id !== id));
      addToast('Ingredient removed.', 'success');
    } catch {
      addToast('Unable to remove ingredient.', 'error');
    }
  };

  return (
    <>
      <header className="dashboard-header">
        <h1>My Pantry</h1>
        <p>Manage ingredients stored in your account.</p>
      </header>

      <section className="dash-card">
        <div className="search-actions autocomplete-wrap" style={{ marginTop: 0 }}>
          <input
            className="ingredients-input"
            value={name}
            onChange={(e) => {
              setName(e.target.value);
              updateSuggestions(e.target.value);
            }}
            onFocus={() => updateSuggestions(name)}
            onBlur={() => setTimeout(() => setShowSuggestions(false), 120)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') {
                event.preventDefault();
                if (showSuggestions && suggestions.length) {
                  applySuggestion(suggestions[0]);
                } else {
                  addItem();
                }
              }
            }}
            placeholder="Search ingredient"
          />

          {showSuggestions && (
            <div className="autocomplete-menu">
              {suggestions.map((item) => (
                <button
                  key={item}
                  type="button"
                  className="autocomplete-item"
                  onClick={() => applySuggestion(item)}
                >
                  {item}
                </button>
              ))}
            </div>
          )}

          <button className="action-btn action-btn-primary" type="button" onClick={addItem}>Add</button>
          <button className="action-btn action-btn-secondary" type="button" onClick={clearAll}>Clear All</button>
          <button className="action-btn action-btn-primary" type="button" onClick={findRecipeFromPantry}>Find Recipe</button>
        </div>
      </section>

      <section className="dash-card">
        <div className="chip-list">
          {pantryItems.length === 0 && <span className="muted-text">No pantry items yet.</span>}
          {pantryItems.map((item) => (
            <button key={item.id} type="button" className="pantry-chip pantry-chip-removable" onClick={() => removeItem(item.id)}>
              {item.name} -
            </button>
          ))}
        </div>
      </section>
    </>
  );
}

export default PantryPage;
