import { Routes, Route, Navigate } from 'react-router-dom';
import Landing from './pages/Landing';
import Dashboard from './pages/Dashboard';
import PrivateRoute from './components/PrivateRoute';

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
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route path="*" element={<SmartRedirect />} />
    </Routes>
  );
}

export default App;
