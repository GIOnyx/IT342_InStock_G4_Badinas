import { Routes, Route, Navigate } from 'react-router-dom';
import Landing from './pages/Landing';
import PrivateRoute from './components/PrivateRoute';
import DashboardShell from './components/DashboardShell';
import OverviewPage from './pages/OverviewPage';
import PantryPage from './pages/PantryPage';
import RecipesPage from './pages/RecipesPage';
import FavoritesPage from './pages/FavoritesPage';
import ProfilePage from './pages/ProfilePage';
import SettingsPage from './pages/SettingsPage';
import RecipeDetailPage from './pages/RecipeDetailPage';

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
