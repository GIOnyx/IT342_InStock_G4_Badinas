import { useLocation, useNavigate } from 'react-router-dom';
import { useMemo } from 'react';
import Navbar from '../components/Navbar';
import AuthPanel from '../components/AuthPanel';
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
            <div className="hero-badge">✨ Smart pantry &amp; recipe management</div>
            <h1>
              Reduce Food Waste,
              <br />
              <span className="hero-accent">One Recipe at a Time</span>
            </h1>
            <p className="hero-subtitle">
              Track your pantry ingredients, discover recipes based on what you already
              have, and never waste food again. Simple, smart, and free.
            </p>
            <div className="hero-buttons">
              <button type="button" className="btn-primary" onClick={() => handleModeChange('register')}>Get Started Free</button>
              <button type="button" className="btn-secondary" onClick={() => handleModeChange('login')}>Sign In</button>
            </div>

            <div className="hero-metrics">
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
