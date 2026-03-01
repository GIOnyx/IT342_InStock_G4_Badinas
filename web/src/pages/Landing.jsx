import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import './Landing.css';

function Landing() {
  return (
    <div className="landing">
      <Navbar />

      <section className="hero">
        <div className="hero-content">
          <div className="hero-badge">
            ✨ Smart pantry &amp; recipe management
          </div>
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
            <Link to="/register" className="btn-primary">Get Started Free</Link>
            <Link to="/login" className="btn-secondary">Sign In</Link>
          </div>
        </div>
        <div className="hero-image">
          <div className="hero-placeholder">
            <div className="hero-placeholder-inner">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/>
                <path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/>
              </svg>
              <span>App Preview</span>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="section how-it-works" id="how">
        <div className="section-label">HOW IT WORKS</div>
        <h2 className="section-title">Three steps to smarter cooking</h2>
        <p className="section-subtitle">Get started in minutes and start saving food today</p>
        <div className="steps-grid">
          <div className="step-card">
            <div className="step-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/>
              </svg>
            </div>
            <h3>1. Add Ingredients</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quickly add items from your pantry.</p>
          </div>
          <div className="step-card">
            <div className="step-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
              </svg>
            </div>
            <h3>2. Get Suggestions</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Smart recipe suggestions for you.</p>
          </div>
          <div className="step-card">
            <div className="step-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z"/><path d="m9 12 2 2 4-4"/>
              </svg>
            </div>
            <h3>3. Cook &amp; Enjoy</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Follow step-by-step instructions.</p>
          </div>
        </div>
      </section>

      {/* Why Choose InStock */}
      <section className="section why-choose" id="features">
        <div className="section-label">WHY INSTOCK</div>
        <h2 className="section-title">Why Choose InStock?</h2>
        <p className="section-subtitle">Everything you need to manage your kitchen smarter</p>
        <div className="features-grid">
          <div className="feature-item">
            <div className="feature-icon green">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"/><path d="m9 12 2 2 4-4"/>
              </svg>
            </div>
            <h4>Reduce Waste</h4>
            <p>Know exactly what&rsquo;s in your pantry and use it before it expires. Save money and reduce environmental impact.</p>
          </div>
          <div className="feature-item">
            <div className="feature-icon blue">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
            </div>
            <h4>Save Time</h4>
            <p>Instantly find recipes that match your available ingredients. No more endless scrolling for meal ideas.</p>
          </div>
          <div className="feature-item">
            <div className="feature-icon orange">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </div>
            <h4>Discover Recipes</h4>
            <p>Get personalized recipe suggestions tailored to your taste preferences and what&rsquo;s currently in your pantry.</p>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="cta-section">
        <div className="cta-card">
          <h2>Ready to Start Cooking Smarter?</h2>
          <p>Join thousands of home cooks who have already reduced waste and discovered amazing recipes.</p>
          <Link to="/register" className="cta-button">Join Now &mdash; It&rsquo;s Free</Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <p>&copy; 2026 InStock. Built to reduce food waste, one pantry at a time.</p>
      </footer>
    </div>
  );
}

export default Landing;
