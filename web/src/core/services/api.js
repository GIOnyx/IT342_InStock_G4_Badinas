import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT token to every request if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export function updatePantryItem(id, payload) {
  return api.put(`/stock/${id}`, payload);
}

export function updateCurrentUser(payload) {
  return api.put('/auth/me', payload);
}

export function changeCurrentPassword(payload) {
  return api.put('/auth/me/password', payload);
}

/**
 * Uploads a profile picture. Sends multipart/form-data with a single
 * field named "file". Axios automatically sets the correct Content-Type
 * boundary when passed a FormData object.
 */
export function uploadAvatar(file) {
  const form = new FormData();
  form.append('file', file);
  return api.post('/users/avatar', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export default api;
