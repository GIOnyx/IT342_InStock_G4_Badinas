import { useEffect, useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import api from '../services/api';
import { useToast } from './Toast';
import googleLogo from '../assets/google.svg';

function AuthPanel({ mode = 'register', onModeChange }) {
  const navigate = useNavigate();
  const location = useLocation();
  const addToast = useToast();

  const [loginData, setLoginData] = useState({ email: '', password: '' });
  const [registerData, setRegisterData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');

    if (token) {
      localStorage.setItem('token', token);
      addToast('Signed in with Google', 'success');
      navigate('/dashboard', { replace: true });
    }
  }, [location.search, navigate, addToast]);

  useEffect(() => {
    setError('');
    setFieldErrors({});
  }, [mode]);

  const handleLoginChange = (e) => {
    setLoginData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setFieldErrors((prev) => ({ ...prev, [e.target.name]: '' }));
  };

  const handleRegisterChange = (e) => {
    setRegisterData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setFieldErrors((prev) => ({ ...prev, [e.target.name]: '' }));
  };

  const handleGoogleClick = () => {
    window.location.href = '/oauth2/authorization/google';
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setLoading(true);

    try {
      const response = await api.post('/auth/login', loginData);
      const userData = response.data.data;
      localStorage.setItem('token', userData.token);
      localStorage.setItem('user', JSON.stringify(userData));
      addToast('Welcome back!', 'success');
      navigate('/dashboard');
    } catch (err) {
      const data = err.response?.data;
      if (data?.error?.code === 'VALIDATION-001' && data.error.details) {
        setFieldErrors(data.error.details);
        addToast('Please fix the errors below', 'error');
      } else {
        const msg = data?.message || 'Login failed. Please try again.';
        setError(msg);
        addToast(msg, 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});

    if (registerData.password !== registerData.confirmPassword) {
      setError('Passwords do not match');
      addToast('Passwords do not match', 'error');
      return;
    }

    setLoading(true);

    try {
      const payload = {
        fullName: registerData.fullName,
        email: registerData.email,
        password: registerData.password,
      };
      const response = await api.post('/auth/register', payload);
      const userData = response.data.data;
      localStorage.setItem('token', userData.token);
      localStorage.setItem('user', JSON.stringify(userData));
      addToast('Account created successfully! Welcome to InStock 🎉', 'success');
      navigate('/dashboard');
    } catch (err) {
      const data = err.response?.data;
      if (data?.error?.code === 'VALIDATION-001' && data.error.details) {
        setFieldErrors(data.error.details);
        addToast('Please fix the errors below', 'error');
      } else {
        const msg = data?.message || 'Registration failed. Please try again.';
        setError(msg);
        addToast(msg, 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const isLogin = mode === 'login';

  return (
    <div className="landing-auth-card">
      <div className="landing-auth-tabs" role="tablist" aria-label="Authentication mode">
        <button
          type="button"
          className={`landing-auth-tab ${!isLogin ? 'active' : ''}`}
          onClick={() => onModeChange('register')}
        >
          Sign Up
        </button>
        <button
          type="button"
          className={`landing-auth-tab ${isLogin ? 'active' : ''}`}
          onClick={() => onModeChange('login')}
        >
          Log In
        </button>
      </div>

      <div className="landing-auth-header">
        <div className="landing-auth-logo">IS</div>
        <h2>{isLogin ? 'Welcome back' : 'Create your account'}</h2>
        <p>
          {isLogin
            ? 'Sign in to your pantry and continue discovering recipes.'
            : 'Start reducing food waste and discover recipes with what you already have.'}
        </p>
      </div>

      {error && <div className="landing-error-banner">{error}</div>}

      {isLogin ? (
        <form onSubmit={handleLoginSubmit} className="landing-auth-form">
          <div className="landing-form-group">
            <label htmlFor="login-email">Email</label>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              </span>
              <input
                id="login-email"
                name="email"
                type="email"
                placeholder="you@example.com"
                value={loginData.email}
                onChange={handleLoginChange}
                required
              />
            </div>
            {fieldErrors.email && <span className="landing-field-error">{fieldErrors.email}</span>}
          </div>

          <div className="landing-form-group">
            <div className="landing-label-row">
              <label htmlFor="login-password">Password</label>
              <a href="#" onClick={(e) => { e.preventDefault(); addToast('Password reset coming soon!', 'info'); }}>
                Forgot password?
              </a>
            </div>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input
                id="login-password"
                name="password"
                type="password"
                placeholder="••••••••"
                value={loginData.password}
                onChange={handleLoginChange}
                required
              />
            </div>
            {fieldErrors.password && <span className="landing-field-error">{fieldErrors.password}</span>}
          </div>

          <button type="submit" className="landing-auth-submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleRegisterSubmit} className="landing-auth-form">
          <div className="landing-form-group">
            <label htmlFor="register-name">Full Name</label>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </span>
              <input
                id="register-name"
                name="fullName"
                type="text"
                placeholder="Jane Smith"
                value={registerData.fullName}
                onChange={handleRegisterChange}
                required
              />
            </div>
            {fieldErrors.fullName && <span className="landing-field-error">{fieldErrors.fullName}</span>}
          </div>

          <div className="landing-form-group">
            <label htmlFor="register-email">Email</label>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              </span>
              <input
                id="register-email"
                name="email"
                type="email"
                placeholder="you@example.com"
                value={registerData.email}
                onChange={handleRegisterChange}
                required
              />
            </div>
            {fieldErrors.email && <span className="landing-field-error">{fieldErrors.email}</span>}
          </div>

          <div className="landing-form-group">
            <label htmlFor="register-password">Password</label>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input
                id="register-password"
                name="password"
                type="password"
                placeholder="••••••••"
                value={registerData.password}
                onChange={handleRegisterChange}
                required
                minLength={8}
              />
            </div>
            {fieldErrors.password && <span className="landing-field-error">{fieldErrors.password}</span>}
          </div>

          <div className="landing-form-group">
            <label htmlFor="register-confirm-password">Confirm Password</label>
            <div className="landing-input-wrapper">
              <span className="landing-input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </span>
              <input
                id="register-confirm-password"
                name="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={registerData.confirmPassword}
                onChange={handleRegisterChange}
                required
                minLength={8}
              />
            </div>
          </div>

          <button type="submit" className="landing-auth-submit" disabled={loading}>
            {loading ? 'Creating account...' : 'Create account'}
          </button>
        </form>
      )}

      <div className="landing-auth-divider">or continue with</div>

      <button type="button" className="landing-google-btn" onClick={handleGoogleClick}>
        <span className="landing-google-icon">
          <img src={googleLogo} alt="Google" className="landing-google-svg" />
        </span>
        Continue with Google
      </button>

      <p className="landing-auth-footer">
        {isLogin ? (
          <>
            Don&apos;t have an account?{' '}
            <Link to="/register" onClick={() => onModeChange('register')}>Sign up</Link>
          </>
        ) : (
          <>
            Already have an account?{' '}
            <Link to="/login" onClick={() => onModeChange('login')}>Sign in</Link>
          </>
        )}
      </p>
    </div>
  );
}

export default AuthPanel;
