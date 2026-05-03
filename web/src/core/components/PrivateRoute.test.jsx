import { render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';

function renderWithRoutes(route, routeElement) {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <Routes>
        <Route path="/login" element={<div>Login Page</div>} />
        <Route path={route} element={routeElement} />
      </Routes>
    </MemoryRouter>
  );
}

describe('PrivateRoute', () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it('redirects unauthenticated users to login', () => {
    renderWithRoutes(
      '/dashboard',
      <PrivateRoute>
        <div>Protected Content</div>
      </PrivateRoute>
    );

    expect(screen.getByText('Login Page')).toBeTruthy();
    expect(screen.queryByText('Protected Content')).toBeNull();
  });

  it('allows authenticated users to access protected route', () => {
    window.localStorage.setItem('token', 'valid-token');
    window.localStorage.setItem('user', JSON.stringify({ role: 'USER' }));

    renderWithRoutes(
      '/dashboard',
      <PrivateRoute>
        <div>Protected Content</div>
      </PrivateRoute>
    );

    expect(screen.getByText('Protected Content')).toBeTruthy();
    expect(screen.queryByText('Login Page')).toBeNull();
  });

  it('blocks user role when allowedRoles does not include it', () => {
    window.localStorage.setItem('token', 'valid-token');
    window.localStorage.setItem('user', JSON.stringify({ role: 'USER' }));

    renderWithRoutes(
      '/admin',
      <PrivateRoute allowedRoles={['ADMIN']}>
        <div>Admin Content</div>
      </PrivateRoute>
    );

    expect(screen.getByText('Not Authorized')).toBeTruthy();
    expect(screen.queryByText('Admin Content')).toBeNull();
  });
});
