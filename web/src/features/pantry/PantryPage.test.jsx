import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useNavigate } from 'react-router-dom';
import PantryPage from './PantryPage';
import api, { updatePantryItem } from '../../core/services/api';
import { useToast } from '../../core/components/Toast';

vi.mock('../../core/components/Toast', () => ({
  __esModule: true,
  useToast: vi.fn(),
}));
vi.mock('../../core/services/api', () => ({
  __esModule: true,
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
  updatePantryItem: vi.fn(),
}));
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    __esModule: true,
    ...actual,
    useNavigate: vi.fn(),
  };
});

describe('PantryPage', () => {
  const mockNavigate = vi.fn();
  const mockAddToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    window.localStorage.clear();
    useNavigate.mockReturnValue(mockNavigate);
    useToast.mockReturnValue(mockAddToast);
  });

  function setUserRole(role) {
    window.localStorage.setItem('user', JSON.stringify({ role }));
  }

  it('renders pantry items from the API', async () => {
    setUserRole('USER');
    api.get.mockResolvedValueOnce({
      data: {
        data: [
          { id: 1, name: 'rice' },
          { id: 2, name: 'egg' },
        ],
      },
    });

    render(<PantryPage />);

    expect(await screen.findByText('rice')).toBeTruthy();
    expect(await screen.findByText('egg')).toBeTruthy();
  });

  it('supports add item flow and clicking Edit button', async () => {
    const user = userEvent.setup();
    setUserRole('ADMIN');
    api.get
      .mockResolvedValueOnce({
        data: {
          data: [{ id: 1, name: 'rice' }],
        },
      })
      .mockResolvedValueOnce({
        data: {
          data: [
            { id: 1, name: 'rice' },
            { id: 2, name: 'tomato' },
          ],
        },
      });
    api.post.mockResolvedValueOnce({ data: { data: { id: 2, name: 'tomato' } } });
    updatePantryItem.mockResolvedValueOnce({
      data: { data: { id: 1, name: 'brown rice' } },
    });

    render(<PantryPage />);

    expect(await screen.findByText('rice')).toBeTruthy();

    await user.type(screen.getByPlaceholderText(/search ingredient/i), 'tomato');
    await user.click(screen.getByRole('button', { name: /^add$/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/stock', { name: 'tomato' });
    });
    expect(await screen.findByText('tomato')).toBeTruthy();

    await user.click(screen.getAllByRole('button', { name: /^edit$/i })[0]);
    const editInput = screen.getByPlaceholderText(/update ingredient/i);
    expect(editInput).toBeTruthy();

    await user.clear(editInput);
    await user.type(editInput, 'brown rice');
    await user.click(screen.getByRole('button', { name: /^save$/i }));

    await waitFor(() => {
      expect(updatePantryItem).toHaveBeenCalledWith(1, { name: 'brown rice' });
    });
    expect(await screen.findByText('brown rice')).toBeTruthy();
  });

  it('shows Clear All only for ADMIN role', async () => {
    api.get.mockResolvedValue({
      data: { data: [] },
    });

    setUserRole('USER');
    const firstRender = render(<PantryPage />);
    await waitFor(() => expect(api.get).toHaveBeenCalled());
    expect(screen.queryByRole('button', { name: /clear all/i })).toBeNull();
    firstRender.unmount();

    vi.clearAllMocks();
    api.get.mockResolvedValue({
      data: { data: [] },
    });

    window.localStorage.clear();
    setUserRole('ADMIN');
    render(<PantryPage />);
    await waitFor(() => expect(api.get).toHaveBeenCalled());
    expect(screen.getByRole('button', { name: /clear all/i })).toBeTruthy();
  });
});
