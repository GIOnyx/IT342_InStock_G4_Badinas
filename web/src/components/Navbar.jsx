import { Link } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        <span className="navbar-logo">IS</span>
        <span className="navbar-title">InStock</span>
      </Link>
      <div className="navbar-links">
        <Link to="/" className="navbar-link">Home</Link>
        <Link to="#how" className="navbar-link">How It Works</Link>
        <Link to="#features" className="navbar-link">Features</Link>
      </div>
      <div className="navbar-actions">
        <Link to="/login" className="navbar-link">Log in</Link>
        <Link to="/register" className="navbar-btn">Sign Up</Link>
      </div>
    </nav>
  );
}

export default Navbar;

