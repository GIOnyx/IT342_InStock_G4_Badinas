import { Routes, Route, Navigate } from 'react-router-dom';
import Landing from './features/landing/Landing';
import PrivateRoute from './core/components/PrivateRoute';
import DashboardShell from './core/components/DashboardShell';
import OverviewPage from './features/dashboard/OverviewPage';
import PantryPage from './features/pantry/PantryPage';
import RecipesPage from './features/recipes/RecipesPage';
import FavoritesPage from './features/favorites/FavoritesPage';
import ProfilePage from './features/profile/ProfilePage';
import SettingsPage from './features/profile/SettingsPage';
import RecipeDetailPage from './features/recipes/RecipeDetailPage';

function SmartRedirect() {
  const token = localStorage.getItem('token');
  return token ? <Navigate to="/dashboard" replace /> : <Navigate to="/" replace />;
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/register" element={<Landing />} />
      <Route path="/login" element={<Landing />} />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <DashboardShell />
          </PrivateRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard/overview" replace />} />
        <Route path="overview" element={<OverviewPage />} />
        <Route path="pantry" element={<PantryPage />} />
        <Route path="recipes" element={<RecipesPage />} />
        <Route path="recipes/:recipeId" element={<RecipeDetailPage />} />
        <Route path="favorites" element={<FavoritesPage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="settings" element={<SettingsPage />} />
      </Route>
      <Route path="*" element={<SmartRedirect />} />
    </Routes>
  );
}

export default App;
