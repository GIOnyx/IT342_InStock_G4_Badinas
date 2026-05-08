import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../core/services/api';
import { useToast } from '../../core/components/Toast';
import Navbar from '../../core/components/Navbar';
import './Auth.css';
import googleLogo from '../../assets/google.svg';

function Register() {
  const navigate = useNavigate();
  const addToast = useToast();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setFieldErrors({ ...fieldErrors, [e.target.name]: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});

    if (formData.password !== confirmPassword) {
      setError('Passwords do not match');
      addToast('Passwords do not match', 'error');
      return;
    }

    setLoading(true);

    try {
      const response = await api.post('/auth/register', formData);
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

  const handleGoogleClick = () => {
    window.location.href = '/oauth2/authorization/google';
  };

  return (
    <div className="auth-page">
      <Navbar />
      <div className="auth-container">
        <div className="auth-shell">
          <section className="auth-visual">
            <div className="auth-visual-overlay" />
            <div className="auth-visual-content">
              <span className="auth-kicker">Fresh meals from what you have</span>
              <h2>Build your pantry. Unlock recipe ideas instantly.</h2>
              <p>
                Create your account to save ingredients, discover smarter meal options,
                and reduce waste with a pantry-first cooking experience.
              </p>
              <div className="auth-highlights">
                <span>Save pantry ingredients</span>
                <span>Filter recipes faster</span>
                <span>Access anywhere</span>
              </div>
            </div>
          </section>

          <div className="auth-card">
            <div className="auth-logo">IS</div>
            <div className="auth-header">
              <h1>Create an account</h1>
              <p>Start reducing food waste and discovering recipes</p>
            </div>

            {error && <div className="error-banner">{error}</div>}

            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <label htmlFor="fullName">Full Name</label>
                <div className="input-wrapper">
                  <span className="input-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  </span>
                  <input
                    id="fullName"
                    name="fullName"
                    type="text"
                    placeholder="Jane Smith"
                    value={formData.fullName}
                    onChange={handleChange}
                    required
                  />
                </div>
                {fieldErrors.fullName && <span className="field-error">{fieldErrors.fullName}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="email">Email</label>
                <div className="input-wrapper">
                  <span className="input-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
                  </span>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    placeholder="you@example.com"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                </div>
                {fieldErrors.email && <span className="field-error">{fieldErrors.email}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="password">Password</label>
                <div className="input-wrapper">
                  <span className="input-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  </span>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    placeholder="••••••••"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    minLength={8}
                  />
                </div>
                {fieldErrors.password && <span className="field-error">{fieldErrors.password}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="confirmPassword">Confirm Password</label>
                <div className="input-wrapper">
                  <span className="input-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  </span>
                  <input
                    id="confirmPassword"
                    type="password"
                    placeholder="••••••••"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    minLength={8}
                  />
                </div>
              </div>

              <button type="submit" className="auth-submit" disabled={loading}>
                {loading ? 'Creating account...' : 'Create account'}
              </button>
            </form>

            <div className="auth-divider">or continue with</div>

            <button type="button" className="google-btn" onClick={handleGoogleClick}>
              <span className="google-icon"><img src={googleLogo} alt="Google" className="google-svg"/></span>
              Continue with Google
            </button>

            <p className="auth-footer">
              Already have an account? <Link to="/login">Sign in</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;
