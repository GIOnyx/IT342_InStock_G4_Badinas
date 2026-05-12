export const SETTINGS_STORAGE_KEY = 'instock_web_settings';

// Spoonacular-recognised intolerance values (maps to their `intolerances` param)
export const ALLERGEN_OPTIONS = [
  { key: 'dairy',     label: 'Dairy' },
  { key: 'egg',       label: 'Egg' },
  { key: 'gluten',    label: 'Gluten' },
  { key: 'grain',     label: 'Grain' },
  { key: 'peanut',    label: 'Peanut' },
  { key: 'seafood',   label: 'Seafood' },
  { key: 'sesame',    label: 'Sesame' },
  { key: 'shellfish', label: 'Shellfish' },
  { key: 'soy',       label: 'Soy' },
  { key: 'sulfite',   label: 'Sulfite' },
  { key: 'tree nut',  label: 'Tree Nut' },
  { key: 'wheat',     label: 'Wheat' },
];

export const DEFAULT_WEB_SETTINGS = {
  theme: 'classic',
  defaultRecipeLimit: 10,
  autocompleteEnabled: true,
  allergens: [],
};

export const THEME_OPTIONS = {
  classic: {
    label: 'Main Green/Gold',
    colors: {
      '--theme-primary': '#2e7d32',
      '--theme-primary-dark': '#25682a',
      '--theme-secondary': '#e9c46a',
      '--theme-bg': '#f6faf6',
      '--theme-surface': '#ffffff',
      '--theme-border': '#d7e4d9',
      '--theme-text': '#153423',
      '--theme-muted': '#6b7f74',
    },
  },
  sage: {
    label: 'Soft Sage',
    colors: {
      '--theme-primary': '#3f7d68',
      '--theme-primary-dark': '#2f604f',
      '--theme-secondary': '#d6b85a',
      '--theme-bg': '#f4f8f5',
      '--theme-surface': '#ffffff',
      '--theme-border': '#d5e1d9',
      '--theme-text': '#17362c',
      '--theme-muted': '#62796d',
    },
  },
  orchard: {
    label: 'Orchard',
    colors: {
      '--theme-primary': '#477a2d',
      '--theme-primary-dark': '#365e22',
      '--theme-secondary': '#c98f45',
      '--theme-bg': '#f7f8f2',
      '--theme-surface': '#ffffff',
      '--theme-border': '#dfe5cf',
      '--theme-text': '#263819',
      '--theme-muted': '#71805f',
    },
  },
};

export function getWebSettings() {
  try {
    const stored = JSON.parse(localStorage.getItem(SETTINGS_STORAGE_KEY) || '{}');
    return {
      ...DEFAULT_WEB_SETTINGS,
      ...stored,
      defaultRecipeLimit: Number(stored.defaultRecipeLimit || DEFAULT_WEB_SETTINGS.defaultRecipeLimit),
      autocompleteEnabled: stored.autocompleteEnabled ?? DEFAULT_WEB_SETTINGS.autocompleteEnabled,
      allergens: Array.isArray(stored.allergens) ? stored.allergens : [],
    };
  } catch {
    return DEFAULT_WEB_SETTINGS;
  }
}

export function saveWebSettings(settings) {
  const nextSettings = {
    ...DEFAULT_WEB_SETTINGS,
    ...settings,
    defaultRecipeLimit: Number(settings.defaultRecipeLimit || DEFAULT_WEB_SETTINGS.defaultRecipeLimit),
    allergens: Array.isArray(settings.allergens) ? settings.allergens : [],
  };

  localStorage.setItem(SETTINGS_STORAGE_KEY, JSON.stringify(nextSettings));
  window.dispatchEvent(new CustomEvent('instock-settings-change', { detail: nextSettings }));
  return nextSettings;
}

export function resetWebSettings() {
  localStorage.removeItem(SETTINGS_STORAGE_KEY);
  window.dispatchEvent(new CustomEvent('instock-settings-change', { detail: DEFAULT_WEB_SETTINGS }));
  return DEFAULT_WEB_SETTINGS;
}

export function applyWebSettings(settings = getWebSettings()) {
  const theme = THEME_OPTIONS[settings.theme] || THEME_OPTIONS[DEFAULT_WEB_SETTINGS.theme];

  Object.entries(theme.colors).forEach(([name, value]) => {
    document.documentElement.style.setProperty(name, value);
  });
}
