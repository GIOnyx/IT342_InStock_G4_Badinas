import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import api from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

vi.mock('./Auth.css', () => ({}));
vi.mock('../../assets/google.svg', () => ({ default: 'google.svg' }));
vi.mock('../../core/components/Navbar', () => ({ default: function MockNavbar() {
  return <div data-testid="mock-navbar" />;
}}));
vi.mock('../../core/components/Toast', () => ({
  __esModule: true,
  useToast: vi.fn(),
}));
vi.mock('../../core/services/api', () => ({
  __esModule: true,
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    __esModule: true,
    ...actual,
    useNavigate: vi.fn(),
  };
});

describe('Auth feature', () => {
  const mockNavigate = vi.fn();
  const mockAddToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    window.localStorage.clear();
    useNavigate.mockReturnValue(mockNavigate);
    useToast.mockReturnValue(mockAddToast);
  });

  it('renders the Login component', () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    expect(screen.getByRole('heading', { name: /welcome back/i })).toBeTruthy();
    expect(screen.getByRole('button', { name: /^sign in$/i })).toBeTruthy();
    expect(screen.getByLabelText(/email/i)).toBeTruthy();
    expect(screen.getByLabelText(/password/i)).toBeTruthy();
  });

  it('submits login form and calls login API', async () => {
    const user = userEvent.setup();
    api.post.mockResolvedValueOnce({
      data: {
        data: {
          token: 'token-123',
          role: 'USER',
          email: 'user@example.com',
        },
      },
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    await user.type(screen.getByLabelText(/email/i), 'user@example.com');
    await user.type(screen.getByLabelText(/password/i), 'secret123');
    await user.click(screen.getByRole('button', { name: /^sign in$/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/login', {
        email: 'user@example.com',
        password: 'secret123',
      });
    });

    expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    expect(window.localStorage.getItem('token')).toBe('token-123');
  });

  it('renders the Register component', () => {
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    expect(screen.getByRole('heading', { name: /create an account/i })).toBeTruthy();
    expect(screen.getByRole('button', { name: /create account/i })).toBeTruthy();
    expect(screen.getByLabelText(/full name/i)).toBeTruthy();
    expect(screen.getByLabelText(/email/i)).toBeTruthy();
  });

  it('submits register form and calls register API', async () => {
    const user = userEvent.setup();
    api.post.mockResolvedValueOnce({
      data: {
        data: {
          token: 'reg-token-123',
          role: 'USER',
          email: 'new.user@example.com',
          fullName: 'New User',
        },
      },
    });

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    await user.type(screen.getByLabelText(/full name/i), 'New User');
    await user.type(screen.getByLabelText(/email/i), 'new.user@example.com');
    await user.type(screen.getByLabelText(/^password$/i), 'password123');
    await user.type(screen.getByLabelText(/confirm password/i), 'password123');
    await user.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/register', {
        fullName: 'New User',
        email: 'new.user@example.com',
        password: 'password123',
      });
    });

    expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    expect(window.localStorage.getItem('token')).toBe('reg-token-123');
  });
});
