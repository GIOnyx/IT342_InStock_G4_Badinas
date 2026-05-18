import { useLocation, useNavigate } from 'react-router-dom';
import { useMemo } from 'react';
import Navbar from '../../core/components/Navbar';
import AuthPanel from '../auth/AuthPanel';
import './Landing.css';

function Landing() {
  const location = useLocation();
  const navigate = useNavigate();

  const authMode = useMemo(() => {
    if (location.pathname === '/login') {
      return 'login';
    }
    return 'register';
  }, [location.pathname]);

  const handleModeChange = (mode) => {
    navigate(mode === 'login' ? '/login' : '/register');
  };

  return (
    <div className="landing">
      <Navbar />

      <section className="hero">
        <div className="hero-inner">
          <div className="hero-content">
              <div className="hero-badge">Smart pantry and recipe management</div>
              <h1>
                Cook with what you have,
                <br />
                <span className="hero-accent">built for student kitchens</span>
              </h1>
              <p className="hero-subtitle">
                Track pantry ingredients, filter out allergens, and get instant recipe ideas
                without spending extra on groceries.
              </p>
              <div className="hero-buttons">
                <button type="button" className="btn-primary" onClick={() => handleModeChange('register')}>
                  Cook with what you have
                </button>
                <button type="button" className="btn-secondary" onClick={() => handleModeChange('login')}>
                  Sign In
                </button>
              </div>

            <div className="hero-metrics">

        <section className="section" aria-label="How it works">
          <p className="section-label">How it Works</p>
          <h2 className="section-title">A simple 3-step cooking flow</h2>
          <p className="section-subtitle">Make smarter meals with your current pantry.</p>

          <div className="steps-grid">
            <article className="step-card">
              <div className="step-icon" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 7h18" />
                  <path d="M3 12h18" />
                  <path d="M3 17h18" />
                </svg>
              </div>
              <h3>Track Your Pantry</h3>
              <p>Add ingredients you currently have in stock.</p>
            </article>

            <article className="step-card">
              <div className="step-icon" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M12 3v18" />
                  <path d="M3 12h18" />
                </svg>
              </div>
              <h3>Set Dietary Filters</h3>
              <p>Exclude allergens like dairy or gluten.</p>
            </article>

            <article className="step-card">
              <div className="step-icon" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 4h16v16H4z" />
                  <path d="M9 9h6v6H9z" />
                </svg>
              </div>
              <h3>Discover Recipes</h3>
              <p>Get instant suggestions from the Spoonacular API.</p>
            </article>
          </div>
        </section>

        <section className="section" aria-label="Feature highlights">
          <p className="section-label">Feature Highlights</p>
          <h2 className="section-title">Built for secure, smart cooking</h2>
          <p className="section-subtitle">Everything you need to stay organized and inspired.</p>

          <div className="features-grid">
            <article className="feature-item">
              <div className="feature-icon green" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M12 1l3 7h7l-5.5 4.5L18 23l-6-4-6 4 1.5-10.5L2 8h7z" />
                </svg>
              </div>
              <h4>Secure Auth</h4>
              <p>Google OAuth and standard login built in.</p>
            </article>

            <article className="feature-item">
              <div className="feature-icon blue" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <h4>Personalized Profiles</h4>
              <p>Custom avatars and dietary settings per user.</p>
            </article>

            <article className="feature-item">
              <div className="feature-icon orange" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 7h16" />
                  <path d="M4 12h16" />
                  <path d="M4 17h16" />
                </svg>
              </div>
              <h4>Smart Sync</h4>
              <p>Pantry and favorites sync on mobile (coming soon).</p>
            </article>
          </div>
        </section>

        <section className="cta-section">
          <div className="cta-card">
            <h2>Start cooking smarter today</h2>
            <p>Join InStock to save pantry items, filter allergens, and discover recipes faster.</p>
            <button type="button" className="cta-button" onClick={() => handleModeChange('register')}>
              Create your free account
            </button>
          </div>
        </section>

        <footer className="landing-footer">
          Built for students who want smarter meals without extra spending.
        </footer>
              <div className="hero-metric-card">
                <strong>Pantry-first</strong>
                <span>Use what you already have before buying more.</span>
              </div>
              <div className="hero-metric-card">
                <strong>Recipe matching</strong>
                <span>Get quick meal ideas based on your available ingredients.</span>
              </div>
            </div>
          </div>

          <div className="hero-auth-panel">
            <AuthPanel mode={authMode} onModeChange={handleModeChange} />
          </div>
        </div>
      </section>


    </div>
  );
}

export default Landing;
